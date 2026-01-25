package learning.itstep.javaweb222.models.page;

import java.util.UUID;

public class PageBlockModel {

    private UUID pageId;
    private String name;
    private String description;
    private String logoUrl;
    private int followersCount;

    // ---------------- getters ----------------

    public UUID getPageId() {
        return pageId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    // ---------------- fluent setters ----------------

    public PageBlockModel setPageId(UUID pageId) {
        this.pageId = pageId;
        return this;
    }

    public PageBlockModel setName(String name) {
        this.name = name;
        return this;
    }

    public PageBlockModel setDescription(String description) {
        this.description = description;
        return this;
    }

    public PageBlockModel setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
        return this;
    }

    public PageBlockModel setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
        return this;
    }
}
