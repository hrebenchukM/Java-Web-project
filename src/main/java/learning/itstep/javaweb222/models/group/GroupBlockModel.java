
package learning.itstep.javaweb222.models.group;

import java.util.UUID;

public class GroupBlockModel {
    private UUID groupId;
    private String name;
    private String description;
    private String avatarUrl;
    private int membersCount;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public GroupBlockModel setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
          return this;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public GroupBlockModel setGroupId(UUID groupId) {
        this.groupId = groupId;
        return this;
    }

    public String getName() {
        return name;
    }

    public GroupBlockModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public GroupBlockModel setDescription(String description) {
        this.description = description;
        return this;
    }

    public int getMembersCount() {
        return membersCount;
    }

    public GroupBlockModel setMembersCount(int membersCount) {
        this.membersCount = membersCount;
        return this;
    }

    
}
