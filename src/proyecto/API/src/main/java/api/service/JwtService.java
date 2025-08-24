package api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * A minimal JSON Web Token (JWT) implementation for issuing and validating
 * session tokens.  This class generates a token with HMAC-SHA256 using a
 * symmetric secret key and embeds simple claims such as table identifier
 * and device fingerprint.  The {@code validateToken} method verifies the
 * signature and expiration time before returning the decoded claims map.
 */
@Service
public class JwtService {
    /**
     * Secret key used for signing tokens.  In a real application this
     * value should be externalised and rotated regularly.
     */
    private static final String SECRET_KEY = "very-secret-key-change-me";

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Generate a JWT containing the given table identifier and device
     * fingerprint.  The token is valid for two hours from the time of
     * generation.
     *
     * @param tableId     the restaurant table identifier
     * @param fingerprint a unique fingerprint for the client device
     * @return a signed JWT string
     *
     * @deprecated Use {@link #generateQrSessionToken(Integer, Integer, String, Integer)}
     *             to include additional claims such as the QR token ID and
     *             session version.  This method remains for backwards
     *             compatibility but will be removed in the future.
     */
    @Deprecated
    public String generateToken(@NotNull Integer tableId, @NotNull String fingerprint) {
        // Delegate to the newer implementation with dummy identifiers.  We
        // pass null values for qrTokenId and sessionVersion which should
        // not be used for QR tokens generated via this legacy method.
        return generateQrSessionToken(null, tableId, fingerprint, null);
    }

    /**
     * Generate a QR session token (JWT) tied to a specific database record.
     * The resulting token embeds the QR token identifier and session
     * version so that the backend can later validate that the underlying
     * {@code QrToken} has not been expired, removed or had its session
     * invalidated.  The token is valid for two hours.
     *
     * @param qrTokenId      the identifier of the {@link api.domain.QrToken}
     * @param tableId        the restaurant table identifier
     * @param fingerprint    a unique fingerprint for the client device
     * @param sessionVersion the current session version of the QR token
     * @return a signed JWT string containing the provided claims
     */
    public String generateQrSessionToken(Integer qrTokenId,
                                         Integer tableId,
                                         String fingerprint,
                                         Integer sessionVersion) {
        try {
            // Build header
            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            // Build payload with additional claims
            Map<String, Object> payload = new HashMap<>();
            payload.put("qrTokenId", qrTokenId);
            payload.put("tableId", tableId);
            payload.put("fingerprint", fingerprint);
            payload.put("sv", sessionVersion);
            payload.put("type", "QR");
            long expirationMillis = System.currentTimeMillis() + (2L * 60L * 60L * 1000L); // 2 hours
            payload.put("exp", expirationMillis);
            // Encode to Base64URL strings
            String headerJson = objectMapper.writeValueAsString(header);
            String payloadJson = objectMapper.writeValueAsString(payload);
            String headerBase64 = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
            String payloadBase64 = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
            // Compute signature
            String signature = sign(headerBase64 + "." + payloadBase64);
            // Concatenate parts
            return headerBase64 + "." + payloadBase64 + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    /**
     * Generate a JWT for authenticated users.  The resulting token contains
     * the user identifier and a claim indicating that the token is of type
     * USER.  It is valid for two hours.
     *
     * @param userId the identifier of the authenticated user
     * @return a signed JWT string
     */
    public String generateUserToken(@NotNull Integer userId) {
        try {
            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            Map<String, Object> payload = new HashMap<>();
            payload.put("userId", userId);
            payload.put("type", "USER");
            long expirationMillis = System.currentTimeMillis() + (2L * 60L * 60L * 1000L);
            payload.put("exp", expirationMillis);
            String headerJson = objectMapper.writeValueAsString(header);
            String payloadJson = objectMapper.writeValueAsString(payload);
            String headerBase64 = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
            String payloadBase64 = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
            String signature = sign(headerBase64 + "." + payloadBase64);
            return headerBase64 + "." + payloadBase64 + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    /**
     * Validate a JWT and return the decoded payload claims.  The method
     * verifies the signature and expiration timestamp.  If the token is
     * invalid or expired an exception is thrown.
     *
     * @param token the JWT string to validate
     * @return a map of claims extracted from the token payload
     */
    public Map<String, Object> validateToken(@NotNull String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT format");
            }
            String header = parts[0];
            String payload = parts[1];
            String signature = parts[2];
            // Verify signature
            String computedSignature = sign(header + "." + payload);
            if (!constantTimeEquals(signature, computedSignature)) {
                throw new IllegalArgumentException("Invalid JWT signature");
            }
            // Decode payload JSON into a map
            String payloadJson = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
            Map<String, Object> claims = objectMapper.readValue(payloadJson, new TypeReference<Map<String, Object>>() {});
            // Check expiration
            Object expValue = claims.get("exp");
            if (expValue instanceof Number) {
                long expMillis = ((Number) expValue).longValue();
                if (System.currentTimeMillis() > expMillis) {
                    throw new IllegalArgumentException("JWT has expired");
                }
            }
            return claims;
        } catch (Exception e) {
            throw new RuntimeException("Error validating JWT", e);
        }
    }

    /**
     * Compute an HMAC-SHA256 signature for the provided data using the
     * configured secret key.
     *
     * @param data the data to sign
     * @return a Base64URL-encoded signature
     */
    private String sign(String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] signatureBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
    }

    /**
     * Perform a constant-time string comparison to avoid timing attacks.
     */
    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}