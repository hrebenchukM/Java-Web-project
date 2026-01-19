package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Language {
    private UUID id;
    private String name;
    private Date createdAt;

    public static Language fromResultSet(ResultSet rs) throws SQLException {
        Language l = new Language();
        l.setId(UUID.fromString(rs.getString("language_id")));
        l.setName(rs.getString("name"));

        Timestamp ts = rs.getTimestamp("created_at");
        l.setCreatedAt(new Date(ts.getTime()));

        return l;
    }

    
        public UUID getId() {
        return id;
    }

    public Language setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Language setName(String name) {
        this.name = name;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Language setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }


}
