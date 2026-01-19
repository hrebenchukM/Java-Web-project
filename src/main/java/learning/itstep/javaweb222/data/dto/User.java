package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class User {
   private UUID id;
    private String email;
    private String phone;

    private String authProvider;
    private String providerId;

    private String firstName;
    private String secondName;

    private String avatarUrl;
    private String headerUrl;

    private String profileTitle;
    private String headline;
    private String genInfo;

    private String university;
    private String location;
    private String portfolioUrl;

    private boolean isCompany;

    private Date registeredAt;
    private Date updatedAt;
    private Date deletedAt;
    
    
  // ---------- factory ----------

    public static User fromResultSet(ResultSet rs) throws SQLException {
        User user = new User();

        user.setId(UUID.fromString(rs.getString("user_id")));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));

        user.setAuthProvider(rs.getString("auth_provider"));
        user.setProviderId(rs.getString("provider_id"));

        user.setFirstName(rs.getString("first_name"));
        user.setSecondName(rs.getString("second_name"));

        user.setAvatarUrl(rs.getString("avatar_url"));
        user.setHeaderUrl(rs.getString("header_url"));

        user.setProfileTitle(rs.getString("profile_title"));
        user.setHeadline(rs.getString("headline"));
        user.setGenInfo(rs.getString("gen_info"));

        user.setUniversity(rs.getString("university"));
        user.setLocation(rs.getString("location"));
        user.setPortfolioUrl(rs.getString("portfolio_url"));

        user.setIsCompany(rs.getBoolean("is_company"));

        Timestamp ts;

        ts = rs.getTimestamp("registered_at");
        if (ts != null) user.setRegisteredAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("updated_at");
        if (ts != null) user.setUpdatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("deleted_at");
        if (ts != null) user.setDeletedAt(new Date(ts.getTime()));

        return user;
    }

    public UUID getId() {
        return id;
    }

    public User setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User  setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public User  setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public User  setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
        return this;
    }

    public String getProviderId() {
        return providerId;
    }

    public User  setProviderId(String providerId) {
        this.providerId = providerId;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public User  setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getSecondName() {
        return secondName;
    }

    public User  setSecondName(String secondName) {
        this.secondName = secondName;
        return this;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public User  setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public String getHeaderUrl() {
        return headerUrl;
    }

    public User  setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
        return this;
    }

    public String getProfileTitle() {
        return profileTitle;
    }

    public User  setProfileTitle(String profileTitle) {
        this.profileTitle = profileTitle;
        return this;
    }

    public String getHeadline() {
        return headline;
    }

    public User  setHeadline(String headline) {
        this.headline = headline;
        return this;
    }

    public String getGenInfo() {
        return genInfo;
    }

    public User  setGenInfo(String genInfo) {
        this.genInfo = genInfo;
        return this;
    }

    public String getUniversity() {
        return university;
    }

    public User  setUniversity(String university) {
        this.university = university;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public User  setLocation(String location) {
        this.location = location;
        return this;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public User  setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
        return this;
    }

    public boolean isIsCompany() {
        return isCompany;
    }

    public User  setIsCompany(boolean isCompany) {
        this.isCompany = isCompany;
        return this;
    }

    public Date getRegisteredAt() {
        return registeredAt;
    }

    public User  setRegisteredAt(Date registeredAt) {
        this.registeredAt = registeredAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public User  setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public User  setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }
    
    
    
    
    
}