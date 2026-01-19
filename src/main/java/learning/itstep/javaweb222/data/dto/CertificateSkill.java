package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class CertificateSkill {
    private UUID id;
    private UUID certificateId;
    private UUID skillId;
    private Date createdAt;

    private Skill skill;

    public static CertificateSkill fromResultSet(ResultSet rs) throws SQLException {
        CertificateSkill cs = new CertificateSkill();
        cs.setId(UUID.fromString(rs.getString("certificate_skill_id")));
        cs.setCertificateId(UUID.fromString(rs.getString("certificate_id")));
        cs.setSkillId(UUID.fromString(rs.getString("skill_id")));

        Timestamp ts = rs.getTimestamp("created_at");
        cs.setCreatedAt(new Date(ts.getTime()));

        try { cs.setSkill(Skill.fromResultSet(rs)); }
        catch (Exception ignore) {}

        return cs;
    }

       public UUID getId() {
        return id;
    }

    public CertificateSkill setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getCertificateId() {
        return certificateId;
    }

    public CertificateSkill setCertificateId(UUID certificateId) {
        this.certificateId = certificateId;
        return this;
    }

    public UUID getSkillId() {
        return skillId;
    }

    public CertificateSkill setSkillId(UUID skillId) {
        this.skillId = skillId;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public CertificateSkill setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Skill getSkill() {
        return skill;
    }

    public CertificateSkill setSkill(Skill skill) {
        this.skill = skill;
        return this;
    }

}
