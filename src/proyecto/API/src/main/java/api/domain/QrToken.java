package api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Entity representing a QR token used to access the restaurant ordering system.
 *
 * <p>
 * Each token is tied to a specific table and can only be used once.  The
 * {@code status} field tracks whether the token has been consumed.  When the
 * token is first accessed the associated device's fingerprint is stored in
 * {@code usedBy} and the status changes to {@code IN_USE}.  If the token
 * expires or is invalidated its status becomes {@code EXPIRED}.
 */
@Entity
@jakarta.persistence.Table(
        name = "qr_tokens",
        // Enforce a unique constraint on the foreign key column to guarantee
        // at most one token per table at the database level.  This prevents
        // accidental duplication if concurrent requests race to create tokens.
        uniqueConstraints = @UniqueConstraint(columnNames = "table_id")
)
public class QrToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "table_id", nullable = false)
    private Table table;

    /**
     * Fingerprint of the first device that accessed this token.  This value
     * will remain {@code null} until the token is consumed.
     */
    @Column(name = "used_by")
    private String usedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Version of the session associated with this QR token.  Each time the
     * version is incremented, any JWTs previously issued for this token
     * become invalid.  Defaults to {@code 1} when the entity is first
     * persisted.
     */
    @Column(name = "session_version", nullable = false)
    private Integer sessionVersion;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Enumeration of the possible token states.
     */
    public enum Status {
        UNUSED,
        IN_USE,
        EXPIRED
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        // Initialise sessionVersion if it has not been set.  Using 1 as
        // the starting value makes it straightforward to invalidate
        // existing session tokens by simply incrementing this field.
        if (this.sessionVersion == null) {
            this.sessionVersion = 1;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public String getUsedBy() {
        return usedBy;
    }

    public void setUsedBy(String usedBy) {
        this.usedBy = usedBy;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getSessionVersion() {
        return sessionVersion;
    }

    public void setSessionVersion(Integer sessionVersion) {
        this.sessionVersion = sessionVersion;
    }
}