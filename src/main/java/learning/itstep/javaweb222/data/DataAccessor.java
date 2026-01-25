package learning.itstep.javaweb222.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import java.util.UUID;
import learning.itstep.javaweb222.data.activity.UserActivityDao;

import learning.itstep.javaweb222.data.core.DbInstaller;
import learning.itstep.javaweb222.data.core.DbSeeder;

import learning.itstep.javaweb222.data.user.UserDao;
import learning.itstep.javaweb222.data.profile.ProfileDao;
import learning.itstep.javaweb222.data.post.PostDao;
import learning.itstep.javaweb222.data.chat.ChatDao;
import learning.itstep.javaweb222.data.company.CompanyDao;
import learning.itstep.javaweb222.data.message.MessageDao;
import learning.itstep.javaweb222.data.notification.NotificationDao;

import learning.itstep.javaweb222.data.dto.AccessToken;
import learning.itstep.javaweb222.data.dto.AuthCredential;
import learning.itstep.javaweb222.data.dto.Certificate;
import learning.itstep.javaweb222.data.dto.User;
import learning.itstep.javaweb222.data.dto.Post;
import learning.itstep.javaweb222.data.dto.Chat;
import learning.itstep.javaweb222.data.dto.Message;
import learning.itstep.javaweb222.data.dto.MessageMedia;
import learning.itstep.javaweb222.data.dto.MessageRead;
import learning.itstep.javaweb222.data.dto.Notification;
import learning.itstep.javaweb222.data.dto.Education;
import learning.itstep.javaweb222.data.dto.Event;
import learning.itstep.javaweb222.data.dto.EventAttendee;
import learning.itstep.javaweb222.data.dto.Experience;
import learning.itstep.javaweb222.data.dto.GroupMember;
import learning.itstep.javaweb222.data.dto.Media;
import learning.itstep.javaweb222.data.dto.Skill;
import learning.itstep.javaweb222.data.dto.UserActivity;
import learning.itstep.javaweb222.data.dto.UserSkill;
import learning.itstep.javaweb222.data.dto.UserLanguage;
import learning.itstep.javaweb222.data.dto.Vacancy;
import learning.itstep.javaweb222.data.event.EventDao;
import learning.itstep.javaweb222.data.group.GroupDao;
import learning.itstep.javaweb222.data.media.MediaDao;
import learning.itstep.javaweb222.data.network.NetworkDao;
import learning.itstep.javaweb222.data.page.PageDao;
import learning.itstep.javaweb222.data.portfolio.PortfolioDao;
import learning.itstep.javaweb222.data.vacancy.VacancyDao;
import learning.itstep.javaweb222.models.event.EventBlockModel;
import learning.itstep.javaweb222.models.event.EventFullModel;
import learning.itstep.javaweb222.models.group.GroupBlockModel;
import learning.itstep.javaweb222.models.group.GroupModel;
import learning.itstep.javaweb222.models.page.PageBlockModel;
import learning.itstep.javaweb222.models.profile.CertificateBlockModel;
import learning.itstep.javaweb222.models.profile.EducationBlockModel;
import learning.itstep.javaweb222.models.profile.ExperienceBlockModel;
import learning.itstep.javaweb222.models.profile.RecommendationBlockModel;

@Singleton
public class DataAccessor {

    private final DbSeeder dbSeeder;
    private final DbInstaller dbInstaller;

    private final UserDao userDao;
    private final ProfileDao profileDao;
    private final PostDao postDao;
    private final ChatDao chatDao;
    private final MessageDao messageDao;
    private final NotificationDao notificationDao;
    private final CompanyDao companyDao;
    private final VacancyDao vacancyDao;
    private final MediaDao  mediaDao;
    private final NetworkDao networkDao;
    private final UserActivityDao userActivityDao;
    private final GroupDao groupDao;
    private final PageDao pageDao;
    private final EventDao eventDao;
    private final PortfolioDao portfolioDao;

 @Inject
public DataAccessor(
        DbSeeder dbSeeder,
        DbInstaller dbInstaller,
        UserDao userDao,
        ProfileDao profileDao,
        PostDao postDao,
        ChatDao chatDao,
        MessageDao messageDao,
        NotificationDao notificationDao,
        CompanyDao companyDao,
        VacancyDao vacancyDao,
        MediaDao mediaDao,
        NetworkDao networkDao,
        UserActivityDao userActivityDao,

        GroupDao groupDao,
        PageDao pageDao,
        EventDao eventDao,
        PortfolioDao portfolioDao 
) {

    this.dbSeeder = dbSeeder;
    this.dbInstaller = dbInstaller;
    this.userDao = userDao;
    this.profileDao = profileDao;
    this.postDao = postDao;
    this.chatDao = chatDao;
    this.messageDao = messageDao;
    this.notificationDao = notificationDao;
    this.companyDao = companyDao;
    this.vacancyDao = vacancyDao;
    this.mediaDao = mediaDao;
    this.networkDao = networkDao;
    this.userActivityDao = userActivityDao;

    this.groupDao = groupDao;
    this.pageDao = pageDao;
    this.eventDao = eventDao;
     this.portfolioDao = portfolioDao;
}


