package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class JobSearchResult {
    private UUID id;
    private UUID searchId;
    private UUID vacancyId;
    private int orderIndex;
    private Date createdAt;
    private Date deletedAt;

    public static JobSearchResult fromResultSet(ResultSet rs) throws SQLException {
        JobSearchResult r = new JobSearchResult();
        r.setId(UUID.fromString(rs.getString("job_search_result_id")));
        r.setSearchId(UUID.fromString(rs.getString("search_id")));
        r.setVacancyId(UUID.fromString(rs.getString("vacancy_id")));
        r.setOrderIndex(rs.getInt("order_index"));

        Timestamp ts;
        ts = rs.getTimestamp("created_at");
        r.setCreatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("deleted_at");
        if (ts != null) r.setDeletedAt(new Date(ts.getTime()));

        return r;
    }
    // getters / setters

    public UUID getId() {
        return id;
    }

    public JobSearchResult setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getSearchId() {
        return searchId;
    }

    public JobSearchResult setSearchId(UUID searchId) {
        this.searchId = searchId;
        return this;
    }

    public UUID getVacancyId() {
        return vacancyId;
    }

    public JobSearchResult setVacancyId(UUID vacancyId) {
        this.vacancyId = vacancyId;
        return this;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public JobSearchResult setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public JobSearchResult setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public JobSearchResult setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

}
