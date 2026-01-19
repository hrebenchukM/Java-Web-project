package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class AccessToken {
 private UUID tokenId;                 // token_id
    private UUID authCredentialId;         // auth_credential_id
    private UUID userId;                   // user_id
    private String roleId;                 // role_id

    private Date issuedAt;
    private Date expiredAt;
    private Date revokedAt;

    private String userAgent;
    private String ipAddress;

    // optional joined objects
    private AuthCredential authCredential;
    private User user;
    private UserRole userRole;

    // ---------- factory ----------

    public static AccessToken fromResultSet(ResultSet rs) throws SQLException {
        AccessToken t = new AccessToken();

        t.setTokenId(UUID.fromString(rs.getString("token_id")));
        t.setAuthCredentialId(UUID.fromString(rs.getString("auth_credential_id")));
        t.setUserId(UUID.fromString(rs.getString("user_id")));
        t.setRoleId(rs.getString("role_id"));

        Timestamp ts;

        ts = rs.getTimestamp("issued_at");
        if (ts != null) t.setIssuedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("expired_at");
        if (ts != null) t.setExpiredAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("revoked_at");
        if (ts != null) t.setRevokedAt(new Date(ts.getTime()));

        t.setUserAgent(rs.getString("user_agent"));
        t.setIpAddress(rs.getString("ip_address"));

        // JOIN-safe parsing (как у препода)
        try { t.setAuthCredential(AuthCredential.fromResultSet(rs)); }
        catch (Exception ignore) {}

        try { t.setUser(User.fromResultSet(rs)); }
        catch (Exception ignore) {}

        try { t.setUserRole(UserRole.fromResultSet(rs)); }
        catch (Exception ignore) {}

        return t;
    }

    public UUID getTokenId() {
        return tokenId;
    }

    public AccessToken setTokenId(UUID tokenId) {
        this.tokenId = tokenId;
         return this;
    }

    public UUID getAuthCredentialId() {
        return authCredentialId;
    }

    public AccessToken setAuthCredentialId(UUID authCredentialId) {
        this.authCredentialId = authCredentialId;
         return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public AccessToken setUserId(UUID userId) {
        this.userId = userId;
         return this;
    }

    public String getRoleId() {
        return roleId;
    }

    public AccessToken setRoleId(String roleId) {
        this.roleId = roleId;
         return this;
    }

    public Date getIssuedAt() {
        return issuedAt;
    }

    public AccessToken setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;
         return this;
    }

    public Date getExpiredAt() {
        return expiredAt;
    }

    public AccessToken setExpiredAt(Date expiredAt) {
        this.expiredAt = expiredAt;
         return this;
    }

    public Date getRevokedAt() {
        return revokedAt;
    }

    public AccessToken setRevokedAt(Date revokedAt) {
        this.revokedAt = revokedAt;
         return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public AccessToken setUserAgent(String userAgent) {
        this.userAgent = userAgent;
         return this;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public AccessToken setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
         return this;
    }

    public AuthCredential getAuthCredential() {
        return authCredential;
    }

    public AccessToken setAuthCredential(AuthCredential authCredential) {
        this.authCredential = authCredential;
         return this;
    }

    public User getUser() {
        return user;
    }

    public AccessToken setUser(User user) {
        this.user = user;
         return this;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public AccessToken setUserRole(UserRole userRole) {
        this.userRole = userRole;
        return this;
    }
    
    
}
/*
"token_id,user_access_id,issued_at,expired_at     DATETIME NULL"
*/