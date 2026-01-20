package learning.itstep.javaweb222.data.group;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import learning.itstep.javaweb222.data.core.DbProvider;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class GroupDao {

    private final DbProvider db;
    private final Logger logger;

    @Inject
    public GroupDao(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    public void createGroup(String ownerId, String name, String description) {
        String sql = """
            INSERT INTO groups
            (group_id, owner_id, name, description)
            VALUES(UUID(), ?, ?, ?)
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, ownerId);
            ps.setString(2, name);
            ps.setString(3, description);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "GroupDao::createGroup {0}", ex.getMessage());
        }
    }
}
