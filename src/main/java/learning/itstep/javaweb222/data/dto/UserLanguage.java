package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class UserLanguage {
    private UUID id;
    private UUID userId;
    private UUID languageId;
    private String level;
    private Date createdAt;
    private Date updatedAt;

    private Language language;

    public static UserLanguage fromResultSet(ResultSet rs) throws SQLException {
        UserLanguage ul = new UserLanguage();
        ul.setId(UUID.fromString(rs.getString("user_language_id")));
        ul.setUserId(UUID.fromString(rs.getString("user_id")));
        ul.setLanguageId(UUID.fromString(rs.getString("language_id")));
        ul.setLevel(rs.getString("level"));

        Timestamp ts;
        ts = rs.getTimestamp("created_at");
        ul.setCreatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("updated_at");
        if (ts != null) ul.setUpdatedAt(new Date(ts.getTime()));

        try { ul.setLanguage(Language.fromResultSet(rs)); }
        catch (Exception ignore) {}

        return ul;
    }
    
        public UUID getId() {
        return id;
    }

    public UserLanguage setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public UserLanguage setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public UUID getLanguageId() {
        return languageId;
    }

    public UserLanguage setLanguageId(UUID languageId) {
        this.languageId = languageId;
        return this;
    }

    public String getLevel() {
        return level;
    }

    public UserLanguage setLevel(String level) {
        this.level = level;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public UserLanguage setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public UserLanguage setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Language getLanguage() {
        return language;
    }

    public UserLanguage setLanguage(Language language) {
        this.language = language;
        return this;
    }

    
}
