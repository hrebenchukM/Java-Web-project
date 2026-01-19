package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Vacancy {
    private UUID id;
    private UUID companyId;
    private UUID postedBy;
    private String title;
    private String jobType;
    private String schedule;
    private String location;
    private Integer salaryFrom;
    private Integer salaryTo;
    private String salaryCurrency;
    private String description;
    private Date postedAt;
    private Date updatedAt;
    private Date deletedAt;

    public static Vacancy fromResultSet(ResultSet rs) throws SQLException {
        Vacancy v = new Vacancy();
        v.setId(UUID.fromString(rs.getString("vacancy_id")));
        v.setCompanyId(UUID.fromString(rs.getString("company_id")));
        v.setPostedBy(UUID.fromString(rs.getString("posted_by")));
        v.setTitle(rs.getString("title"));
        v.setJobType(rs.getString("job_type"));
        v.setSchedule(rs.getString("schedule"));
        v.setLocation(rs.getString("location"));
        v.setSalaryFrom((Integer) rs.getObject("salary_from"));
        v.setSalaryTo((Integer) rs.getObject("salary_to"));
        v.setSalaryCurrency(rs.getString("salary_currency"));
        v.setDescription(rs.getString("description"));

        Timestamp ts;
        ts = rs.getTimestamp("posted_at");
        v.setPostedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("updated_at");
        if (ts != null) v.setUpdatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("deleted_at");
        if (ts != null) v.setDeletedAt(new Date(ts.getTime()));

        return v;
    }

       // getters / setters

    public UUID getId() {
        return id;
    }

    public Vacancy setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public Vacancy setCompanyId(UUID companyId) {
        this.companyId = companyId;
        return this;
    }

    public UUID getPostedBy() {
        return postedBy;
    }

    public Vacancy setPostedBy(UUID postedBy) {
        this.postedBy = postedBy;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Vacancy setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getJobType() {
        return jobType;
    }

    public Vacancy setJobType(String jobType) {
        this.jobType = jobType;
        return this;
    }

    public String getSchedule() {
        return schedule;
    }

    public Vacancy setSchedule(String schedule) {
        this.schedule = schedule;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public Vacancy setLocation(String location) {
        this.location = location;
        return this;
    }

    public Integer getSalaryFrom() {
        return salaryFrom;
    }

    public Vacancy setSalaryFrom(Integer salaryFrom) {
        this.salaryFrom = salaryFrom;
        return this;
    }

    public Integer getSalaryTo() {
        return salaryTo;
    }

    public Vacancy setSalaryTo(Integer salaryTo) {
        this.salaryTo = salaryTo;
        return this;
    }

    public String getSalaryCurrency() {
        return salaryCurrency;
    }

    public Vacancy setSalaryCurrency(String salaryCurrency) {
        this.salaryCurrency = salaryCurrency;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Vacancy setDescription(String description) {
        this.description = description;
        return this;
    }

    public Date getPostedAt() {
        return postedAt;
    }

    public Vacancy setPostedAt(Date postedAt) {
        this.postedAt = postedAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Vacancy setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public Vacancy setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

}
