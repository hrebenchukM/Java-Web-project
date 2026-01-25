package learning.itstep.javaweb222.models.page;

import java.util.List;
import java.util.UUID;

public class PageFullModel {

    private PageBlockModel page;
    private List<UUID> admins;
    private int followersCount;
    private int postsCount;
    private boolean verified;

    // ---------------- getters ----------------

    public PageBlockModel getPage() {
        return page;
    }

    public List<UUID> getAdmins() {
        return admins;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getPostsCount() {
        return postsCount;
    }

    public boolean isVerified() {
        return verified;
    }

    // ---------------- fluent setters ----------------

    public PageFullModel setPage(PageBlockModel page) {
        this.page = page;
        return this;
    }

    public PageFullModel setAdmins(List<UUID> admins) {
        this.admins = admins;
        return this;
    }

    public PageFullModel setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
        return this;
    }

    public PageFullModel setPostsCount(int postsCount) {
        this.postsCount = postsCount;
        return this;
    }

    public PageFullModel setVerified(boolean verified) {
        this.verified = verified;
        return this;
    }
}
