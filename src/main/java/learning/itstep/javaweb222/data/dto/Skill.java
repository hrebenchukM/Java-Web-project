package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Skill {
    private UUID id;
    private String name;
    private String description;
    private Date createdAt;
    private Date updatedAt;

    public static Skill fromResultSet(ResultSet rs) throws SQLException {
        Skill s = new Skill();
        s.setId(UUID.fromString(rs.getString("skill_id")));
        s.setName(rs.getString("name"));
        s.setDescription(rs.getString("description"));

        Timestamp ts;
        ts = rs.getTimestamp("created_at");
        s.setCreatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("updated_at");
        if (ts != null) s.setUpdatedAt(new Date(ts.getTime()));

        return s;
    }
    public UUID getId() {
        return id;
    }

    public Skill setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Skill setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Skill setDescription(String description) {
        this.description = description;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Skill setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Skill setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

}
