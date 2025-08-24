package api.repository;

import api.domain.QrToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository for {@link QrToken} entities.  Provides standard CRUD
 * operations along with a lookup by token string.
 */
public interface QrTokenRepository extends JpaRepository<QrToken, Integer> {
    /**
     * Retrieve a QR token by its unique token string.
     *
     * @param token the token string
     * @return an optional containing the matching {@link QrToken}, or empty
     *         if no such token exists
     */
    Optional<QrToken> findByToken(String token);

    /**
     * Find the QR token associated with a given table ID.  With the unique
     * constraint on the table_id column there will be at most one result.
     *
     * @param tableId the identifier of the table whose token should be retrieved
     * @return an optional containing the token, or empty if none exists
     */
    Optional<QrToken> findByTable_Id(Integer tableId);

    /**
     * Delete all QR tokens associated with the given table ID.  This method
     * allows regeneration of a token by removing any existing entry for the
     * specified table before creating a new one.  It is intentionally void
     * because the number of deleted rows is not needed by callers.
     *
     * @param tableId the identifier of the table whose token(s) should be deleted
     */
    @Modifying
    @Query("DELETE FROM QrToken qt WHERE qt.table.id = :tableId")
    void deleteByTableId(@Param("tableId") Integer tableId);
}