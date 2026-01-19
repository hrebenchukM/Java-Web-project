package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class JobApplication {
    private UUID id;
    private UUID vacancyId;
    private UUID userId;
    private String status;
    private Date appliedAt;
    private Date statusChangedAt;
    private Date withdrawnAt;

    public static JobApplication fromResultSet(ResultSet rs) throws SQLException {
        JobApplication ja = new JobApplication();
        ja.setId(UUID.fromString(rs.getString("job_application_id")));
        ja.setVacancyId(UUID.fromString(rs.getString("vacancy_id")));
        ja.setUserId(UUID.fromString(rs.getString("user_id")));
        ja.setStatus(rs.getString("status"));

        Timestamp ts;
        ts = rs.getTimestamp("applied_at");
        ja.setAppliedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("status_changed_at");
        if (ts != null) ja.setStatusChangedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("withdrawn_at");
        if (ts != null) ja.setWithdrawnAt(new Date(ts.getTime()));

        return ja;
    }

     // getters / setters

    public UUID getId() {
        return id;
    }

    public JobApplication setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getVacancyId() {
        return vacancyId;
    }

    public JobApplication setVacancyId(UUID vacancyId) {
        this.vacancyId = vacancyId;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public JobApplication setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public JobApplication setStatus(String status) {
        this.status = status;
        return this;
    }

    public Date getAppliedAt() {
        return appliedAt;
    }

    public JobApplication setAppliedAt(Date appliedAt) {
        this.appliedAt = appliedAt;
        return this;
    }

    public Date getStatusChangedAt() {
        return statusChangedAt;
    }

    public JobApplication setStatusChangedAt(Date statusChangedAt) {
        this.statusChangedAt = statusChangedAt;
        return this;
    }

    public Date getWithdrawnAt() {
        return withdrawnAt;
    }

    public JobApplication setWithdrawnAt(Date withdrawnAt) {
        this.withdrawnAt = withdrawnAt;
        return this;
    }

    
}
