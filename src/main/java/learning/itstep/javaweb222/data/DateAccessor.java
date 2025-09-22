
package learning.itstep.javaweb222.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import learning.itstep.javaweb222.services.config.ConfigService;

@Singleton
public class DateAccessor {
    private final ConfigService configService;
    private  final Logger logger;
    private Connection connection;
    private Driver mysqlDriver;
    @Inject
    public DateAccessor(ConfigService configService,Logger logger) {
        this.configService = configService;
        this.logger = logger;
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
}
