package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.UUID;

public class Certificate {
    private UUID id;
    private UUID userId;
    private UUID academyId;
    private String name;
    private String downloadRef;
    private Date issueDate;
    private Date expiryDate;
    private String accreditationId;
    private String organizationUrl;
    private java.util.Date createdAt;
    private java.util.Date updatedAt;
    private java.util.Date deletedAt;

    public static Certificate fromResultSet(ResultSet rs) throws SQLException {
        Certificate c = new Certificate();
        c.setId(UUID.fromString(rs.getString("certificate_id")));
        c.setUserId(UUID.fromString(rs.getString("user_id")));

        String academyId = rs.getString("academy_id");
        if (academyId != null) {
            c.setAcademyId(UUID.fromString(academyId));
        }

        c.setName(rs.getString("name"));
        c.setDownloadRef(rs.getString("download_ref"));
        c.setIssueDate(rs.getDate("issue_date"));
        c.setExpiryDate(rs.getDate("expiry_date"));
        c.setAccreditationId(rs.getString("accreditation_id"));
        c.setOrganizationUrl(rs.getString("organization_url"));

        Timestamp ts;
        ts = rs.getTimestamp("created_at");
        c.setCreatedAt(new java.util.Date(ts.getTime()));

        ts = rs.getTimestamp("updated_at");
        if (ts != null) c.setUpdatedAt(new java.util.Date(ts.getTime()));

        ts = rs.getTimestamp("deleted_at");
        if (ts != null) c.setDeletedAt(new java.util.Date(ts.getTime()));

        return c;
    }

       // getters / setters

    public UUID getId() {
        return id;
    }

    public Certificate setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public Certificate setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public UUID getAcademyId() {
        return academyId;
    }

    public Certificate setAcademyId(UUID academyId) {
        this.academyId = academyId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Certificate setName(String name) {
        this.name = name;
        return this;
    }

    public String getDownloadRef() {
        return downloadRef;
    }

    public Certificate setDownloadRef(String downloadRef) {
        this.downloadRef = downloadRef;
        return this;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public Certificate setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
        return this;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public Certificate setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }

    public String getAccreditationId() {
        return accreditationId;
    }

    public Certificate setAccreditationId(String accreditationId) {
        this.accreditationId = accreditationId;
        return this;
    }

    public String getOrganizationUrl() {
        return organizationUrl;
    }

    public Certificate setOrganizationUrl(String organizationUrl) {
        this.organizationUrl = organizationUrl;
        return this;
    }

    public java.util.Date getCreatedAt() {
        return createdAt;
    }

    public Certificate setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public java.util.Date getUpdatedAt() {
        return updatedAt;
    }

    public Certificate setUpdatedAt(java.util.Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public java.util.Date getDeletedAt() {
        return deletedAt;
    }

    public Certificate setDeletedAt(java.util.Date deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

}
