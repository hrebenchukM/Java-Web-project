
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
    
    private boolean exec(String sql, String tag) {
        try (Statement st = db.getConnection().createStatement()) {
            st.executeUpdate(sql);
            return true;
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "DbSeeder::{0} {1}",
                    new Object[]{tag, ex.getMessage() + " | " + sql});
            return false;
        }
    }
    
    public boolean seed() {

        // ------------------ Roles ------------------
        String sql =
            "INSERT INTO user_roles(role_id, description, can_create, can_read, can_update, can_delete) " +
            "VALUES('admin','Root Administrator',1,1,1,1) " +
            "ON DUPLICATE KEY UPDATE " +
            "description=VALUES(description), " +
            "can_create=VALUES(can_create), " +
            "can_read=VALUES(can_read), " +
            "can_update=VALUES(can_update), " +
            "can_delete=VALUES(can_delete)";

        if (!exec(sql, "seed user_roles admin")) return false;

        sql =
            "INSERT INTO user_roles(role_id, description, can_create, can_read, can_update, can_delete) " +
            "VALUES('guest','Self Registered User',0,1,0,0) " +
            "ON DUPLICATE KEY UPDATE " +
            "description=VALUES(description), " +
            "can_create=VALUES(can_create), " +
            "can_read=VALUES(can_read), " +
            "can_update=VALUES(can_update), " +
            "can_delete=VALUES(can_delete)";

        if (!exec(sql, "seed user_roles guest")) return false;

        // ------------------ Default admin user ------------------
        sql =
            "INSERT INTO users(" +
            "user_id, email, first_name, second_name, auth_provider" +
            ") VALUES(" +
            "'69231c55-9851-11f0-b1b7-62517600596c'," +
            "'admin@localhost'," +
            "'Default'," +
            "'Administrator'," +
            "'local'" +
            ") ON DUPLICATE KEY UPDATE " +
            "email=VALUES(email), " +
            "first_name=VALUES(first_name), " +
            "second_name=VALUES(second_name)";

        if (!exec(sql, "seed users admin")) return false;

        // ------------------ AuthCredentials (login + password) ------------------
        String salt = "admin";
        String passwordHash = kdf.dk("admin", salt);

        sql =
            "INSERT INTO auth_credentials(" +
            "auth_id, user_id, role_id, login, salt, password_hash, auth_provider" +
            ") VALUES(" +
            "'35326873-9852-11f0-b1b7-62517600596c'," +
            "'69231c55-9851-11f0-b1b7-62517600596c'," +
            "'admin'," +
            "'admin'," +
            "'" + salt + "'," +
            "'" + passwordHash + "'," +
            "'local'" +
            ") ON DUPLICATE KEY UPDATE " +
            "role_id=VALUES(role_id), " +
            "login=VALUES(login), " +
            "salt=VALUES(salt), " +
            "password_hash=VALUES(password_hash)";

        if (!exec(sql, "seed auth_credentials admin")) return false;

        return true;
    }
    
}
