
package learning.itstep.javaweb222.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import learning.itstep.javaweb222.data.dto.User;
import learning.itstep.javaweb222.data.dto.UserAccesss;
import learning.itstep.javaweb222.services.config.ConfigService;
import learning.itstep.javaweb222.services.kdf.KdfService;

@Singleton
public class DateAccessor {
    private final ConfigService configService;
    private  final Logger logger;
    private final KdfService kdfService;
    private Connection connection;
    private Driver mysqlDriver;
    @Inject
    public DateAccessor(ConfigService configService,Logger logger, KdfService kdfService) {
        this.configService = configService;
        this.logger = logger;
        this.kdfService=kdfService;
    }
    
    public User getUserByCredentials(String login,String password){
      String sql = "SELECT * FROM user_accesses ua JOIN users u ON ua.user_id = u.id WHERE ua.login = ?";
  
      try(PreparedStatement prep = this.getConnection().prepareStatement(sql)) {
            prep.setString(1, login);
            ResultSet rs = prep.executeQuery();
            if(rs.next()){
            UserAccesss userAccess = UserAccesss.fromResultSet(rs);
             if(kdfService.dk(password, userAccess.getSalt())
                    .equals(userAccess.getDk())){
             }
            }
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::getUserByCredentials " 
                    + ex.getMessage() + " | " + sql);
        } 
        return null;
    }
    Connection getConnection() throws SQLException{
    if(this.connection==null){
        String connectionString;
        try{
            connectionString = configService.get("connectionStrings.mainDb");
        }
        catch(NoSuchFieldError err)
        {
            throw new RuntimeException(
                    "DateAccessor::getConnection Connection string not found 'connectionStrings.mainDb'"
                     +err.getMessage());
        }
        try{
        mysqlDriver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(mysqlDriver);
            connection = DriverManager.getConnection(connectionString);
        }
        catch(SQLException ex){
                   throw new RuntimeException(
                    "DateAccessor::getConnection Connection not opened 'connectionStrings.mainDb'"
                     +ex.getMessage());
        }
    }
    return this.connection;
    }
    
    public UUID getDbIdentity()
    {
         String sql = "SELECT UUID()";
        
      try (Statement statement = this.getConnection().createStatement())
      {
       ResultSet rs = statement.executeQuery(sql);
       rs.next();
       return UUID.fromString(rs.getString(1));
        
      }
      catch(SQLException ex)
      {
       logger.log(Level.WARNING,
        "DateAccessor::getDbIdentity " + ex.getMessage() + " | " + sql);
      }
    return null;
    }
    
    public LocalDateTime getDbTime() 
    {
      String sql = "SELECT NOW()";
      try (Statement statement = this.getConnection().createStatement()) 
      {
        ResultSet rs = statement.executeQuery(sql);
        rs.next();
        Timestamp ts = rs.getTimestamp(1);
        return ts.toLocalDateTime();
      }
      catch (SQLException ex) 
      {
        logger.log(Level.WARNING,
            "DateAccessor::getDbTime " + ex.getMessage() + " | " + sql);
      }
    return null;
    }
    
    
     public boolean install() {
        String sql = "CREATE TABLE  IF NOT EXISTS  users("
                + "id            CHAR(36)     PRIMARY KEY,"
                + "name          VARCHAR(64)  NOT NULL,"
                + "email         VARCHAR(128) NOT NULL,"
                + "birthdate     DATETIME     NULL,"
                + "registered_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "deleted_at    DATETIME     NULL"
                + ")ENGINE = INNODB, "
                + " DEFAULT CHARSET = utf8mb4, "
                + " COLLATE utf8mb4_unicode_ci";
        try(Statement statement = this.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::install " 
                    + ex.getMessage() + " | " + sql);
            return false;
        }
        
        sql = "CREATE TABLE  IF NOT EXISTS  user_accesses("
                + "id      CHAR(36)    PRIMARY KEY,"
                + "user_id CHAR(36)    NOT NULL,"
                + "role_id VARCHAR(16) NOT NULL,"
                + "login   VARCHAR(32) NOT NULL,"
                + "salt    CHAR(16)    NOT NULL,"
                + "dk      CHAR(32)    NOT NULL,"
                + "UNIQUE(login)"
                + ")ENGINE = INNODB, "
                + " DEFAULT CHARSET = utf8mb4, "
                + " COLLATE utf8mb4_unicode_ci";
        try(Statement statement = this.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::install " 
                    + ex.getMessage() + " | " + sql);
            return false;
        }
        
        
        sql = "CREATE TABLE  IF NOT EXISTS  user_roles("
                + "id          VARCHAR(16)  PRIMARY KEY,"
                + "description VARCHAR(256) NOT NULL,"
                + "can_create  TINYINT      NOT NULL DEFAULT 0,"
                + "can_read    TINYINT      NOT NULL DEFAULT 0,"
                + "can_update  TINYINT      NOT NULL DEFAULT 0,"
                + "can_delete  TINYINT      NOT NULL DEFAULT 0"
                + ")ENGINE = INNODB, "
                + " DEFAULT CHARSET = utf8mb4, "
                + " COLLATE utf8mb4_unicode_ci";
        try(Statement statement = this.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::install " 
                    + ex.getMessage() + " | " + sql);
            return false;
        }
        
        sql = "CREATE TABLE  IF NOT EXISTS  tokens("
                + "id             CHAR(36) PRIMARY KEY,"
                + "user_access_id CHAR(36) NOT NULL,"
                + "issued_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "expired_at     DATETIME NULL"
                + ")ENGINE = INNODB, "
                + " DEFAULT CHARSET = utf8mb4, "
                + " COLLATE utf8mb4_unicode_ci";
        try(Statement statement = this.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::install " 
                    + ex.getMessage() + " | " + sql);
            return false;
        }
        
        return true;
    }
    
    public boolean seed() {
        String sql = "INSERT INTO user_roles(id, description, can_create, "
                + "can_read, can_update, can_delete)"
                + "VALUES('admin', 'Root Administrator', 1, 1, 1, 1) "
                + "ON DUPLICATE KEY UPDATE "
                + "description = VALUES(description),"
                + "can_create  = VALUES(can_create),"
                + "can_read    = VALUES(can_read),"
                + "can_update  = VALUES(can_update),"
                + "can_delete  = VALUES(can_delete)";
        try(Statement statement = this.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::seed " 
                    + ex.getMessage() + " | " + sql);
            return false;
        }
        sql = "INSERT INTO user_roles(id, description, can_create, "
                + "can_read, can_update, can_delete)"
                + "VALUES('guest', 'Self Registered User', 0, 0, 0, 0) "
                + "ON DUPLICATE KEY UPDATE "
                + "description = VALUES(description),"
                + "can_create  = VALUES(can_create),"
                + "can_read    = VALUES(can_read),"
                + "can_update  = VALUES(can_update),"
                + "can_delete  = VALUES(can_delete)";
        try(Statement statement = this.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::seed " 
                    + ex.getMessage() + " | " + sql);
            return false;
        }
        
        
         sql = "INSERT INTO users(id, name,email)"
                + "VALUES('71446a1c-9851-11f0-a9d8-00410e7ae988',"
                 + " 'Default Administrator', 'admin@localghost') "
                + "ON DUPLICATE KEY UPDATE "
                + "name = VALUES(name),"
                + "email  = VALUES(email)";
        try(Statement statement = this.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::seed " 
                    + ex.getMessage() + " | " + sql);
            return false;
        }
        
        
         sql = "INSERT INTO user_accesses(id, user_id, role_id, login, salt, dk) "
            + "VALUES('71446a1c-9851-11f0-a9d8-00410e7ae988', "
            + "'3fa29bc1-9852-11f0-a9d8-00410e7ae988', "
            + "'admin', "
            + "'admin', "
            + "'admin', "
            + "'" + kdfService.dk("admin", "admin") + "') "
            + "ON DUPLICATE KEY UPDATE "
            + "user_id = VALUES(user_id), "
            + "role_id = VALUES(role_id), "
            + "login = VALUES(login), "
            + "salt = VALUES(salt), "
            + "dk = VALUES(dk)";

        try(Statement statement = this.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::seed " 
                    + ex.getMessage() + " | " + sql);
            return false;
        }
        
        return true;
    }
    
    
    
}
