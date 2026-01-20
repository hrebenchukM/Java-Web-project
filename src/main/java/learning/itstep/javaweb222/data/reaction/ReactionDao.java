package learning.itstep.javaweb222.data.reaction;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import learning.itstep.javaweb222.data.core.DbProvider;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class ReactionDao {

    private final DbProvider db;
    private final Logger logger;

    @Inject
    public ReactionDao(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    public void addReaction(String postId, String userId, String type) {
        String sql = """
            INSERT INTO reactions
            (reaction_id, post_id, user_id, reaction_type)
            VALUES(UUID(), ?, ?, ?)
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, postId);
            ps.setString(2, userId);
            ps.setString(3, type);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "ReactionDao::addReaction {0}", ex.getMessage());
        }
    }
}
