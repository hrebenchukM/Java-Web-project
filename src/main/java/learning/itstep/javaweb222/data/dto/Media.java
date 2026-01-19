package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Media {
    private UUID id;
    private String url;
    private String type;
    private Date createdAt;

    public static Media fromResultSet(ResultSet rs) throws SQLException {
        Media m = new Media();
        m.setId(UUID.fromString(rs.getString("media_id")));
        m.setUrl(rs.getString("url"));
        m.setType(rs.getString("type"));

        Timestamp ts = rs.getTimestamp("created_at");
        m.setCreatedAt(new Date(ts.getTime()));

        return m;
    }

      // getters / setters

    public UUID getId() {
        return id;
    }

    public Media setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Media setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getType() {
        return type;
    }

    public Media setType(String type) {
        this.type = type;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Media setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

}
