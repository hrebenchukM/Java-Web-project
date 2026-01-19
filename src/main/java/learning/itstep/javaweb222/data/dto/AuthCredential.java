package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class AuthCredential {
private UUID id;
    private UUID userId;
    private String roleId;

    private String login;
    private String salt;
    private String passwordHash;

    private String authProvider;
    private String providerId;

    private Date createdAt;

    private User user;
    private UserRole userRole;
    
    
    
   // ---------- factory ----------

    public static AuthCredential fromResultSet(ResultSet rs) throws SQLException {
        AuthCredential ac = new AuthCredential();

        ac.setId(UUID.fromString(rs.getString("auth_id")));
        ac.setUserId(UUID.fromString(rs.getString("user_id")));
        ac.setRoleId(rs.getString("role_id"));

        ac.setLogin(rs.getString("login"));
        ac.setSalt(rs.getString("salt"));
        ac.setPasswordHash(rs.getString("password_hash"));

        ac.setAuthProvider(rs.getString("auth_provider"));
        ac.setProviderId(rs.getString("provider_id"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            ac.setCreatedAt(new Date(ts.getTime()));
        }

      
        try { ac.setUser(User.fromResultSet(rs)); }
        catch (Exception ignore) {}

        try { ac.setUserRole(UserRole.fromResultSet(rs)); }
        catch (Exception ignore) {}

        return ac;
    }

    public UUID getId() {
        return id;
    }

    public AuthCredential setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public AuthCredential  setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public String getRoleId() {
        return roleId;
    }

    public AuthCredential  setRoleId(String roleId) {
        this.roleId = roleId;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public AuthCredential  setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getSalt() {
        return salt;
    }

    public AuthCredential  setSalt(String salt) {
        this.salt = salt;
        return this;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public AuthCredential  setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        return this;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public AuthCredential  setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
        return this;
    }

    public String getProviderId() {
        return providerId;
    }

    public AuthCredential  setProviderId(String providerId) {
        this.providerId = providerId;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public AuthCredential  setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public User getUser() {
        return user;
    }

    public AuthCredential  setUser(User user) {
        this.user = user;
        return this;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public AuthCredential  setUserRole(UserRole userRole) {
        this.userRole = userRole;
        return this;
    }

    
    
    
}