package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class UserVacancyFavorite {
    private UUID id;
    private UUID userId;
    private UUID vacancyId;
    private Date createdAt;
    private Date deletedAt;

    public static UserVacancyFavorite fromResultSet(ResultSet rs) throws SQLException {
        UserVacancyFavorite f = new UserVacancyFavorite();
        f.setId(UUID.fromString(rs.getString("uvf_id")));
        f.setUserId(UUID.fromString(rs.getString("user_id")));
        f.setVacancyId(UUID.fromString(rs.getString("vacancy_id")));

        Timestamp ts;
        ts = rs.getTimestamp("created_at");
        f.setCreatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("deleted_at");
        if (ts != null) f.setDeletedAt(new Date(ts.getTime()));

        return f;
    }

     // getters / setters

    public UUID getId() {
        return id;
    }

    public UserVacancyFavorite setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public UserVacancyFavorite setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public UUID getVacancyId() {
        return vacancyId;
    }

    public UserVacancyFavorite setVacancyId(UUID vacancyId) {
        this.vacancyId = vacancyId;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public UserVacancyFavorite setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public UserVacancyFavorite setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

}
