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
import learning.itstep.javaweb222.models.page.PageFullModel;

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
     // ================== PAGE BY ID ==================
    // Используется в /pages/{id} и как organizer в events

    public PageBlockModel getPageById(String pageId) {

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
            WHERE p.page_id = ?
              AND p.deleted_at IS NULL
            GROUP BY p.page_id
        """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, pageId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                PageBlockModel p = new PageBlockModel();
                p.setPageId(UUID.fromString(rs.getString("page_id")));
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                p.setLogoUrl(rs.getString("logo_url"));
                p.setFollowersCount(rs.getInt("followers_count"));
                return p;
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING,
                "PageDao::getPageById {0}",
                ex.getMessage() + " | " + sql);
        }

        return null;
    }

    // ================== PAGE ADMINS ==================

    public List<UUID> getPageAdmins(String pageId) {

        List<UUID> res = new ArrayList<>();

        String sql = """
            SELECT user_id
            FROM page_admins
            WHERE page_id = ?
              AND revoked_at IS NULL
        """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, pageId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                res.add(UUID.fromString(rs.getString("user_id")));
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING,
                "PageDao::getPageAdmins {0}",
                ex.getMessage() + " | " + sql);
        }

        return res;
    }

    // ================== FOLLOWERS COUNT ==================

    public int getFollowersCount(String pageId) {

        String sql = """
            SELECT COUNT(*) cnt
            FROM page_followers
            WHERE page_id = ?
              AND unfollowed_at IS NULL
        """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, pageId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("cnt");
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING,
                "PageDao::getFollowersCount {0}",
                ex.getMessage() + " | " + sql);
        }

        return 0;
    }
    // ================== PAGE FULL ==================
 
    public PageFullModel getPageFull(String pageId) {

        PageBlockModel page = getPageById(pageId);
        if (page == null) return null;

        List<UUID> admins = getPageAdmins(pageId);
        int followersCount = getFollowersCount(pageId);

        return new PageFullModel()
            .setPage(page)
            .setAdmins(admins)
            .setFollowersCount(followersCount)
            .setVerified(!admins.isEmpty());
    }

}
