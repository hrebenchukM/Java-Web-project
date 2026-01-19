
package learning.itstep.javaweb222.data.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import learning.itstep.javaweb222.services.kdf.KdfService;

@Singleton
public class DbSeeder {

    private final DbProvider db;
    private final Logger logger;
    private final KdfService kdf;

    @Inject
    public DbSeeder(DbProvider db, Logger logger, KdfService kdf) {
        this.db = db;
        this.logger = logger;
        this.kdf = kdf;
    }
    public boolean seed() {
        String sql = "INSERT INTO user_roles(role_id, description, can_create, "
                + "can_read, can_update, can_delete)"
                + "VALUES('admin', 'Root Administrator', 1, 1, 1, 1) "
                + "ON DUPLICATE KEY UPDATE "
                + "description = VALUES(description),"
                + "can_create  = VALUES(can_create),"
                + "can_read    = VALUES(can_read),"
                + "can_update  = VALUES(can_update),"
                + "can_delete  = VALUES(can_delete)";
        try(Statement statement = db.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DbSeeder::seed {0}",
                    ex.getMessage() + " | " + sql);

            return false;
        }
        sql = "INSERT INTO user_roles(role_id, description, can_create, "
                + "can_read, can_update, can_delete)"
                + "VALUES('guest', 'Self Registered User', 0, 0, 0, 0) "
                + "ON DUPLICATE KEY UPDATE "
                + "description = VALUES(description),"
                + "can_create  = VALUES(can_create),"
                + "can_read    = VALUES(can_read),"
                + "can_update  = VALUES(can_update),"
                + "can_delete  = VALUES(can_delete)";
        try(Statement statement = db.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DbSeeder::seed {0}",
                    ex.getMessage() + " | " + sql);

            return false;
        }
        
        sql = "INSERT INTO users(user_id, name, email)"
                + "VALUES('69231c55-9851-11f0-b1b7-62517600596c', "
                + "'Default Administrator', 'admin@localhost') "
                + "ON DUPLICATE KEY UPDATE "
                + "name  = VALUES(name),"
                + "email = VALUES(email)";
        try(Statement statement = db.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DbSeeder::seed {0}",
                    ex.getMessage() + " | " + sql);

            return false;
        }
        
        sql = "INSERT INTO user_accesses(ua_id, user_id, role_id, login, salt, dk)"
                + "VALUES('35326873-9852-11f0-b1b7-62517600596c', "
                + "'69231c55-9851-11f0-b1b7-62517600596c', "
                + "'admin', "
                + "'admin',"
                + "'admin', '" + kdf.dk("admin", "admin")+ "'"
                + ") ON DUPLICATE KEY UPDATE "
                + "user_id  = VALUES(user_id),"
                + "role_id  = VALUES(role_id),"
                + "login  = VALUES(login),"
                + "salt  = VALUES(salt),"
                + "dk = VALUES(dk)";
        try(Statement statement = db.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DbSeeder::seed {0}",
                    ex.getMessage() + " | " + sql);

            return false;
        }
        
        return true;
    }
    
}
