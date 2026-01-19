
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
import learning.itstep.javaweb222.data.dto.UserAccess;
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

     public User getUserById(String userId) {
        String sql = "SELECT * FROM users u WHERE u.user_id = ?";
        try(PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, userId);
            ResultSet rs = prep.executeQuery();
            if(rs.next()) {
                return User.fromResultSet(rs);                
            }
        }
        catch(Exception ex) {
            logger.log(Level.WARNING, "DataAccessor::getUserById {0}", 
                    ex.getMessage() + " | " + sql);
        }
        return null;
    }
     
        public UserAccess getUserAccess(String userId, String roleId) {
        // перевіряємо чи є UserAccess із зазначеними даними
        String sql = "SELECT * FROM user_accesses ua "
                + "JOIN users u ON ua.user_id = u.user_id "
                + "JOIN user_roles ur ON ua.role_id = ur.role_id "
                + "WHERE u.user_id = ? AND ur.role_id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, roleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return UserAccess.fromResultSet(rs);
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::getUserAccess {0}",
                    ex.getMessage() + " | " + sql);
        }
        return null;
    }
    
   
    public UserAccess getUserAccessByCredentials(String login, String password) {
        String sql = """
            SELECT 
               *
            FROM 
               user_accesses ua 
               JOIN users u ON ua.user_id = u.user_id 
            WHERE ua.login = ?""";
        try(PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, login);
            ResultSet rs = prep.executeQuery();
            if(rs.next()) {
                UserAccess userAccess = UserAccess.fromResultSet(rs);
                if(kdfService.dk(password, userAccess.getSalt())
                        .equals(userAccess.getDk())) {
                    return userAccess;
                }
            }
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::getUserByCredentials {0}", 
                    ex.getMessage() + " | " + sql);
        }
        return null;
    }
    
 
       
    public AccessToken getTokenByUserAccess(UserAccess ua) {
        AccessToken at = new AccessToken();
        at.setTokenId(UUID.randomUUID());
        at.setIssuedAt(new Date());
        at.setExpiredAt( new Date( at.getIssuedAt().getTime() + 1000 * 60 * 10 ) );
        at.setUserAccessId(ua.getId());
        at.setUserAccess(ua);
  
        String sql = """
                INSERT INTO tokens(token_id,user_access_id,issued_at,expired_at)
                VALUES(?,?,?,?)
        """;
        try(PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, at.getTokenId().toString());
            prep.setString(2, at.getUserAccessId().toString());
            prep.setTimestamp(3, new Timestamp(at.getIssuedAt().getTime()));
            prep.setTimestamp(4, new Timestamp(at.getExpiredAt().getTime()));
            prep.executeUpdate();
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::getTokenByUserAccess {0}", 
                    ex.getMessage() + " | " + sql);
        }
        return at;
    }
}
