
package learning.itstep.javaweb222.data.user;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import learning.itstep.javaweb222.data.core.DbProvider;
import learning.itstep.javaweb222.data.dto.AccessToken;
import learning.itstep.javaweb222.data.dto.User;
import learning.itstep.javaweb222.data.dto.AuthCredential;
import learning.itstep.javaweb222.services.kdf.KdfService;


@Singleton
public class UserDao {
    private final DbProvider db;
    private final Logger logger;
    private final KdfService kdfService;

    @Inject
    public UserDao(DbProvider db, Logger logger, KdfService kdfService) {
        this.db = db;
        this.logger = logger;
        this.kdfService = kdfService;
    }

    
    // -------------------- Users --------------------

    public User getUserById(String userId) {
        String sql = "SELECT * FROM users WHERE user_id = ? AND deleted_at IS NULL";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return User.fromResultSet(rs);
            }
        }
        catch (Exception ex) {
            logger.log(Level.WARNING, "UserDao::getUserById {0}",
                    ex.getMessage() + " | " + sql);
        }
        return null;
    }

    
    
    // -------------------- Auth --------------------

    /**
     * Аутентификация по логину и паролю
     */
    public AuthCredential getUserAccessByCredentials(String login, String password) {

        String sql = """
            SELECT ac.*, u.*
            FROM auth_credentials ac
            JOIN users u ON ac.user_id = u.user_id
            WHERE ac.login = ?
        """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                AuthCredential ua = AuthCredential.fromResultSet(rs);

                String calculatedHash =
                        kdfService.dk(password, ua.getSalt());

                if (calculatedHash.equals(ua.getPasswordHash())) {
                    return ua;
                }
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "UserDao::getUserAccessByCredentials {0}",
                    ex.getMessage() + " | " + sql);
        }
        return null;
    }
    
    
    
    /**
     * Проверка наличия роли у пользователя
     */
    public AuthCredential getUserAccess(String userId, String roleId) {

        String sql = """
            SELECT ac.*, u.*, ur.*
            FROM auth_credentials ac
            JOIN users u ON ac.user_id = u.user_id
            JOIN user_roles ur ON ac.role_id = ur.role_id
            WHERE ac.user_id = ? AND ac.role_id = ?
        """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, roleId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return AuthCredential.fromResultSet(rs);
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "UserDao::getUserAccess {0}",
                    ex.getMessage() + " | " + sql);
        }
        return null;
    }

    
    // -------------------- Tokens --------------------

    /**
     * Создание access token (JWT будет строиться поверх)
     */
    public AccessToken createAccessToken(AuthCredential ua, String userAgent, String ip) {

        AccessToken token = new AccessToken();
        token.setTokenId(db.getDbIdentity());
        token.setUserId(ua.getUserId());
        token.setRoleId(ua.getRoleId());
        token.setIssuedAt(new Date());
        token.setExpiredAt(
                new Date(token.getIssuedAt().getTime() + 1000 * 60 * 10)
        );

        String sql = """
            INSERT INTO access_tokens
            (token_id, auth_credential_id, user_id, role_id,
             issued_at, expired_at, user_agent, ip_address)
            VALUES (?,?,?,?,?,?,?,?)
        """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, token.getTokenId().toString());
            ps.setString(2, ua.getId().toString());
            ps.setString(3, ua.getUserId().toString());
            ps.setString(4, ua.getRoleId());
            ps.setTimestamp(5, new Timestamp(token.getIssuedAt().getTime()));
            ps.setTimestamp(6, new Timestamp(token.getExpiredAt().getTime()));
            ps.setString(7, userAgent);
            ps.setString(8, ip);

            ps.executeUpdate();
            return token;
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "UserDao::createAccessToken {0}",
                    ex.getMessage() + " | " + sql);
            return null;
        }
    }
    
}
