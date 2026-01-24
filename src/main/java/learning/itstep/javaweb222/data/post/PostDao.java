package learning.itstep.javaweb222.data.post;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import learning.itstep.javaweb222.data.core.DbProvider;
import learning.itstep.javaweb222.data.dto.Post;
import learning.itstep.javaweb222.data.dto.Media;

@Singleton
public class PostDao {

    private final DbProvider db;
    private final Logger logger;

    @Inject
    public PostDao(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    // ================== GET POST BY ID ==================

    public Post getPostById(String postId) {
        try {
            UUID.fromString(postId);
        }
        catch (Exception ex) {
            return null;
        }

        String sql =
            "SELECT p.*, u.*, m.* " +
            "FROM posts p " +
            "JOIN users u ON u.user_id = p.user_id " +
            "LEFT JOIN post_media pm ON pm.post_id = p.post_id " +
            "LEFT JOIN media m ON m.media_id = pm.media_id " +
            "WHERE p.post_id = ? AND p.deleted_at IS NULL";

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, postId);
            ResultSet rs = prep.executeQuery();

            Post post = null;
            List<Media> mediaList = new ArrayList<>();

            while (rs.next()) {
                if (post == null) {
                    post = Post.fromResultSet(rs);
                }

                try {
                    Media media = Media.fromResultSet(rs);
                    mediaList.add(media);
                }
                catch (Exception ignore) {}
            }

            if (post != null) {
                post.setMedia(mediaList);
            }

            return post;
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "PostDao::getPostById {0}",
                    ex.getMessage() + " | " + sql);
            return null;
        }
    }

    // ================== FEED ==================

    public List<Post> getFeed(int page, int perPage) {
        if (page < 1) page = 1;
        if (perPage < 1) perPage = 10;

        int skip = (page - 1) * perPage;

        String sql =
            "SELECT p.*, u.*, m.* " +
            "FROM posts p " +
            "JOIN users u ON u.user_id = p.user_id " +
            "LEFT JOIN post_media pm ON pm.post_id = p.post_id " +
            "LEFT JOIN media m ON m.media_id = pm.media_id " +
            "WHERE p.deleted_at IS NULL " +
            "ORDER BY p.created_at DESC " +
            "LIMIT ?, ?";

        Map<UUID, Post> posts = new LinkedHashMap<>();

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setInt(1, skip);
            prep.setInt(2, perPage);

            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                UUID postId = UUID.fromString(rs.getString("post_id"));

                Post post = posts.get(postId);
                if (post == null) {
                    post = Post.fromResultSet(rs);
                    post.setMedia(new ArrayList<>());
                    posts.put(postId, post);
                }

                try {
                    Media media = Media.fromResultSet(rs);
                    post.getMedia().add(media);
                }
                catch (Exception ignore) {}
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "PostDao::getFeed {0}",
                    ex.getMessage() + " | " + sql);
        }

        return new ArrayList<>(posts.values());
    }

    // ================== FEED COUNT ==================

    public int getFeedCount() {
        String sql =
            "SELECT COUNT(*) FROM posts WHERE deleted_at IS NULL";

        try (Statement st = db.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            rs.next();
            return rs.getInt(1);
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "PostDao::getFeedCount {0}",
                    ex.getMessage() + " | " + sql);
            return 0;
        }
    }

    // ================== USER POSTS ==================

    public List<Post> getUserPosts(String userId) {
        try {
            UUID.fromString(userId);
        }
        catch (Exception ex) {
            return List.of();
        }

        String sql =
            "SELECT p.*, u.*, m.* " +
            "FROM posts p " +
            "JOIN users u ON u.user_id = p.user_id " +
            "LEFT JOIN post_media pm ON pm.post_id = p.post_id " +
            "LEFT JOIN media m ON m.media_id = pm.media_id " +
            "WHERE p.user_id = ? AND p.deleted_at IS NULL " +
            "ORDER BY p.created_at DESC";

        Map<UUID, Post> posts = new LinkedHashMap<>();

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, userId);
            ResultSet rs = prep.executeQuery();

            while (rs.next()) {
                UUID postId = UUID.fromString(rs.getString("post_id"));

                Post post = posts.get(postId);
                if (post == null) {
                    post = Post.fromResultSet(rs);
                    post.setMedia(new ArrayList<>());
                    posts.put(postId, post);
                }

                try {
                    Media media = Media.fromResultSet(rs);
                    post.getMedia().add(media);
                }
                catch (Exception ignore) {}
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "PostDao::getUserPosts {0}",
                    ex.getMessage() + " | " + sql);
        }

        return new ArrayList<>(posts.values());
    }

    // ================== USER POSTS COUNT ==================

    public int getUserPostsCount(String userId) {
        try {
            UUID.fromString(userId);
        }
        catch (Exception ex) {
            return 0;
        }

        String sql =
            "SELECT COUNT(*) FROM posts WHERE user_id = ? AND deleted_at IS NULL";

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, userId);
            ResultSet rs = prep.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "PostDao::getUserPostsCount {0}",
                    ex.getMessage() + " | " + sql);
            return 0;
        }
    }

    // ================== CREATE POST ==================

    public Post addPost(Post post) {
        if (post.getId() == null) {
            post.setId(db.getDbIdentity());
        }

        String sql =
            "INSERT INTO posts (post_id, user_id, content, visibility) " +
            "VALUES (?, ?, ?, ?)";

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, post.getId().toString());
            prep.setString(2, post.getUserId().toString());
            prep.setString(3, post.getContent());
            prep.setString(4, post.getVisibility());
            prep.executeUpdate();
            return post;
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "PostDao::addPost {0}",
                    ex.getMessage() + " | " + sql);
            throw new RuntimeException(ex.getMessage());
        }
    }

    // ================== DELETE POST ==================

    public void deletePost(String postId) throws Exception {
        try {
            UUID.fromString(postId);
        }
        catch (Exception ex) {
            throw new Exception("Invalid post id format");
        }

        String sql =
            "UPDATE posts SET deleted_at = CURRENT_TIMESTAMP " +
            "WHERE post_id = ? AND deleted_at IS NULL";

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, postId);
            prep.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "PostDao::deletePost {0}",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
    }
    public void attachMedia(UUID postId, UUID mediaId) {

    String sql =
        "INSERT INTO post_media (post_media_id, post_id, media_id) " +
        "VALUES (?, ?, ?)";

    try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
        prep.setString(1, db.getDbIdentity().toString());
        prep.setString(2, postId.toString());
        prep.setString(3, mediaId.toString());
        prep.executeUpdate();
    }
    catch (SQLException ex) {
        logger.log(Level.WARNING, "PostDao::attachMedia {0}",
                ex.getMessage() + " | " + sql);
        throw new RuntimeException(ex.getMessage());
    }
}

}
