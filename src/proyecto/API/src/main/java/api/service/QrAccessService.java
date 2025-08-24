package api.service;

import api.domain.Order;
import api.domain.QrToken;
import api.domain.Table;
import api.domain.QrToken.Status;
import api.repository.OrderRepository;
import api.repository.QrTokenRepository;
import api.repository.TableRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

/**
 * Service responsible for generating QR tokens and validating access to
 * tables via those tokens.  Tokens are created with an expiry time and
 * tracked through their lifecycle states.  When a token is redeemed
 * successfully, a session JWT is issued to the client.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class QrAccessService {

    private final QrTokenRepository qrTokenRepository;
    private final TableRepository tableRepository;
    private final JwtService jwtService;
    private final OrderRepository orderRepository;

    /**
     * Generate a new QR token for the specified table.  The token is
     * initialised in the UNUSED state with a configured validity period.
     *
     * @param tableId the identifier of the table for which to generate a token
     * @param validity the duration for which the token is valid
     * @return the created {@link QrToken}
     * @throws Exception if the table cannot be found
     */
    public QrToken generateQrToken(@NotNull Integer tableId, @NotNull Duration validity) throws Exception {
        Table table = tableRepository.findTableById(tableId);
        if (table == null) {
            throw new Exception("Table not found: " + tableId);
        }
        // Before generating a new token, remove any existing token(s) for this table.  This
        // enforces a one-token-per-table policy and ensures that a fresh token is always
        // created when requested.  The repository method name reflects the joined column
        // "table_id" from the QrToken entity.  The delete operation is part of the same
        // transaction, so either both delete and create succeed, or both fail.
        // Explicitly delete any token linked to this table.  This uses a custom JPQL
        // query defined in the repository to ensure the correct rows are removed.
        qrTokenRepository.deleteByTableId(tableId);

        QrToken qrToken = new QrToken();
        qrToken.setToken(UUID.randomUUID().toString());
        qrToken.setTable(table);
        qrToken.setStatus(Status.UNUSED);
        qrToken.setExpiresAt(LocalDateTime.now().plus(validity));
        qrToken.setUsedBy(null);
        // Initialise the session version to 1 so that any JWTs issued for this
        // token can later be invalidated by incrementing this value.  A
        // non-null sessionVersion is also required by the entity mapping.
        qrToken.setSessionVersion(1);

        // Cuando se genera un QR para una mesa, si la mesa está libre o en uso, se marca como "BUSSY".
        String currentState = table.getState();
        if (currentState == null || currentState.isBlank() || "FREE".equalsIgnoreCase(currentState) || "IN_SERVICE".equalsIgnoreCase(currentState)) {
            table.setState("BUSSY");
            tableRepository.save(table);
        }

        return qrTokenRepository.save(qrToken);
    }

    /**
     * Validate a QR code scan attempt.  This method enforces single-use by
     * allowing only the first device to claim the token and sets the token
     * state to IN_USE.  Upon successful validation a JWT is returned to
     * establish a session.
     *
     * @param tokenString the raw token from the QR code
     * @param fingerprint a fingerprint string uniquely identifying the client
     * @return a JWT representing the authorised session
     * @throws Exception if the token is invalid, expired or already in use
     */
    public String validateQrAccess(@NotNull String tokenString, @NotNull String fingerprint) throws Exception {
        Optional<QrToken> optional = qrTokenRepository.findByToken(tokenString);
        if (optional.isEmpty()) {
            throw new Exception("Invalid QR token");
        }
        QrToken qrToken = optional.get();
        // Always check expiration first.  If the token has expired, mark it
        // accordingly and refuse access.
        if (qrToken.getExpiresAt() != null && qrToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            qrToken.setStatus(Status.EXPIRED);
            qrTokenRepository.save(qrToken);
            throw new Exception("QR token has expired");
        }
        // Behaviour depends on the current status of the QR token
        if (qrToken.getStatus() == Status.UNUSED) {
            // First device to redeem this token.  Store the fingerprint,
            // update status and issue a JWT tied to this DB record.
            qrToken.setUsedBy(fingerprint);
            qrToken.setStatus(Status.IN_USE);
            qrTokenRepository.save(qrToken);
            return jwtService.generateQrSessionToken(
                    qrToken.getId(),
                    qrToken.getTable().getId(),
                    fingerprint,
                    qrToken.getSessionVersion()
            );
        } else if (qrToken.getStatus() == Status.IN_USE) {
            // Token already claimed.  Allow reentry only from the same
            // fingerprint while the DB record remains valid and has not
            // expired.  Do not change the DB record here: we simply
            // reissue a JWT using the current session version.
            if (qrToken.getUsedBy() != null && qrToken.getUsedBy().equals(fingerprint)) {
                return jwtService.generateQrSessionToken(
                        qrToken.getId(),
                        qrToken.getTable().getId(),
                        fingerprint,
                        qrToken.getSessionVersion()
                );
            } else {
                throw new Exception("QR token already used by another device");
            }
        } else {
            // EXPIRED or any other state results in rejection
            throw new Exception("QR token is no longer valid");
        }
    }

    /**
     * Retrieve the current QR token for a given table.  There should be at most one token per
     * table due to the unique constraint on the table_id column.  If no token exists, an exception
     * is thrown so that the controller can return an appropriate response.
     *
     * @param tableId the identifier of the table whose token is requested
     * @return the {@link QrToken} associated with the table
     * @throws Exception if no token exists for the given table
     */
    public QrToken getQrTokenByTableId(@NotNull Integer tableId) throws Exception {
        Optional<QrToken> optional = qrTokenRepository.findByTable_Id(tableId);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new Exception("QR token not found for table: " + tableId);
        }
    }
}