    // ================= INSTALL / SEED =================

    public boolean install() {
        return dbInstaller.install();
    }

    public boolean seed() {
        return dbSeeder.seed();
    }

    // ================= USER / AUTH =================

    public User getUserById(String userId) {
        return userDao.getUserById(userId);
    }

    public AuthCredential getUserAccessByCredentials(String login, String password) {
        return userDao.getUserAccessByCredentials(login, password);
    }

    public AuthCredential getUserAccess(String userId, String roleId) {
        return userDao.getUserAccess(userId, roleId);
    }

    public AccessToken createAccessToken(
            AuthCredential ua,
            String userAgent,
            String ip
    ) {
        return userDao.createAccessToken(ua, userAgent, ip);
    }

  // ================= VACANCIES =================

public List<Vacancy> getVacancies() {
    return vacancyDao.getAllWithCompany();
}

public List<Vacancy> getBestVacancies(int limit) {
    return vacancyDao.getBestWithCompany(limit);
}

public void addVacancy(
        String userId,
        String companyName,
        Vacancy vacancy
) throws Exception {

    UUID companyId = companyDao.getOrCreateCompanyByName(
            companyName,
            userId
    );

    vacancy
        .setCompanyId(companyId)
        .setPostedBy(UUID.fromString(userId));

    vacancyDao.addVacancy(vacancy);
}



public void attachMediaToPost(UUID postId, String fileName, String type) {
    Media media = new Media()
        .setUrl(fileName)
        .setType(type);

    UUID mediaId = mediaDao.addMedia(media);
    postDao.attachMedia(postId, mediaId);
}

    // ================= PROFILE =================

   
    public void addExperience(String userId, Experience experience) throws Exception {
        profileDao.addExperience(userId, experience);
    }

    public UUID getOrCreateCompanyByName(String name, String ownerUserId) throws Exception {
        return companyDao.getOrCreateCompanyByName(name, ownerUserId);
    }

    public List<ExperienceBlockModel> getUserExperienceBlocks(String userId) {
        return profileDao.getExperienceBlocksByUser(userId);
    }


    public List<EducationBlockModel> getUserEducations(String userId) {
        return profileDao.getEducationBlocksByUser(userId);
    }


    public void addEducation(String userId, Education education) throws Exception {
        profileDao.addEducation(userId, education);
    }

    public List<UserSkill> getUserSkills(String userId) {
        return profileDao.getSkillsByUser(userId);
    }


    public List<UserLanguage> getUserLanguages(String userId) {
        return profileDao.getLanguagesByUser(userId);
    }


// ================= POSTS =================

    public Post getPostById(String postId) {
        return postDao.getPostById(postId);
    }

    public List<Post> getFeed(int page, int perPage) {
        return postDao.getFeed(page, perPage);
    }

    public int getFeedCount() {
        return postDao.getFeedCount();
    }

    public List<Post> getUserPosts(String userId) {
        return postDao.getUserPosts(userId);
    }

    public int getUserPostsCount(String userId) {
        return postDao.getUserPostsCount(userId);
    }

    public Post addPost(Post post) {
        return postDao.addPost(post);
    }

    public void deletePost(String postId) throws Exception {
        postDao.deletePost(postId);
    }



    // ================= CHATS =================

    public Chat getChatById(String chatId) {
        return chatDao.getChatById(chatId);
    }

    public List<Chat> getUserChats(String userId) {
        return chatDao.getUserChats(userId);
    }

    public Chat createChat(String creatorUserId) {
        return chatDao.createChat(creatorUserId);
    }

    public void addChatMember(String chatId, String userId) {
        chatDao.addMember(chatId, userId);
    }

    public void leaveChat(String chatId, String userId) {
        chatDao.leaveChat(chatId, userId);
    }

    public void deleteChat(String chatId) {
        chatDao.deleteChat(chatId);
    }

    // ================= MESSAGES =================

    public List<Message> getChatMessages(String chatId) {
        return messageDao.getChatMessages(chatId);
    }

    public Message getMessageById(String messageId) {
        return messageDao.getMessageById(messageId);
    }

    
    public void addMessage(Message message) throws Exception {
        messageDao.addMessage(message);
    }
    public void editMessage(String messageId, String content) {
        messageDao.editMessage(messageId, content);
    }

    public void deleteMessage(String messageId) {
        messageDao.deleteMessage(messageId);
    }

    public void addMessageMedia(String messageId, String mediaId) {
        messageDao.addMessageMedia(messageId, mediaId);
    }

    public List<MessageMedia> getMessageMedia(String messageId) {
        return messageDao.getMessageMedia(messageId);
    }

    public void markMessageAsRead(String messageId, String userId) {
        messageDao.markAsRead(messageId, userId);
    }

    public List<MessageRead> getMessageReads(String messageId) {
        return messageDao.getMessageReads(messageId);
    }

