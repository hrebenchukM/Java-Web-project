package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class UserSkill {
    private UUID id;
    private UUID userId;
    private UUID skillId;
    private String level;
    private byte isMain;
    private Integer orderIndex;
    private Date createdAt;
    private Date updatedAt;

    private Skill skill;

    public static UserSkill fromResultSet(ResultSet rs) throws SQLException {
        UserSkill us = new UserSkill();
        us.setId(UUID.fromString(rs.getString("user_skill_id")));
        us.setUserId(UUID.fromString(rs.getString("user_id")));
        us.setSkillId(UUID.fromString(rs.getString("skill_id")));
        us.setLevel(rs.getString("level"));
        us.setIsMain(rs.getByte("is_main"));
        us.setOrderIndex((Integer) rs.getObject("order_index"));

        Timestamp ts;
        ts = rs.getTimestamp("created_at");
        us.setCreatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("updated_at");
        if (ts != null) us.setUpdatedAt(new Date(ts.getTime()));

        try { us.setSkill(Skill.fromResultSet(rs)); }
        catch (Exception ignore) {}

        return us;
    }

       public UUID getId() {
        return id;
    }

    public UserSkill setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public UserSkill setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public UUID getSkillId() {
        return skillId;
    }

    public UserSkill setSkillId(UUID skillId) {
        this.skillId = skillId;
        return this;
    }

    public String getLevel() {
        return level;
    }

    public UserSkill setLevel(String level) {
        this.level = level;
        return this;
    }

    public byte getIsMain() {
        return isMain;
    }

    public UserSkill setIsMain(byte isMain) {
        this.isMain = isMain;
        return this;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public UserSkill setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public UserSkill setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public UserSkill setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Skill getSkill() {
        return skill;
    }

    public UserSkill setSkill(Skill skill) {
        this.skill = skill;
        return this;
    }

 
}
