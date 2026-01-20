
package learning.itstep.javaweb222.data.post;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import learning.itstep.javaweb222.data.core.DbProvider;
import learning.itstep.javaweb222.data.dto.Post;

@Singleton
public class PostDao {
    private final DbProvider db;
    private final Logger logger;

    @Inject
    public PostDao(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }
    // ================== GET POST ==================

    public Post getPostById(String postId) {
        String sql = "SELECT * FROM posts p "
                + "WHERE p.post_id = ? AND p.deleted_at IS NULL";

        try( PreparedStatement prep = db.getConnection().prepareStatement(sql) ) {
            prep.setString(1, postId);
            ResultSet rs = prep.executeQuery();
            if(rs.next()) {
                return Post.fromResultSet(rs);
            }
            else return null;
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "PostDao::getPostById {0}",
                    ex.getMessage() + " | " + sql);
            return null;
        }
    }

    // ================== FEED ==================

    public List<Post> getFeed(int page, int perPage) {
        int skip = (page - 1) * perPage;

        String sql = "SELECT * FROM posts p "
                + "WHERE p.deleted_at IS NULL "
                + "ORDER BY p.created_at DESC "
                + String.format("LIMIT %d, %d", skip, perPage);

        List<Post> ret = new ArrayList<>();
        try( Statement statement = db.getConnection().createStatement();
             ResultSet rs = statement.executeQuery(sql) ) {

            while(rs.next()) {
                ret.add(Post.fromResultSet(rs));
            }
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "PostDao::getFeed {0}",
                    ex.getMessage() + " | " + sql);
        }
        return ret;
    }

    // ================== USER POSTS ==================

    public List<Post> getUserPosts(String userId) {
        String sql = "SELECT * FROM posts p "
                + "WHERE p.user_id = ? AND p.deleted_at IS NULL "
                + "ORDER BY p.created_at DESC";

        List<Post> ret = new ArrayList<>();
        try( PreparedStatement prep = db.getConnection().prepareStatement(sql) ) {
            prep.setString(1, userId);
            ResultSet rs = prep.executeQuery();
            while(rs.next()) {
                ret.add(Post.fromResultSet(rs));
            }
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "PostDao::getUserPosts {0}",
                    ex.getMessage() + " | " + sql);
        }
        return ret;
    }

    // ================== CREATE ==================

    public void addPost(Post post) {
        if(post.getId() == null) {
            post.setId(db.getDbIdentity());
        }

        String sql = "INSERT INTO posts("
                + "post_id, user_id, content, visibility"
                + ") VALUES(?, ?, ?, ?)";

        try( PreparedStatement prep = db.getConnection().prepareStatement(sql) ) {
            prep.setString(1, post.getId().toString());
            prep.setString(2, post.getUserId().toString());
            prep.setString(3, post.getContent());
            prep.setString(4, post.getVisibility());
            prep.executeUpdate();
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "PostDao::addPost {0}",
                    ex.getMessage() + " | " + sql);
            throw new RuntimeException(ex.getMessage());
        }
    }
    
}
