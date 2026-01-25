package learning.itstep.javaweb222.data.core.seed;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import learning.itstep.javaweb222.data.core.DbProvider;

import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class SeedDemoPosts {

    private final DbProvider db;
    private final Logger logger;

    private static final String DESIGNER_ID =
        "7a9f1c21-9851-11f0-b1b7-62517600596c";
    private static final String DEVELOPER_ID =
        "8c21d9a2-9851-11f0-b1b7-62517600596c";

    @Inject
    public SeedDemoPosts(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    private boolean exec(String sql, String tag) {
        try (Statement st = db.getConnection().createStatement()) {
            st.executeUpdate(sql);
            return true;
        }
        catch (Exception ex) {
            logger.log(Level.WARNING,
                "SeedDemoPosts::{0} {1}",
                new Object[]{tag, ex.getMessage() + " | " + sql});
            return false;
        }
    }

    public boolean seed() {

        String sql;

        // ================== POSTS ==================
        sql =
            "INSERT INTO posts (post_id,user_id,content) " +
            "SELECT UUID(),'" + DESIGNER_ID + "'," +
            "'Design is not just what it looks like — it is how it works.' " +
            "WHERE NOT EXISTS (SELECT 1 FROM posts WHERE user_id='" + DESIGNER_ID + "')";
        if (!exec(sql, "post designer")) return false;

        sql =
            "INSERT INTO posts (post_id,user_id,content) " +
            "SELECT UUID(),'" + DEVELOPER_ID + "'," +
            "'Clean code always looks like it was written by someone who cares.' " +
            "WHERE NOT EXISTS (SELECT 1 FROM posts WHERE user_id='" + DEVELOPER_ID + "')";
        if (!exec(sql, "post developer")) return false;

        // ================== MEDIA ==================
        sql =
            "INSERT INTO media (media_id,url,type) " +
            "SELECT 'b3333c55-9853-11f0-b1b7-62517600596c','post_designer_1.jpg','image' " +
            "WHERE NOT EXISTS (SELECT 1 FROM media WHERE url='post_designer_1.jpg')";
        if (!exec(sql, "media designer")) return false;

        sql =
            "INSERT INTO media (media_id,url,type) " +
            "SELECT 'b4444c55-9853-11f0-b1b7-62517600596c','post_developer_1.jpg','image' " +
            "WHERE NOT EXISTS (SELECT 1 FROM media WHERE url='post_developer_1.jpg')";
        if (!exec(sql, "media developer")) return false;

        // ================== POST_MEDIA ==================
        sql =
            "INSERT INTO post_media (post_media_id,post_id,media_id) " +
            "SELECT UUID(),p.post_id,'b3333c55-9853-11f0-b1b7-62517600596c' " +
            "FROM posts p WHERE p.user_id='" + DESIGNER_ID + "' " +
            "AND NOT EXISTS (SELECT 1 FROM post_media pm WHERE pm.post_id=p.post_id)";
        if (!exec(sql, "post_media designer")) return false;

        sql =
            "INSERT INTO post_media (post_media_id,post_id,media_id) " +
            "SELECT UUID(),p.post_id,'b4444c55-9853-11f0-b1b7-62517600596c' " +
            "FROM posts p WHERE p.user_id='" + DEVELOPER_ID + "' " +
            "AND NOT EXISTS (SELECT 1 FROM post_media pm WHERE pm.post_id=p.post_id)";
        if (!exec(sql, "post_media developer")) return false;

        return true;
    }
}
