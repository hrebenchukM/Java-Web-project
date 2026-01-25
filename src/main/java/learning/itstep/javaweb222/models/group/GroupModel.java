package learning.itstep.javaweb222.models.group;

import java.util.List;
import java.util.UUID;

public class GroupModel {
    private UUID groupId;
    private String name;
    private String description;
    private String avatarUrl;
    private int membersCount;
    private UUID ownerId;
    
    private int postsPerWeek;
    private String cover;
    private List<String> rules;

    public int getPostsPerWeek() {
        return postsPerWeek;
    }

    public GroupModel setPostsPerWeek(int postsPerWeek) {
        this.postsPerWeek = postsPerWeek;
         return this;
    }

    public String getCover() {
        return cover;
    }

    public GroupModel setCover(String cover) {
        this.cover = cover;
         return this;
    }

    public List<String> getRules() {
        return rules;
    }

    public GroupModel setRules(List<String> rules) {
        this.rules = rules;
         return this;
    }

    
    public UUID getGroupId() {
        return groupId;
    }

    public GroupModel setGroupId(UUID groupId) {
        this.groupId = groupId;
        return this;
    }

    public String getName() {
        return name;
    }

    public GroupModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public GroupModel setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public GroupModel setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public int getMembersCount() {
        return membersCount;
    }

    public GroupModel setMembersCount(int membersCount) {
        this.membersCount = membersCount;
        return this;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public GroupModel setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
        return this;
    }
}
