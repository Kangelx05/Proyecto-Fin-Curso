package api.controllers;

import api.domain.QrToken;
import api.dto.QrTokenResponse;
import api.mappers.QrTokenMapper;
import api.service.QrAccessService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

/**
 * REST controller exposing endpoints for generating and redeeming QR codes.  It
 * delegates business logic to {@link QrAccessService} and handles mapping
 * between HTTP requests and response DTOs.
 */
@RestController
@RequestMapping("/qr")
@RequiredArgsConstructor
public class QrAccessController {

    private final QrAccessService qrAccessService;

    /**
     * Generate a new QR token for the given table.  Optionally a validity
     * period in minutes can be provided; if omitted the default is 15
     * minutes.
     *
     * @param tableId the table identifier
     * @param minutes the validity period in minutes (optional)
     * @return a {@link QrTokenResponse} representing the new token
     * @throws Exception if the table cannot be found
     */
    @GetMapping("/generate")
    public QrTokenResponse generate(@RequestParam("tableId") int tableId,
                                    @RequestParam(value = "minutes", defaultValue = "15") int minutes) throws Exception {
        Duration validity = Duration.ofMinutes(minutes);
        QrToken qrToken = qrAccessService.generateQrToken(tableId, validity);
        return QrTokenMapper.toResponse(qrToken);
    }

    /**
     * Redeem a QR token scanned from a mobile device.  The token string is
     * supplied via the {@code t} query parameter.  The caller's IP and
     * user-agent are combined to derive a simple fingerprint used to
     * restrict subsequent access to the same device.
     *
     * @param token   the raw token string from the QR code
     * @param request the HTTP servlet request used to derive the fingerprint
     * @return a session token (JWT) granting continued access
     * @throws Exception if the token is invalid, expired or already used
     */
    @GetMapping("/access")
    public String access(@RequestParam("t") String token, HttpServletRequest request) throws Exception {
        String ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");
        // Derive a simple fingerprint by concatenating IP and user-agent and
        // Base64-encoding the result.  In a real system a more robust
        // fingerprinting technique should be used.
        String fingerprintRaw = ip + (ua != null ? ua : "");
        String fingerprint = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(fingerprintRaw.getBytes(StandardCharsets.UTF_8));
        return qrAccessService.validateQrAccess(token, fingerprint);
    }

    /**
     * Retrieve the current QR token associated with a given table.  This endpoint allows
     * clients to query the API for the token string of a specific table without generating
     * a new token.  If no token exists for the specified table, a 404-style exception is
     * thrown by the service.
     *
     * @param tableId the identifier of the table whose token is requested
     * @return a {@link QrTokenResponse} containing the token and related information
     * @throws Exception if no token exists for the given table
     */
    @GetMapping("/token")
    public QrTokenResponse getToken(@RequestParam("tableId") int tableId) throws Exception {
        QrToken qrToken = qrAccessService.getQrTokenByTableId(tableId);
        return QrTokenMapper.toResponse(qrToken);
    }
}