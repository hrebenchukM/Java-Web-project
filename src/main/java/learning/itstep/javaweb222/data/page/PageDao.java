package learning.itstep.javaweb222.data.page;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import learning.itstep.javaweb222.data.core.DbProvider;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class PageDao {

    private final DbProvider db;
    private final Logger logger;

    @Inject
    public PageDao(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    public void createPage(String ownerId, String name, String description) {
        String sql = """
            INSERT INTO pages
            (page_id, owner_id, name, description)
            VALUES(UUID(), ?, ?, ?)
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, ownerId);
            ps.setString(2, name);
            ps.setString(3, description);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "PageDao::createPage {0}", ex.getMessage());
        }
    }
}
