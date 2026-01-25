package learning.itstep.javaweb222.data.group;

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
import learning.itstep.javaweb222.data.dto.GroupMember;
import learning.itstep.javaweb222.data.dto.Post;
import learning.itstep.javaweb222.models.group.GroupBlockModel;
import learning.itstep.javaweb222.models.group.GroupModel;

@Singleton
public class GroupDao {

    private final DbProvider db;
    private final Logger logger;

    @Inject
    public GroupDao(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

  public void createGroup(String ownerId, String name, String description, String avatarUrl) {
    String sql = """
        INSERT INTO user_groups
        (group_id, owner_id, name, description, avatar_url)
        VALUES(UUID(), ?, ?, ?, ?)
    """;

    try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
        ps.setString(1, ownerId);
        ps.setString(2, name);
        ps.setString(3, description);
        ps.setString(4, avatarUrl);
        ps.executeUpdate();
    }
    catch (SQLException ex) {
        logger.log(Level.WARNING, "GroupDao::createGroup {0}", ex.getMessage());
    }
}
  
  
  public GroupModel getGroupById(String groupId) {

String sql = """
    SELECT
        g.group_id,
        g.owner_id,
        g.name,
        g.description,
        g.avatar_url,
        COUNT(DISTINCT gm.user_id) AS members_count,
        COUNT(DISTINCT p.post_id) AS postsCount
    FROM user_groups g
    LEFT JOIN group_members gm
        ON gm.group_id = g.group_id
       AND gm.deleted_at IS NULL
    LEFT JOIN posts p 
        ON p.user_id = gm.user_id 
        AND p.deleted_at IS NULL
    WHERE g.group_id = ?
      AND g.deleted_at IS NULL
    GROUP BY g.group_id
""";


    try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
        ps.setString(1, groupId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new GroupModel()
                .setGroupId(UUID.fromString(rs.getString("group_id")))
                .setOwnerId(UUID.fromString(rs.getString("owner_id")))
                .setName(rs.getString("name"))
                .setDescription(rs.getString("description"))
                .setAvatarUrl(rs.getString("avatar_url"))
                .setMembersCount(rs.getInt("members_count"))
                .setPostsPerWeek(rs.getInt("postsCount"))
                .setCover("/assets/group-cover.jpg")
                    .setRules(List.of(
                        "Be respectful and professional",
                        "No spam or self-promotion",
                        "Share quality content",
                        "Help others learn and grow"
                    ));
        }
    }
    catch (SQLException ex) {
        logger.log(Level.WARNING,
            "GroupDao::getGroupById {0}",
            ex.getMessage() + " | " + sql);
    }

    return null;
}


    // ================== GROUPS ==================

public List<GroupBlockModel> getMyGroups(String userId) {

    List<GroupBlockModel> res = new ArrayList<>();

    String sql = """
        SELECT
            g.group_id,
            g.name,
            g.description,
            g.avatar_url,
            COUNT(gm.user_id) AS members_count
        FROM user_groups g
        LEFT JOIN group_members gm
            ON gm.group_id = g.group_id
           AND gm.deleted_at IS NULL
        WHERE g.owner_id = ?
          AND g.deleted_at IS NULL
        GROUP BY g.group_id
        ORDER BY g.created_at DESC
    """;

    try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
        ps.setString(1, userId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            GroupBlockModel g = new GroupBlockModel();
            g.setGroupId(UUID.fromString(rs.getString("group_id")));
            g.setName(rs.getString("name"));
            g.setDescription(rs.getString("description"));
            g.setAvatarUrl(rs.getString("avatar_url"));
            g.setMembersCount(rs.getInt("members_count"));

            res.add(g);
        }
    }
    catch (SQLException ex) {
        logger.log(Level.WARNING,
            "GroupDao::getMyGroups {0}",
            ex.getMessage() + " | " + sql);
    }

    return res;
}
// ================== GROUP MEMBERS ==================
public List<GroupMember> getGroupMembers(String groupId) {

    List<GroupMember> res = new ArrayList<>();
String sql = """
    SELECT
        gm.group_member_id,
        gm.group_id,
        gm.role,
        gm.created_at,
        gm.updated_at,
        gm.deleted_at,

        u.*
    FROM group_members gm
    JOIN users u
        ON u.user_id = gm.user_id
       AND u.deleted_at IS NULL
    WHERE gm.group_id = ?
      AND gm.deleted_at IS NULL
    ORDER BY gm.created_at
""";


    try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
        ps.setString(1, groupId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            res.add(GroupMember.fromResultSet(rs));
        }
    }
    catch (SQLException ex) {
        logger.log(Level.WARNING,
            "GroupDao::getGroupMembers {0}",
            ex.getMessage() + " | " + sql);
    }

    return res;
}

public List<Post> getGroupPosts(String groupId) {

    List<Post> res = new ArrayList<>();

    String sql = """
  SELECT
        p.post_id,
        p.user_id,
        p.content,
        p.visibility,
        p.reaction_count,
        p.comment_count,
        p.repost_count,
        p.created_at,
        p.edited_at,
        p.deleted_at,
    
        u.*
    FROM group_posts gp
    JOIN posts p ON p.post_id = gp.post_id
    JOIN users u ON u.user_id = p.user_id
    WHERE gp.group_id = ?
      AND p.deleted_at IS NULL
    ORDER BY p.created_at DESC
""";


    try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
        ps.setString(1, groupId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            res.add(Post.fromResultSet(rs));
        }
    }
    catch (SQLException ex) {
        logger.log(Level.WARNING, "GroupDao::getGroupPosts {0}", ex.getMessage());
    }

    return res;
}

  
}