   // ================= NOTIFICATIONS =================

public List<Notification> getUserNotifications(String userId) {
    return notificationDao.getUserNotifications(userId);
}

public List<Notification> getVacancyNotifications(String userId) {
    return notificationDao.getVacancyNotifications(userId);
}

public List<Notification> getPublicationNotifications(String userId) {
    return notificationDao.getPublicationNotifications(userId);
}

public List<Notification> getMentionNotifications(String userId) {
    return notificationDao.getMentionNotifications(userId);
}

public int getUnreadNotificationsCount(String userId) {
    return notificationDao.getUnreadCount(userId);
}

public Notification getNotificationById(String notificationId) {
    return notificationDao.getById(notificationId);
}

public void addNotification(Notification notification) throws Exception {
    notificationDao.addNotification(notification);
}

public void markNotificationAsRead(String notificationId) {
    notificationDao.markAsRead(notificationId);
}

public void markAllNotificationsAsRead(String userId) {
    notificationDao.markAllAsRead(userId);
}

public void deleteNotification(String notificationId) {
    notificationDao.deleteNotification(notificationId);
}

    // ================= PROFILE ANALYTICS =================

    public int getProfileViewsCount(String userId) {
        return profileDao.getProfileViewsCount(userId);
    }

    public int getPostViewsCount(String userId) {
        return profileDao.getPostViewsCount(userId);
    }

    public List<CertificateBlockModel> getUserCertificates(String userId) {
        return profileDao.getCertificateBlocksByUser(userId);
    }

    public void addCertificate(String userId, Certificate certificate, String academyName) throws Exception {
        profileDao.addCertificate(userId, certificate, academyName);
    }

    // ================= PROFILE SKILLS =================


    public void addSkill(
        String userId,
        String name,
        String level,
        boolean isMain
    ) throws Exception {
        profileDao.addSkill(userId, name, level, isMain);
    }

    
    // ================= NETWORK =================

public List<User> getSuggestedUsers(String userId) {
    return networkDao.getSuggestedUsers(userId);
}

public boolean sendConnectionRequest(String fromUserId, String toUserId) {
    return networkDao.sendRequest(fromUserId, toUserId);
}

public boolean acceptConnection(String requesterId, String receiverId) {
    return networkDao.acceptRequest(requesterId, receiverId);
}

public boolean removeConnection(String userA, String userB) {
    return networkDao.removeConnection(userA, userB);
}

public String getConnectionStatus(String userA, String userB) {
    return networkDao.getConnectionStatus(userA, userB);
}
public List<User> getContacts(String userId) {
    return networkDao.getContacts(userId);
}

public List<User> getFollowing(String userId) {
    return networkDao.getFollowing(userId);
}


// ================= USER ACTIVITY / EVENTS =================

public List<UserActivity> getMyActivity(
        String userId,
        int limit,
        int offset
) {
    return userActivityDao.getMyActivity(userId, limit, offset);
}

public List<UserActivity> getNetworkActivity(
        String userId,
        int limit,
        int offset
) {
    return userActivityDao.getNetworkActivity(userId, limit, offset);
}


public List<GroupBlockModel> getMyGroups(String userId) {
    return groupDao.getMyGroups(userId);
}

public GroupModel getGroupById(String id) {
    return groupDao.getGroupById(id);
}
public List<GroupMember> getGroupMembers(String groupId){
    return groupDao.getGroupMembers(groupId);
}

public List<Post> getGroupPosts(String groupId) {
    return groupDao.getGroupPosts(groupId);
}


public List<PageBlockModel> getMyPages(String userId) {
    return pageDao.getMyPages(userId);
}

// ================= EVENTS =================

public List<EventBlockModel> getMyEvents(String userId) {
    return eventDao.getMyEvents(userId);
}

public Event getEventById(String eventId) {
    return eventDao.getEventById(eventId);
}

public EventFullModel getEventFull(String eventId) {

    Event event = eventDao.getEventById(eventId);
    if (event == null) return null;

    Object organizer = null;

    if ("user".equals(event.getOrganizerType())) {
        organizer = userDao.getUserById(
            event.getOrganizerId().toString()
        );
    }
    

    List<EventAttendee> attendees =
        eventDao.getEventAttendees(eventId);

    return new EventFullModel()
        .setEvent(event)
        .setOrganizer(organizer)
        .setAttendees(attendees)
        .setAttendeesCount(attendees.size())
        .setSchedule(eventDao.getEventSchedule(eventId))
        .setSpeakers(eventDao.getEventSpeakers(eventId));
}


// ================= PORTFOLIO =================

public List<ExperienceBlockModel> getPortfolioExperienceBlocks(String userId) {
    return portfolioDao.getExperienceBlocks(userId);
}

public List<EducationBlockModel> getPortfolioEducations(String userId) {
    return portfolioDao.getEducationBlocks(userId);
}

public List<CertificateBlockModel> getPortfolioCertificates(String userId) {
    return portfolioDao.getCertificateBlocks(userId);
}

public List<UserSkill> getPortfolioSkills(String userId) {
    return portfolioDao.getSkills(userId);
}

public List<UserLanguage> getPortfolioLanguages(String userId) {
    return portfolioDao.getLanguages(userId);
}

public List<RecommendationBlockModel> getPortfolioRecommendations(String userId) {
    return portfolioDao.getRecommendations(userId);
}


}
