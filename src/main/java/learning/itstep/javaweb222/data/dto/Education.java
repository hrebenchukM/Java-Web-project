package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Education {
    private UUID id;
    private UUID userId;
    private UUID academyId;
    private String institution;
    private String degree;
    private String fieldOfStudy;
    private Date startDate;
    private Date endDate;
    private String source;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    public static Education fromResultSet(ResultSet rs) throws SQLException {
        Education e = new Education();
        e.setId(UUID.fromString(rs.getString("education_id")));
        e.setUserId(UUID.fromString(rs.getString("user_id")));

        String academyId = rs.getString("academy_id");
        if (academyId != null) {
            e.setAcademyId(UUID.fromString(academyId));
        }

        e.setInstitution(rs.getString("institution"));
        e.setDegree(rs.getString("degree"));
        e.setFieldOfStudy(rs.getString("field_of_study"));
        e.setStartDate(rs.getDate("start_date"));
        e.setEndDate(rs.getDate("end_date"));
        e.setSource(rs.getString("source"));

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
    // getters / setters

    public UUID getId() {
        return id;
    }

    public Education setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public Education setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public UUID getAcademyId() {
        return academyId;
    }

    public Education setAcademyId(UUID academyId) {
        this.academyId = academyId;
        return this;
    }

    public String getInstitution() {
        return institution;
    }

    public Education setInstitution(String institution) {
        this.institution = institution;
        return this;
    }

    public String getDegree() {
        return degree;
    }

    public Education setDegree(String degree) {
        this.degree = degree;
        return this;
    }

    public String getFieldOfStudy() {
        return fieldOfStudy;
    }

    public Education setFieldOfStudy(String fieldOfStudy) {
        this.fieldOfStudy = fieldOfStudy;
        return this;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Education setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Education setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public String getSource() {
        return source;
    }

    public Education setSource(String source) {
        this.source = source;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Education setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Education setUpdatedAt(java.util.Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public Education setDeletedAt(java.util.Date deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

}
