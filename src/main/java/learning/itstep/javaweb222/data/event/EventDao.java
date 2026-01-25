
package learning.itstep.javaweb222.data.event;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import learning.itstep.javaweb222.data.core.DbProvider;
import learning.itstep.javaweb222.data.dto.Event;
import learning.itstep.javaweb222.models.event.EventBlockModel;

@Singleton
public class EventDao {

    private final DbProvider db;
    private final Logger logger;

    @Inject
    public EventDao(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    // ================== MY EVENTS ==================

    public List<EventBlockModel> getMyEvents(String userId) {

        List<EventBlockModel> res = new ArrayList<>();

        String sql = """
            SELECT
                e.*,
                COUNT(ea.user_id) AS attendees_count
            FROM events e
            LEFT JOIN event_attendees ea
                ON ea.event_id = e.event_id
               AND ea.deleted_at IS NULL
            WHERE e.organizer_type = 'user'
              AND e.organizer_id = ?
              AND e.deleted_at IS NULL
            GROUP BY e.event_id
            ORDER BY e.start_at
        """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                EventBlockModel ev = new EventBlockModel()
                    .setEvent(Event.fromResultSet(rs))
                    .setAttendeesCount(rs.getInt("attendees_count"));

                res.add(ev);
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING,
                "EventDao::getMyEvents {0}",
                ex.getMessage() + " | " + sql);
        }

        return res;
    }
}