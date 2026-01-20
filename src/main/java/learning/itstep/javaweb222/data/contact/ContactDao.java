package learning.itstep.javaweb222.data.contact;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import learning.itstep.javaweb222.data.core.DbProvider;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class ContactDao {

    private final DbProvider db;
    private final Logger logger;

    @Inject
    public ContactDao(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    public void sendRequest(String fromId, String toId) {
        String sql = """
            INSERT INTO contacts
            (contact_id, requester_id, receiver_id, status)
            VALUES(UUID(), ?, ?, 'pending')
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, fromId);
            ps.setString(2, toId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "ContactDao::sendRequest {0}", ex.getMessage());
        }
    }

    public void acceptRequest(String contactId) {
        String sql = """
            UPDATE contacts
            SET status='accepted', responded_at=CURRENT_TIMESTAMP
            WHERE contact_id=?
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, contactId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "ContactDao::acceptRequest {0}", ex.getMessage());
        }
    }
}
