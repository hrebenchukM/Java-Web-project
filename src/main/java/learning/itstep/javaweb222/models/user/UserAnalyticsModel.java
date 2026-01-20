package learning.itstep.javaweb222.models.user;

public class UserAnalyticsModel {
    private int profileViews;
    private int postViews;

    public int getProfileViews() {
        return profileViews;
    }

    public UserAnalyticsModel setProfileViews(int profileViews) {
        this.profileViews = profileViews;
        return this;
    }

    public int getPostViews() {
        return postViews;
    }

    public UserAnalyticsModel setPostViews(int postViews) {
        this.postViews = postViews;
        return this;
    }
}
