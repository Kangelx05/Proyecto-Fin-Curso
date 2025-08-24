package api.security;

import api.domain.QrToken;
import api.repository.QrTokenRepository;
import api.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

/**
 * Interceptor responsible for enforcing token-based authentication and
 * authorisation across the API.  It examines the JWT present in the
 * {@code Authorization} header and validates it via {@link JwtService}.
 * Depending on the token type (USER or QR) it restricts access to
 * certain endpoints.  Requests to excluded paths (e.g. /auth/login and
 * /qr/*) bypass authentication.
 */
@Component
@RequiredArgsConstructor
public class TokenAuthenticationInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;
    private final QrTokenRepository qrTokenRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1) CORS preflight requests should bypass authentication entirely.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        String method = request.getMethod();
        // 2) Allow unauthenticated access to the login endpoint
        if (path.startsWith("/auth/login")) {
            return true;
        }
        // 3) Allow unauthenticated access to QR access endpoint (mobile clients
        // redeem a token here).  Only the GET method is allowed without
        // authentication.  Other /qr/* endpoints require a valid USER token.
        if ("GET".equalsIgnoreCase(method) && "/qr/access".equals(path)) {
            return true;
        }

        // 3b) Requiere autenticación para el stream SSE de pedidos de barra, pero permite el token en query param
        if ("GET".equalsIgnoreCase(method) && "/order-detail/bar-stream".equals(path)) {
            // Obtener el token del parámetro ?token= o del header Authorization
            String tokenParam = request.getParameter("token");
            String header = request.getHeader("Authorization");
            String token = null;
            if (tokenParam != null && !tokenParam.isEmpty()) {
                token = tokenParam;
            } else if (header != null && header.startsWith("Bearer ")) {
                token = header.substring(7);
            }
            if (token == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            java.util.Map<String, Object> claims;
            try {
                claims = jwtService.validateToken(token);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            String type = (String) claims.get("type");
            // Solo permitimos tokens de usuario para suscribirse al stream
            if ("USER".equals(type)) {
                return true;
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        // 4) All other requests must present an Authorization header with a
        // Bearer token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        String token = authHeader.substring(7);
        Map<String, Object> claims;
        try {
            claims = jwtService.validateToken(token);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        String type = (String) claims.get("type");
        if ("USER".equals(type)) {
            // Full access for authenticated users
            return true;
        }
        if ("QR".equals(type)) {
            // Extract required claims for QR token validation
            Object qrTokenIdObj = claims.get("qrTokenId");
            Object svObj = claims.get("sv");
            Object tokenFingerprintObj = claims.get("fingerprint");
            if (!(qrTokenIdObj instanceof Number) || !(svObj instanceof Number) || !(tokenFingerprintObj instanceof String)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            Integer qrTokenId = ((Number) qrTokenIdObj).intValue();
            Integer sessionVersion = ((Number) svObj).intValue();
            String tokenFingerprint = (String) tokenFingerprintObj;
            // Recompute fingerprint from the current request (IP + User-Agent)
            String ip = request.getRemoteAddr();
            String ua = request.getHeader("User-Agent");
            String fingerprintRaw = ip + (ua != null ? ua : "");
            String currentFingerprint = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(fingerprintRaw.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            // Validate that the fingerprint in the token matches the current
            // request fingerprint to avoid replay from another device
            if (!currentFingerprint.equals(tokenFingerprint)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            // Look up the QrToken in the database
            QrToken dbToken = qrTokenRepository.findById(qrTokenId).orElse(null);
            if (dbToken == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            // Ensure the token is currently in use
            if (dbToken.getStatus() != QrToken.Status.IN_USE) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            // Check expiration against database record
            if (dbToken.getExpiresAt() != null && dbToken.getExpiresAt().isBefore(LocalDateTime.now())) {
                // Mark as expired and deny access
                dbToken.setStatus(QrToken.Status.EXPIRED);
                qrTokenRepository.save(dbToken);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            // Check that the session version in the JWT matches the current DB
            // session version.  If not, the JWT is stale and must be rejected.
            if (dbToken.getSessionVersion() == null || !dbToken.getSessionVersion().equals(sessionVersion)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            // Ensure the DB record's usedBy matches the current fingerprint.
            if (dbToken.getUsedBy() != null && !dbToken.getUsedBy().equals(currentFingerprint)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
            // Restrict access for QR tokens to specific endpoints
            boolean allowed = false;
            // Allow GET requests to /product and its subpaths
            if (path.startsWith("/product") && "GET".equalsIgnoreCase(method)) {
                allowed = true;
            }
            // Allow GET requests to /cardProduct for QR tokens (mobile menu)
            if (path.startsWith("/cardProduct") && "GET".equalsIgnoreCase(method)) {
                allowed = true;
            }
            // Allow creation of orders
            if ("/order".equals(path) && "POST".equalsIgnoreCase(method)) {
                allowed = true;
            }
            // Allow creation of order details
            if ("/order-detail".equals(path) && "POST".equalsIgnoreCase(method)) {
                allowed = true;
            }
            if (!allowed) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
            return true;
        }
        // Unknown token type or missing claims results in unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}