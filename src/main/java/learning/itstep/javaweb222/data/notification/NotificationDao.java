package learning.itstep.javaweb222.data.notification;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import learning.itstep.javaweb222.data.core.DbProvider;
import learning.itstep.javaweb222.data.dto.Notification;

@Singleton
public class NotificationDao {

    private final DbProvider db;
    private final Logger logger;

    @Inject
    public NotificationDao(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    // ================== GET ==================

    public List<Notification> getUserNotifications(String userId) {
        String sql = "SELECT * FROM notifications n "
                + "LEFT JOIN users u ON n.user_id = u.user_id "
                + "WHERE n.user_id = ? AND n.deleted_at IS NULL "
                + "ORDER BY n.created_at DESC";

        List<Notification> list = new ArrayList<>();

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, userId);
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                list.add(Notification.fromResultSet(rs));
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "NotificationDao::getUserNotifications {0}",
                    ex.getMessage() + " | " + sql);
        }
        return list;
    }

    public int getUnreadCount(String userId) {
        String sql = "SELECT COUNT(*) FROM notifications "
                + "WHERE user_id = ? AND is_read = 0 AND deleted_at IS NULL";

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, userId);
            ResultSet rs = prep.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "NotificationDao::getUnreadCount {0}",
                    ex.getMessage() + " | " + sql);
            return 0;
        }
    }

    public Notification getById(String notificationId) {
        String sql = "SELECT * FROM notifications n "
                + "LEFT JOIN users u ON n.user_id = u.user_id "
                + "WHERE n.notification_id = ?";

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, notificationId);
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                return Notification.fromResultSet(rs);
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "NotificationDao::getById {0}",
                    ex.getMessage() + " | " + sql);
        }
        return null;
    }

    // ================== CREATE ==================

    public void addNotification(Notification n) throws Exception {
        UUID.fromString(n.getUserId().toString());

        String sql = "INSERT INTO notifications "
                + "(notification_id, user_id, type, title, body, entity_type, entity_id) "
                + "VALUES(UUID(), ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, n.getUserId().toString());
            prep.setString(2, n.getType());
            prep.setString(3, n.getTitle());
            prep.setString(4, n.getBody());
            prep.setString(5, n.getEntityType());
            prep.setString(6, n.getEntityId() == null ? null : n.getEntityId().toString());
            prep.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "NotificationDao::addNotification {0}",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
    }

    // ================== UPDATE ==================

    public void markAsRead(String notificationId) {
        String sql = "UPDATE notifications "
                + "SET is_read = 1, updated_at = CURRENT_TIMESTAMP "
                + "WHERE notification_id = ? AND deleted_at IS NULL";

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, notificationId);
            prep.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "NotificationDao::markAsRead {0}",
                    ex.getMessage() + " | " + sql);
        }
    }

    public void markAllAsRead(String userId) {
        String sql = "UPDATE notifications "
                + "SET is_read = 1, updated_at = CURRENT_TIMESTAMP "
                + "WHERE user_id = ? AND is_read = 0 AND deleted_at IS NULL";

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, userId);
            prep.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "NotificationDao::markAllAsRead {0}",
                    ex.getMessage() + " | " + sql);
        }
    }

    // ================== DELETE ==================

    public void deleteNotification(String notificationId) {
        String sql = "UPDATE notifications "
                + "SET deleted_at = CURRENT_TIMESTAMP "
                + "WHERE notification_id = ?";

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, notificationId);
            prep.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "NotificationDao::deleteNotification {0}",
                    ex.getMessage() + " | " + sql);
        }
    }
}
