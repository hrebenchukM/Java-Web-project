package learning.itstep.javaweb222.data.core.seed;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import learning.itstep.javaweb222.data.core.DbProvider;

@Singleton
public class SeedNotifications {

    private final DbProvider db;
    private final Logger logger;

    @Inject
    public SeedNotifications(DbProvider db, Logger logger) {
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
                "SeedNotifications::{0} {1}",
                new Object[]{tag, ex.getMessage() + " | " + sql}
            );
            return false;
        }
    }

    public boolean seed() {

        String adminId    = "69231c55-9851-11f0-b1b7-62517600596c";
        String designerId = "7a9f1c21-9851-11f0-b1b7-62517600596c";
        String developerId= "8c21d9a2-9851-11f0-b1b7-62517600596c";

        String sql;

        // ------------------ Notification: Like ------------------
        sql =
            "INSERT INTO notifications (" +
            "notification_id, user_id, actor_user_id, type, title, body, entity_type, is_read" +
            ") SELECT UUID(), " +
            "'" + adminId + "', " +
            "'" + designerId + "', " +
            "'like', " +
            "'New like on your post', " +
            "'Emma Stone liked your post', " +
            "'post', 0 " +
            "WHERE NOT EXISTS (" +
            "  SELECT 1 FROM notifications " +
            "  WHERE user_id='" + adminId + "' AND type='like'" +
            ")";

        if (!exec(sql, "seed notification like")) return false;


        // ------------------ Notification: Comment ------------------
        sql =
            "INSERT INTO notifications (" +
            "notification_id, user_id, actor_user_id, type, title, body, entity_type, is_read" +
            ") SELECT UUID(), " +
            "'" + adminId + "', " +
            "'" + developerId + "', " +
            "'comment', " +
            "'New comment on your post', " +
            "'Lucas Brown commented on your post', " +
            "'post', 0 " +
            "WHERE NOT EXISTS (" +
            "  SELECT 1 FROM notifications " +
            "  WHERE user_id='" + adminId + "' AND type='comment'" +
            ")";

        if (!exec(sql, "seed notification comment")) return false;


        // ------------------ Notification: Connection ------------------
        sql =
            "INSERT INTO notifications (" +
            "notification_id, user_id, actor_user_id, type, title, body, entity_type, is_read" +
            ") SELECT UUID(), " +
            "'" + adminId + "', " +
            "'" + designerId + "', " +
            "'connection', " +
            "'Connection request accepted', " +
            "'Emma Stone accepted your connection request', " +
            "'user', 1 " +
            "WHERE NOT EXISTS (" +
            "  SELECT 1 FROM notifications " +
            "  WHERE user_id='" + adminId + "' AND type='connection'" +
            ")";

        if (!exec(sql, "seed notification connection")) return false;


        // ------------------ Notification: Vacancy ------------------
        sql =
            "INSERT INTO notifications (" +
            "notification_id, user_id, actor_user_id, type, title, body, entity_type, entity_id, is_read" +
            ") SELECT UUID(), " +
            "'" + adminId + "', " +
            "NULL, " +
            "'vacancy', " +
            "'New job recommendation', " +
            "'Google posted a new job: Senior UI/UX Designer', " +
            "'vacancy', " +
            "(SELECT vacancy_id FROM vacancies LIMIT 1), " +
            "0 " +
            "WHERE NOT EXISTS (" +
            "  SELECT 1 FROM notifications " +
            "  WHERE user_id='" + adminId + "' AND type='vacancy'" +
            ")";

        if (!exec(sql, "seed notification vacancy")) return false;

        return true;
    }
}
