package learning.itstep.javaweb222.data.core.seed;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import learning.itstep.javaweb222.data.core.DbProvider;

@Singleton
public class SeedActivity {

    private final DbProvider db;
    private final Logger logger;

    @Inject
    public SeedActivity(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    private boolean exec(String sql, String tag) {
        try (Statement st = db.getConnection().createStatement()) {
            st.executeUpdate(sql);
            return true;
        }
        catch (SQLException ex) {
            logger.log(
                Level.WARNING,
                "SeedActivity::{0} {1}",
                new Object[]{tag, ex.getMessage() + " | " + sql}
            );
            return false;
        }
    }

    public boolean seed() {

        String sql;

        // ==============================
        // Emma Stone — career start
        // ==============================
        sql =
            "INSERT INTO user_activity (" +
            "activity_id, user_id, action, entity_type, meta" +
            ") SELECT UUID(), " +
            "'7a9f1c21-9851-11f0-b1b7-62517600596c', " +
            "'career', 'user', " +
            "JSON_OBJECT(" +
            "  'title','Started new position'," +
            "  'description','Started new position as Junior UI/UX Designer'" +
            ")" +
            "WHERE NOT EXISTS (" +
            "  SELECT 1 FROM user_activity " +
            "  WHERE action='career' " +
            "    AND user_id='7a9f1c21-9851-11f0-b1b7-62517600596c'" +
            ")";

        if (!exec(sql, "career emma")) return false;


        // ==============================
        // Lucas Brown — birthday
        // ==============================
        sql =
            "INSERT INTO user_activity (" +
            "activity_id, user_id, action, entity_type, meta" +
            ") SELECT UUID(), " +
            "'8c21d9a2-9851-11f0-b1b7-62517600596c', " +
            "'birthday', 'user', " +
            "JSON_OBJECT(" +
            "  'title','Birthday today'," +
            "  'description','Lucas Brown is celebrating birthday today'," +
            "  'date', CURDATE()" +
            ")" +
            "WHERE NOT EXISTS (" +
            "  SELECT 1 FROM user_activity " +
            "  WHERE action='birthday' " +
            "    AND user_id='8c21d9a2-9851-11f0-b1b7-62517600596c'" +
            ")";

        if (!exec(sql, "birthday lucas")) return false;


        // ==============================
        // Lucas Brown — career promotion
        // ==============================
        sql =
            "INSERT INTO user_activity (" +
            "activity_id, user_id, action, entity_type, meta" +
            ") SELECT UUID(), " +
            "'8c21d9a2-9851-11f0-b1b7-62517600596c', " +
            "'career', 'user', " +
            "JSON_OBJECT(" +
            "  'title','Promoted to Team Lead'," +
            "  'description','Promoted to Team Lead at Innovation Labs'," +
            "  'subtype','promotion'" +
            ")" +
            "WHERE NOT EXISTS (" +
            "  SELECT 1 FROM user_activity " +
            "  WHERE action='career' " +
            "    AND user_id='8c21d9a2-9851-11f0-b1b7-62517600596c'" +
            ")";

        if (!exec(sql, "career promotion lucas")) return false;


        // ==============================
        // Admin — education
        // ==============================
        sql =
            "INSERT INTO user_activity (" +
            "activity_id, user_id, action, entity_type, meta" +
            ") SELECT UUID(), " +
            "'69231c55-9851-11f0-b1b7-62517600596c', " +
            "'education', 'user', " +
            "JSON_OBJECT(" +
            "  'title','Graduated from University'," +
            "  'institution','UCLA'," +
            "  'degree','Computer Science'" +
            ")" +
            "WHERE NOT EXISTS (" +
            "  SELECT 1 FROM user_activity " +
            "  WHERE action='education' " +
            "    AND user_id='69231c55-9851-11f0-b1b7-62517600596c'" +
            ")";

        if (!exec(sql, "education admin")) return false;


        // ==============================
        // Like activity (designer → admin)
        // ==============================
        sql =
            "INSERT INTO user_activity (" +
            "activity_id, user_id, action, entity_type, meta" +
            ") SELECT UUID(), " +
            "'7a9f1c21-9851-11f0-b1b7-62517600596c', " +
            "'like', 'post', " +
            "JSON_OBJECT('target','admin')" +
            "WHERE NOT EXISTS (" +
            "  SELECT 1 FROM user_activity " +
            "  WHERE action='like' " +
            "    AND user_id='7a9f1c21-9851-11f0-b1b7-62517600596c'" +
            ")";

        if (!exec(sql, "like designer")) return false;


        return true;
    }
}
