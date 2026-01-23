package learning.itstep.javaweb222.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import java.util.UUID;

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
import learning.itstep.javaweb222.data.dto.Experience;
import learning.itstep.javaweb222.data.dto.Skill;
import learning.itstep.javaweb222.data.dto.UserSkill;
import learning.itstep.javaweb222.data.dto.UserLanguage;
import learning.itstep.javaweb222.models.profile.CertificateBlockModel;
import learning.itstep.javaweb222.models.profile.EducationBlockModel;
import learning.itstep.javaweb222.models.profile.ExperienceBlockModel;

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
    private CompanyDao companyDao;
    
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
            CompanyDao companyDao
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

}
