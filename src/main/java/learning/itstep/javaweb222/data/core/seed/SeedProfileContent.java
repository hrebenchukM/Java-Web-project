package learning.itstep.javaweb222.data.core.seed;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import learning.itstep.javaweb222.data.core.DbProvider;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class SeedProfileContent {

    private final DbProvider db;
    private final Logger logger;

    public static final String ADMIN_ID =
            "69231c55-9851-11f0-b1b7-62517600596c";

    @Inject
    public SeedProfileContent(DbProvider db, Logger logger) {
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
                "SeedProfileContent::{0} {1}",
                new Object[]{tag, ex.getMessage() + " | " + sql}
            );
            return false;
        }
    }

    public boolean seed() {

        String sql;

        // ================== ADMIN PROFILE ==================
        sql =
            "UPDATE users SET " +
            "profile_title='Lead UI/UX Designer', " +
            "headline='Lead UI/UX Designer · CD Project Red', " +
            "gen_info='Experienced UI/UX designer with focus on product design.', " +
            "university='Warsaw University', " +
            "location='Warsaw, Poland', " +
            "portfolio_url='https://portfolio.example.com', " +
            "avatar_url='admin.jpg' " +
            "WHERE user_id='" + ADMIN_ID + "'";
        if (!exec(sql, "admin profile")) return false;


        // ================== ADMIN POSTS ==================
        sql =
            "INSERT INTO posts (post_id, user_id, content) " +
            "SELECT UUID(), '" + ADMIN_ID + "', 'Seed post 1' " +
            "WHERE NOT EXISTS (" +
            "   SELECT 1 FROM posts WHERE user_id='" + ADMIN_ID + "' AND content='Seed post 1'" +
            ")";
        if (!exec(sql, "admin post 1")) return false;

        sql =
            "INSERT INTO posts (post_id, user_id, content) " +
            "SELECT UUID(), '" + ADMIN_ID + "', 'Seed post 2' " +
            "WHERE NOT EXISTS (" +
            "   SELECT 1 FROM posts WHERE user_id='" + ADMIN_ID + "' AND content='Seed post 2'" +
            ")";
        if (!exec(sql, "admin post 2")) return false;

        sql =
            "INSERT INTO posts (post_id, user_id, content) " +
            "SELECT UUID(), '" + ADMIN_ID + "', 'Seed post 3' " +
            "WHERE NOT EXISTS (" +
            "   SELECT 1 FROM posts WHERE user_id='" + ADMIN_ID + "' AND content='Seed post 3'" +
            ")";
        if (!exec(sql, "admin post 3")) return false;


        // ================== PROFILE VIEWS ==================
        sql =
            "INSERT INTO profile_views (pv_id, profile_owner_id, viewer_user_id, source) " +
            "SELECT UUID(), '" + ADMIN_ID + "', NULL, 'seed' " +
            "FROM dual WHERE NOT EXISTS (" +
            "   SELECT 1 FROM profile_views " +
            "   WHERE profile_owner_id='" + ADMIN_ID + "' AND source='seed'" +
            ")";
        if (!exec(sql, "profile views admin")) return false;


        // ================== POST VIEWS ==================
        sql =
            "INSERT INTO post_views (post_view_id, post_id, viewer_user_id, source) " +
            "SELECT UUID(), p.post_id, NULL, 'seed' " +
            "FROM posts p " +
            "WHERE p.user_id='" + ADMIN_ID + "' " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM post_views pv " +
            "   WHERE pv.post_id=p.post_id AND pv.source='seed'" +
            ")";
        if (!exec(sql, "post views admin")) return false;


        // ================== MEDIA ==================
        sql =
            "INSERT INTO media (media_id, url, type) " +
            "SELECT 'b1111c55-9853-11f0-b1b7-62517600596c', 'post_admin_1.jpg', 'image' " +
            "WHERE NOT EXISTS (SELECT 1 FROM media WHERE url='post_admin_1.jpg')";
        if (!exec(sql, "media admin 1")) return false;

        sql =
            "INSERT INTO media (media_id, url, type) " +
            "SELECT 'b2222c55-9853-11f0-b1b7-62517600596c', 'post_admin_2.jpg', 'image' " +
            "WHERE NOT EXISTS (SELECT 1 FROM media WHERE url='post_admin_2.jpg')";
        if (!exec(sql, "media admin 2")) return false;

        sql =
            "INSERT INTO media (media_id, url, type) " +
            "SELECT 'b5555c55-9853-11f0-b1b7-62517600596c', 'post_admin_3.jpg', 'image' " +
            "WHERE NOT EXISTS (SELECT 1 FROM media WHERE url='post_admin_3.jpg')";
        if (!exec(sql, "media admin 3")) return false;


        // ================== POST_MEDIA ==================
        sql =
            "INSERT INTO post_media (post_media_id, post_id, media_id) " +
            "SELECT UUID(), p.post_id, 'b1111c55-9853-11f0-b1b7-62517600596c' " +
            "FROM posts p " +
            "WHERE p.user_id='" + ADMIN_ID + "' AND p.content='Seed post 1' " +
            "AND NOT EXISTS (SELECT 1 FROM post_media pm WHERE pm.post_id=p.post_id)";
        if (!exec(sql, "post_media admin 1")) return false;

        sql =
            "INSERT INTO post_media (post_media_id, post_id, media_id) " +
            "SELECT UUID(), p.post_id, 'b2222c55-9853-11f0-b1b7-62517600596c' " +
            "FROM posts p " +
            "WHERE p.user_id='" + ADMIN_ID + "' AND p.content='Seed post 2' " +
            "AND NOT EXISTS (SELECT 1 FROM post_media pm WHERE pm.post_id=p.post_id)";
        if (!exec(sql, "post_media admin 2")) return false;

        return true;
    }
}
