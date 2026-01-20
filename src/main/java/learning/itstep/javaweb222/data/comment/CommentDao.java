package learning.itstep.javaweb222.data.comment;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import learning.itstep.javaweb222.data.core.DbProvider;
import learning.itstep.javaweb222.data.dto.Comment;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class CommentDao {

    private final DbProvider db;
    private final Logger logger;

    @Inject
    public CommentDao(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    public void addComment(Comment c) {
        String sql = """
            INSERT INTO comments
            (comment_id, post_id, user_id, parent_comment_id, content)
            VALUES(UUID(), ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, c.getPostId().toString());
            ps.setString(2, c.getUserId().toString());
            ps.setString(3, c.getParentCommentId() == null ? null : c.getParentCommentId().toString());
            ps.setString(4, c.getContent());
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "CommentDao::addComment {0}", ex.getMessage());
        }
    }
}
