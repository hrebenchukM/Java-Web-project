package learning.itstep.javaweb222.data.page;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import learning.itstep.javaweb222.data.core.DbProvider;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import learning.itstep.javaweb222.models.page.PageBlockModel;

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
     // ================== MY PAGES ==================

    public List<PageBlockModel> getMyPages(String userId) {

        List<PageBlockModel> res = new ArrayList<>();

        String sql = """
            SELECT
                p.page_id,
                p.name,
                p.description,
                p.logo_url,
                COUNT(pf.user_id) AS followers_count
            FROM pages p
            LEFT JOIN page_followers pf
                ON pf.page_id = p.page_id
               AND pf.unfollowed_at IS NULL
            WHERE p.owner_id = ?
              AND p.deleted_at IS NULL
            GROUP BY p.page_id
            ORDER BY p.created_at DESC
        """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PageBlockModel p = new PageBlockModel();
                p.setPageId(UUID.fromString(rs.getString("page_id")));
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                p.setLogoUrl(rs.getString("logo_url"));
                p.setFollowersCount(rs.getInt("followers_count"));

                res.add(p);
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING,
                "PageDao::getMyPages {0}",
                ex.getMessage() + " | " + sql);
        }

        return res;
    }
}
