package learning.itstep.javaweb222.data.activity;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import learning.itstep.javaweb222.data.core.DbProvider;
import learning.itstep.javaweb222.data.dto.UserActivity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class UserActivityDao {

    private final DbProvider db;
    private final Logger logger;

    @Inject
    public UserActivityDao(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    // =========================================================
    // My events (только мои)
    // =========================================================
    public List<UserActivity> getMyActivity(String userId, int limit, int offset) {

        List<UserActivity> result = new ArrayList<>();

        String sql = """
            SELECT ua.*, u.*
            FROM user_activity ua
            JOIN users u ON u.user_id = ua.user_id
            WHERE ua.user_id = ?
            ORDER BY ua.created_at DESC
            LIMIT ? OFFSET ?
        """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(UserActivity.fromResultSet(rs));
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "UserActivityDao::getMyActivity {0}",
                    ex.getMessage() + " | " + sql);
        }

        return result;
    }

    // =========================================================
    // Network events (люди из сети + suggested)
    // =========================================================
    public List<UserActivity> getNetworkActivity(String userId, int limit, int offset) {

        List<UserActivity> result = new ArrayList<>();

        String sql = """
            SELECT ua.*, u.*
            FROM user_activity ua
            JOIN users u ON u.user_id = ua.user_id
            JOIN contacts c ON (
                   (c.requester_id = ? AND c.receiver_id = ua.user_id)
                OR (c.receiver_id = ? AND c.requester_id = ua.user_id)
            )
            WHERE c.status IN ('accepted', 'suggested')
              AND ua.user_id <> ?
            ORDER BY ua.created_at DESC
            LIMIT ? OFFSET ?
        """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, userId);
            ps.setString(3, userId);
            ps.setInt(4, limit);
            ps.setInt(5, offset);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(UserActivity.fromResultSet(rs));
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "UserActivityDao::getNetworkActivity {0}",
                    ex.getMessage() + " | " + sql);
        }

        return result;
    }
}
