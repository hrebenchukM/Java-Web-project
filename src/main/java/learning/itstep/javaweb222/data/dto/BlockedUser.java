package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class BlockedUser {
    private UUID id;
    private UUID userId;
    private UUID blockedUserId;
    private Date blockedAt;
    private Date unblockedAt;

    private User user;
    private User blockedUser;

    public static BlockedUser fromResultSet(ResultSet rs) throws SQLException {
        BlockedUser b = new BlockedUser();
        b.setId(UUID.fromString(rs.getString("block_id")));
        b.setUserId(UUID.fromString(rs.getString("user_id")));
        b.setBlockedUserId(UUID.fromString(rs.getString("blocked_user_id")));

        Timestamp ts;
        ts = rs.getTimestamp("blocked_at");
        b.setBlockedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("unblocked_at");
        if (ts != null) b.setUnblockedAt(new Date(ts.getTime()));

        try { b.setUser(User.fromResultSet(rs)); } catch (Exception ignore) {}
        try { b.setBlockedUser(User.fromResultSet(rs)); } catch (Exception ignore) {}

        return b;
    }

       // getters / setters

    public UUID getId() {
        return id;
    }

    public BlockedUser setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public BlockedUser setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public UUID getBlockedUserId() {
        return blockedUserId;
    }

    public BlockedUser setBlockedUserId(UUID blockedUserId) {
        this.blockedUserId = blockedUserId;
        return this;
    }

    public Date getBlockedAt() {
        return blockedAt;
    }

    public BlockedUser setBlockedAt(Date blockedAt) {
        this.blockedAt = blockedAt;
        return this;
    }

    public Date getUnblockedAt() {
        return unblockedAt;
    }

    public BlockedUser setUnblockedAt(Date unblockedAt) {
        this.unblockedAt = unblockedAt;
        return this;
    }

    public User getUser() {
        return user;
    }

    public BlockedUser setUser(User user) {
        this.user = user;
        return this;
    }

    public User getBlockedUser() {
        return blockedUser;
    }

    public BlockedUser setBlockedUser(User blockedUser) {
        this.blockedUser = blockedUser;
        return this;
    }

}
