package learning.itstep.javaweb222.data.network;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import learning.itstep.javaweb222.data.core.DbProvider;
import learning.itstep.javaweb222.data.dto.User;

@Singleton
public class NetworkDao {

    private final DbProvider db;
    private final Logger logger;

    @Inject
    public NetworkDao(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    // -------------------- Suggestions --------------------

  public List<User> getSuggestedUsers(String userId) {

    List<User> result = new ArrayList<>();

    String sql = """
        SELECT u.*
        FROM contacts c
        JOIN users u ON u.user_id = c.receiver_id
        WHERE c.status = 'suggested'
    """;

    try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            result.add(User.fromResultSet(rs));
        }
    }
    catch (SQLException ex) {
        logger.log(Level.WARNING, "NetworkDao::getSuggestedUsers {0}",
                ex.getMessage() + " | " + sql);
    }

    return result;
}


    // -------------------- Requests --------------------

    public boolean sendRequest(String requesterId, String receiverId) {

        String sql = """
            INSERT INTO contacts
            (contact_id, requester_id, receiver_id, status, requested_at)
            VALUES (?,?,?,?,?)
        """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, requesterId);
            ps.setString(3, receiverId);
            ps.setString(4, "pending");
            ps.setTimestamp(5, new Timestamp(new Date().getTime()));

            ps.executeUpdate();
            return true;
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "NetworkDao::sendRequest {0}",
                    ex.getMessage() + " | " + sql);
            return false;
        }
    }

    // -------------------- Accept --------------------

    public boolean acceptRequest(String requesterId, String receiverId) {

        String sql = """
            UPDATE contacts SET
                status='accepted',
                responded_at=?,
                status_changed_at=?
            WHERE requester_id=? AND receiver_id=? AND status='pending'
        """;

        Timestamp now = new Timestamp(new Date().getTime());

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setTimestamp(1, now);
            ps.setTimestamp(2, now);
            ps.setString(3, requesterId);
            ps.setString(4, receiverId);

            return ps.executeUpdate() > 0;
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "NetworkDao::acceptRequest {0}",
                    ex.getMessage() + " | " + sql);
            return false;
        }
    }

    // -------------------- Remove / Reject --------------------

    public boolean removeConnection(String userA, String userB) {

        String sql = """
            DELETE FROM contacts
            WHERE (requester_id=? AND receiver_id=?)
               OR (requester_id=? AND receiver_id=?)
        """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, userA);
            ps.setString(2, userB);
            ps.setString(3, userB);
            ps.setString(4, userA);

            return ps.executeUpdate() > 0;
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "NetworkDao::removeConnection {0}",
                    ex.getMessage() + " | " + sql);
            return false;
        }
    }

    // -------------------- Status --------------------

    public String getConnectionStatus(String userA, String userB) {

        String sql = """
            SELECT status
            FROM contacts
            WHERE (requester_id=? AND receiver_id=?)
               OR (requester_id=? AND receiver_id=?)
        """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, userA);
            ps.setString(2, userB);
            ps.setString(3, userB);
            ps.setString(4, userA);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("status");
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "NetworkDao::getConnectionStatus {0}",
                    ex.getMessage() + " | " + sql);
        }

        return null;
    }
    
    // ================== CONTACTS ==================

public List<User> getContacts(String userId) {

    List<User> res = new ArrayList<>();

    String sql = """
        SELECT u.*
        FROM contacts c
        JOIN users u ON (
               (c.requester_id = ? AND u.user_id = c.receiver_id)
            OR (c.receiver_id = ? AND u.user_id = c.requester_id)
        )
        WHERE c.status = 'accepted'
          AND u.deleted_at IS NULL
    """;

    try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
        ps.setString(1, userId);
        ps.setString(2, userId);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            res.add(User.fromResultSet(rs));
        }
    }
    catch (SQLException ex) {
        logger.log(Level.WARNING,
            "NetworkDao::getContacts {0}",
            ex.getMessage() + " | " + sql);
    }

    return res;
}



// ================== FOLLOWING ==================

public List<User> getFollowing(String userId) {

    List<User> res = new ArrayList<>();

    String sql = """
        SELECT u.*
        FROM follows f
        JOIN users u ON u.user_id = f.following_id
        WHERE f.follower_id = ?
          AND f.unfollowed_at IS NULL
          AND u.deleted_at IS NULL
    """;

    try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
        ps.setString(1, userId);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            res.add(User.fromResultSet(rs));
        }
    }
    catch (SQLException ex) {
        logger.log(Level.WARNING,
            "NetworkDao::getFollowing {0}",
            ex.getMessage() + " | " + sql);
    }

    return res;
}


}
