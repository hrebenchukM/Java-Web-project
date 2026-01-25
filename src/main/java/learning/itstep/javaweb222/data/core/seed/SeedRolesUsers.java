package learning.itstep.javaweb222.data.core.seed;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import learning.itstep.javaweb222.data.core.DbProvider;
import learning.itstep.javaweb222.services.kdf.KdfService;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class SeedRolesUsers {

    private final DbProvider db;
    private final Logger logger;
    private final KdfService kdf;

    public static final String ADMIN_ID =
            "69231c55-9851-11f0-b1b7-62517600596c";

    @Inject
    public SeedRolesUsers(DbProvider db, Logger logger, KdfService kdf) {
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
            logger.log(Level.WARNING,
                "SeedRolesUsers::{0} {1}",
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
        if (!exec(sql, "role admin")) return false;

        sql =
            "INSERT INTO user_roles(role_id, description, can_create, can_read, can_update, can_delete) " +
            "VALUES('guest','Self Registered User',0,1,0,0) " +
            "ON DUPLICATE KEY UPDATE " +
            "description=VALUES(description), " +
            "can_create=VALUES(can_create), " +
            "can_read=VALUES(can_read), " +
            "can_update=VALUES(can_update), " +
            "can_delete=VALUES(can_delete)";
        if (!exec(sql, "role guest")) return false;

        // ------------------ Admin user ------------------
        sql =
            "INSERT INTO users (user_id, email, first_name, second_name, auth_provider) VALUES (" +
            "'" + ADMIN_ID + "', " +
            "'admin@localhost', 'Default', 'Administrator', 'local'" +
            ") ON DUPLICATE KEY UPDATE email=VALUES(email)";
        if (!exec(sql, "admin user")) return false;

        // ------------------ Auth credentials ------------------
        String salt = "admin";
        String hash = kdf.dk("admin", salt);

        sql =
            "INSERT INTO auth_credentials (" +
            "auth_id, user_id, role_id, login, salt, password_hash, auth_provider" +
            ") VALUES (" +
            "UUID(), '" + ADMIN_ID + "', 'admin', 'admin', " +
            "'" + salt + "', '" + hash + "', 'local'" +
            ") ON DUPLICATE KEY UPDATE " +
            "password_hash=VALUES(password_hash)";
        if (!exec(sql, "admin auth")) return false;

        return true;
    }
}
