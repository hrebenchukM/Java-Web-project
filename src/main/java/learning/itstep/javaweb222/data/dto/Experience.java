package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Experience {
    private UUID id;
    private UUID userId;
    private UUID companyId;
    private String position;
    private String employmentType;
    private String workLocationType;
    private String location;
    private Date startDate;
    private Date endDate;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    public static Experience fromResultSet(ResultSet rs) throws SQLException {
        Experience e = new Experience();
        e.setId(UUID.fromString(rs.getString("experience_id")));
        e.setUserId(UUID.fromString(rs.getString("user_id")));
        e.setCompanyId(UUID.fromString(rs.getString("company_id")));
        e.setPosition(rs.getString("position"));
        e.setEmploymentType(rs.getString("employment_type"));
        e.setWorkLocationType(rs.getString("work_location_type"));
        e.setLocation(rs.getString("location"));
        e.setStartDate(rs.getDate("start_date"));
        e.setEndDate(rs.getDate("end_date"));
        e.setDescription(rs.getString("description"));

        Timestamp ts;
        ts = rs.getTimestamp("created_at");
        e.setCreatedAt(new java.util.Date(ts.getTime()));

        ts = rs.getTimestamp("updated_at");
        if (ts != null) e.setUpdatedAt(new java.util.Date(ts.getTime()));

        ts = rs.getTimestamp("deleted_at");
        if (ts != null) e.setDeletedAt(new java.util.Date(ts.getTime()));

        return e;
    }

    // getters / setters

    public UUID getId() {
        return id;
    }

    public Experience setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public Experience setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public Experience setCompanyId(UUID companyId) {
        this.companyId = companyId;
        return this;
    }

    public String getPosition() {
        return position;
    }

    public Experience setPosition(String position) {
        this.position = position;
        return this;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public Experience setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
        return this;
    }

    public String getWorkLocationType() {
        return workLocationType;
    }

    public Experience setWorkLocationType(String workLocationType) {
        this.workLocationType = workLocationType;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public Experience setLocation(String location) {
        this.location = location;
        return this;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Experience setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Experience setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Experience setDescription(String description) {
        this.description = description;
        return this;
    }

    public java.util.Date getCreatedAt() {
        return createdAt;
    }

    public Experience setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public java.util.Date getUpdatedAt() {
        return updatedAt;
    }

    public Experience setUpdatedAt(java.util.Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public java.util.Date getDeletedAt() {
        return deletedAt;
    }

    public Experience setDeletedAt(java.util.Date deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }
    
}
