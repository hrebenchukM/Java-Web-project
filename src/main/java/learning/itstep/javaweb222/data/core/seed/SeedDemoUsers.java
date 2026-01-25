package learning.itstep.javaweb222.data.core.seed;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import learning.itstep.javaweb222.data.core.DbProvider;
import learning.itstep.javaweb222.services.kdf.KdfService;

import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class SeedDemoUsers {

    private final DbProvider db;
    private final Logger logger;
    private final KdfService kdf;

    public static final String ADMIN_ID =
        "69231c55-9851-11f0-b1b7-62517600596c";
    public static final String DESIGNER_ID =
        "7a9f1c21-9851-11f0-b1b7-62517600596c";
    public static final String DEVELOPER_ID =
        "8c21d9a2-9851-11f0-b1b7-62517600596c";

    @Inject
    public SeedDemoUsers(DbProvider db, Logger logger, KdfService kdf) {
        this.db = db;
        this.logger = logger;
        this.kdf = kdf;
    }

    private boolean exec(String sql, String tag) {
        try (Statement st = db.getConnection().createStatement()) {
            st.executeUpdate(sql);
            return true;
        }
        catch (Exception ex) {
            logger.log(Level.WARNING,
                "SeedDemoUsers::{0} {1}",
                new Object[]{tag, ex.getMessage() + " | " + sql});
            return false;
        }
    }

    public boolean seed() {

        String sql;

        // ================== DESIGNER ==================
        sql =
            "INSERT INTO users (user_id,email,first_name,second_name,avatar_url,profile_title,location,auth_provider) " +
            "SELECT '" + DESIGNER_ID + "', 'designer@demo.com','Emma','Stone','emma.jpg'," +
            "'Junior UI/UX Designer','Berlin, Germany','local' " +
            "WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_id='" + DESIGNER_ID + "')";
        if (!exec(sql, "user designer")) return false;

        String salt = "designer";
        String hash = kdf.dk("designer", salt);

        sql =
            "INSERT INTO auth_credentials (auth_id,user_id,role_id,login,salt,password_hash,auth_provider) " +
            "SELECT UUID(),'" + DESIGNER_ID + "','guest','designer','" + salt + "','" + hash + "','local' " +
            "WHERE NOT EXISTS (SELECT 1 FROM auth_credentials WHERE user_id='" + DESIGNER_ID + "')";
        if (!exec(sql, "auth designer")) return false;

        // ================== DEVELOPER ==================
        sql =
            "INSERT INTO users (user_id,email,first_name,second_name,avatar_url,profile_title,location,auth_provider) " +
            "SELECT '" + DEVELOPER_ID + "', 'dev@demo.com','Lucas','Brown','lucas.jpg'," +
            "'Frontend Developer','Amsterdam, Netherlands','local' " +
            "WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_id='" + DEVELOPER_ID + "')";
        if (!exec(sql, "user developer")) return false;

        salt = "dev";
        hash = kdf.dk("dev", salt);

        sql =
            "INSERT INTO auth_credentials (auth_id,user_id,role_id,login,salt,password_hash,auth_provider) " +
            "SELECT UUID(),'" + DEVELOPER_ID + "','guest','dev','" + salt + "','" + hash + "','local' " +
            "WHERE NOT EXISTS (SELECT 1 FROM auth_credentials WHERE user_id='" + DEVELOPER_ID + "')";
        if (!exec(sql, "auth developer")) return false;

        // ================== CONTACT USERS ==================
        sql =
            "INSERT INTO users (user_id,email,first_name,second_name,avatar_url,profile_title,auth_provider) " +
            "SELECT 'aaaa1111-0000-0000-0000-000000000002','james@demo.com','James','Wilson','james.jpg'," +
            "'UX Lead at Meta','local' " +
            "WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='james@demo.com')";
        if (!exec(sql, "contact James")) return false;

        sql =
            "INSERT INTO users (user_id,email,first_name,second_name,avatar_url,profile_title,auth_provider) " +
            "SELECT 'aaaa1111-0000-0000-0000-000000000003','emma.thompson@demo.com','Emma','Thompson','emma.jpg'," +
            "'Design Manager at Apple','local' " +
            "WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='emma.thompson@demo.com')";
        if (!exec(sql, "contact Emma Thompson")) return false;

        sql =
            "INSERT INTO users (user_id,email,first_name,second_name,avatar_url,profile_title,auth_provider) " +
            "SELECT 'aaaa1111-0000-0000-0000-000000000004','michael@demo.com','Michael','Chen','michael.jpg'," +
            "'Product Manager at Microsoft','local' " +
            "WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='michael@demo.com')";
        if (!exec(sql, "contact Michael")) return false;

        // ================== ACCEPTED CONTACT (Lucas ↔ Admin) ==================
        sql =
            "INSERT INTO contacts (contact_id,requester_id,receiver_id,status,requested_at,responded_at,status_changed_at) " +
            "SELECT UUID(),'" + DEVELOPER_ID + "','" + ADMIN_ID + "','accepted',NOW(),NOW(),NOW() " +
            "WHERE NOT EXISTS (" +
            "  SELECT 1 FROM contacts WHERE requester_id='" + DEVELOPER_ID + "' AND receiver_id='" + ADMIN_ID + "'" +
            ")";
        if (!exec(sql, "contact Lucas-Admin")) return false;

        return true;
    }
}
