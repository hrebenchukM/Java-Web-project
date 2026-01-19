package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Hashtag {
    private UUID id;
    private String name;
    private Date createdAt;
    private Date updatedAt;

    public static Hashtag fromResultSet(ResultSet rs) throws SQLException {
        Hashtag h = new Hashtag();
        h.setId(UUID.fromString(rs.getString("hashtag_id")));
        h.setName(rs.getString("name"));

        Timestamp ts;
        ts = rs.getTimestamp("created_at");
        h.setCreatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("updated_at");
        if (ts != null) h.setUpdatedAt(new Date(ts.getTime()));

        return h;
    }
    // getters / setters

    public UUID getId() {
        return id;
    }

    public Hashtag setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Hashtag setName(String name) {
        this.name = name;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Hashtag setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Hashtag setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

}
