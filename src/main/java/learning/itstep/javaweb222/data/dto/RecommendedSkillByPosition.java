package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class RecommendedSkillByPosition {
    private UUID id;
    private String position;
    private UUID skillId;
    private Date createdAt;
    private Date updatedAt;

    private Skill skill;

    public static RecommendedSkillByPosition fromResultSet(ResultSet rs) throws SQLException {
        RecommendedSkillByPosition r = new RecommendedSkillByPosition();
        r.setId(UUID.fromString(rs.getString("rsp_id")));
        r.setPosition(rs.getString("position"));
        r.setSkillId(UUID.fromString(rs.getString("skill_id")));

        Timestamp ts;
        ts = rs.getTimestamp("created_at");
        r.setCreatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("updated_at");
        if (ts != null) r.setUpdatedAt(new Date(ts.getTime()));

        try { r.setSkill(Skill.fromResultSet(rs)); }
        catch (Exception ignore) {}

        return r;
    }

    // getters / setters

    public UUID getId() {
        return id;
    }

    public RecommendedSkillByPosition setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getPosition() {
        return position;
    }

    public RecommendedSkillByPosition setPosition(String position) {
        this.position = position;
        return this;
    }

    public UUID getSkillId() {
        return skillId;
    }

    public RecommendedSkillByPosition setSkillId(UUID skillId) {
        this.skillId = skillId;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public RecommendedSkillByPosition setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public RecommendedSkillByPosition setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Skill getSkill() {
        return skill;
    }

    public RecommendedSkillByPosition setSkill(Skill skill) {
        this.skill = skill;
        return this;
    }

    
}
