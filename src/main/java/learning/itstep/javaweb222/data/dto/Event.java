package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Event {
    private UUID id;

    private String organizerType;
    private UUID organizerId;

    private String title;
    private String description;

    private String coverImageUrl;
    private String location;
    private byte isOnline;
    private String externalLink;
    private String timezone;

    private String visibility;
    private byte allowComments;

    private Date startAt;
    private Date endAt;

    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    
     
    public static Event fromResultSet(ResultSet rs) throws SQLException {
        Event e = new Event();
        e.setId(UUID.fromString(rs.getString("event_id")));

        e.setOrganizerType(rs.getString("organizer_type"));
        e.setOrganizerId(UUID.fromString(rs.getString("organizer_id")));

        e.setTitle(rs.getString("title"));
        e.setDescription(rs.getString("description"));

        e.setCoverImageUrl(rs.getString("cover_image_url"));
        e.setLocation(rs.getString("location"));
        e.setIsOnline(rs.getByte("is_online"));
        e.setExternalLink(rs.getString("external_link"));
        e.setTimezone(rs.getString("timezone"));

        e.setVisibility(rs.getString("visibility"));
        e.setAllowComments(rs.getByte("allow_comments"));

        Timestamp ts;
        ts = rs.getTimestamp("start_at");
        e.setStartAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("end_at");
        if (ts != null) e.setEndAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("created_at");
        e.setCreatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("updated_at");
        if (ts != null) e.setUpdatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("deleted_at");
        if (ts != null) e.setDeletedAt(new Date(ts.getTime()));

        return e;
    }

       // getters / setters

    public UUID getId() {
        return id;
    }

    public Event setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getOrganizerType() {
        return organizerType;
    }

    public Event setOrganizerType(String organizerType) {
        this.organizerType = organizerType;
        return this;
    }

    public UUID getOrganizerId() {
        return organizerId;
    }

    public Event setOrganizerId(UUID organizerId) {
        this.organizerId = organizerId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Event setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Event setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public Event setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public Event setLocation(String location) {
        this.location = location;
        return this;
    }

    public byte getIsOnline() {
        return isOnline;
    }

    public Event setIsOnline(byte isOnline) {
        this.isOnline = isOnline;
        return this;
    }

    public String getExternalLink() {
        return externalLink;
    }

    public Event setExternalLink(String externalLink) {
        this.externalLink = externalLink;
        return this;
    }

    public String getTimezone() {
        return timezone;
    }

    public Event setTimezone(String timezone) {
        this.timezone = timezone;
        return this;
    }

    public String getVisibility() {
        return visibility;
    }

    public Event setVisibility(String visibility) {
        this.visibility = visibility;
        return this;
    }

    public byte getAllowComments() {
        return allowComments;
    }

    public Event setAllowComments(byte allowComments) {
        this.allowComments = allowComments;
        return this;
    }

    public Date getStartAt() {
        return startAt;
    }

    public Event setStartAt(Date startAt) {
        this.startAt = startAt;
        return this;
    }

    public Date getEndAt() {
        return endAt;
    }

    public Event setEndAt(Date endAt) {
        this.endAt = endAt;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Event setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Event setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public Event setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

}
