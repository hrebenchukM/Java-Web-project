
package learning.itstep.javaweb222.data.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class DbInstaller {
    private final DbProvider db;
    private final Logger logger;

    @Inject
    public DbInstaller(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }
    
    private boolean exec(String sql, String tag) {
        try (Statement st = db.getConnection().createStatement()) {
            st.executeUpdate(sql);
            return true;
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "DbInstaller::{0} {1}",
                    new Object[]{tag, ex.getMessage() + " | " + sql});
            return false;
        }
    }
    
    
    public boolean install() {

        // ------------------ LinkedIn Core: Users ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS users ("
            + "user_id CHAR(36) PRIMARY KEY,"
            + "email VARCHAR(128) NOT NULL,"
            + "phone VARCHAR(32) NULL,"
            + "auth_provider VARCHAR(32) NOT NULL DEFAULT 'local',"
            + "provider_id VARCHAR(128) NULL,"
            + "first_name VARCHAR(64) NULL,"
            + "second_name VARCHAR(64) NULL,"
            + "avatar_url VARCHAR(256) NULL,"
            + "header_url VARCHAR(256) NULL,"
            + "profile_title VARCHAR(128) NULL,"
            + "headline VARCHAR(256) NULL,"
            + "gen_info TEXT NULL,"
            + "university VARCHAR(128) NULL,"
            + "location VARCHAR(128) NULL,"
            + "portfolio_url VARCHAR(256) NULL,"
            + "is_company TINYINT NOT NULL DEFAULT 0,"
            + "registered_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at DATETIME NULL,"
            + "deleted_at DATETIME NULL,"
            + "UNIQUE(email)"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "users"
        )) return false;

        // ------------------ AuthCredentials ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS auth_credentials ("
            + "auth_id CHAR(36) PRIMARY KEY,"
            + "user_id CHAR(36) NOT NULL,"
            + "role_id VARCHAR(32) NOT NULL,"
            + "login VARCHAR(128) NOT NULL,"
            + "salt VARCHAR(64) NOT NULL,"
            + "password_hash VARCHAR(256) NOT NULL,"
            + "auth_provider VARCHAR(32) NOT NULL DEFAULT 'local',"
            + "provider_id VARCHAR(128) NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "UNIQUE(login)"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "auth_credentials"
        )) return false;

        // ------------------ UserRoles ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS user_roles ("
            + "role_id VARCHAR(32) PRIMARY KEY,"
            + "description VARCHAR(256) NOT NULL,"
            + "can_create TINYINT NOT NULL DEFAULT 0,"
            + "can_read TINYINT NOT NULL DEFAULT 1,"
            + "can_update TINYINT NOT NULL DEFAULT 0,"
            + "can_delete TINYINT NOT NULL DEFAULT 0"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "user_roles"
        )) return false;

        // ------------------ AccessTokens ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS access_tokens ("
            + "token_id CHAR(36) PRIMARY KEY,"
            + "auth_credential_id CHAR(36) NOT NULL,"
            + "user_id CHAR(36) NOT NULL,"
            + "role_id VARCHAR(32) NOT NULL,"
            + "issued_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "expired_at DATETIME NULL,"
            + "revoked_at DATETIME NULL,"
            + "user_agent VARCHAR(256) NULL,"
            + "ip_address VARCHAR(64) NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "access_tokens"
        )) return false;

        // ------------------ MessageSettings ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS message_settings ("
            + "ms_id CHAR(36) PRIMARY KEY,"
            + "user_id CHAR(36) NOT NULL,"
            + "office_absence_enabled TINYINT NOT NULL DEFAULT 0,"
            + "office_absence_message TEXT NULL,"
            + "notifications_enabled TINYINT NOT NULL DEFAULT 1,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "message_settings"
        )) return false;

        // ------------------ ProfileViews ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS profile_views ("
            + "pv_id CHAR(36) PRIMARY KEY,"
            + "profile_owner_id CHAR(36) NOT NULL,"
            + "viewer_user_id CHAR(36) NULL,"
            + "viewer_ip VARCHAR(64) NULL,"
            + "viewer_user_agent VARCHAR(256) NULL,"
            + "source VARCHAR(32) NULL,"
            + "viewed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "profile_views"
        )) return false;

        // ------------------ PostViews ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS post_views ("
            + "post_view_id CHAR(36) PRIMARY KEY,"
            + "post_id CHAR(36) NOT NULL,"
            + "viewer_user_id CHAR(36) NULL,"
            + "viewer_ip VARCHAR(64) NULL,"
            + "viewer_user_agent VARCHAR(256) NULL,"
            + "source VARCHAR(32) NULL,"
            + "viewed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "post_views"
        )) return false;

        // ------------------ Professional: Companies ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS companies ("
            + "company_id CHAR(36) PRIMARY KEY,"
            + "owner_user_id CHAR(36) NULL,"
            + "name VARCHAR(128) NOT NULL,"
            + "logo_url VARCHAR(256) NULL,"
            + "industry VARCHAR(128) NULL,"
            + "location VARCHAR(128) NULL,"
            + "website_url VARCHAR(256) NULL,"
            + "description TEXT NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at DATETIME NULL,"
            + "deleted_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "companies"
        )) return false;

        
        // ------------------ Professional: Experiences ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS experiences ("
            + "experience_id CHAR(36) PRIMARY KEY,"
            + "user_id CHAR(36) NOT NULL,"
            + "company_id CHAR(36) NOT NULL,"
            + "position VARCHAR(128) NOT NULL,"
            + "employment_type VARCHAR(32) NULL,"
            + "work_location_type VARCHAR(32) NULL,"
            + "location VARCHAR(128) NULL,"
            + "start_date DATE NOT NULL,"
            + "end_date DATE NULL,"
            + "description TEXT NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at DATETIME NULL,"
            + "deleted_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "experiences"
        )) return false;
        
        // ------------------ Professional: Academies ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS academies ("
            + "academy_id CHAR(36) PRIMARY KEY,"
            + "name VARCHAR(128) NOT NULL,"
            + "logo_url VARCHAR(256) NULL,"
            + "website_url VARCHAR(256) NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "academies"
        )) return false;

        // ------------------ Professional: Educations ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS educations ("
            + "education_id CHAR(36) PRIMARY KEY,"
            + "user_id CHAR(36) NOT NULL,"
            + "academy_id CHAR(36) NULL,"
            + "institution VARCHAR(256) NULL,"
            + "degree VARCHAR(128) NULL,"
            + "field_of_study VARCHAR(128) NULL,"
            + "start_date DATE NULL,"
            + "end_date DATE NULL,"
            + "source VARCHAR(64) NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at DATETIME NULL,"
            + "deleted_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "educations"
        )) return false;

        
        // ------------------ Professional: Certificates ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS certificates ("
            + "certificate_id CHAR(36) PRIMARY KEY,"
            + "user_id CHAR(36) NOT NULL,"
            + "academy_id CHAR(36) NULL,"
            + "name VARCHAR(256) NOT NULL,"
            + "download_ref VARCHAR(256) NULL,"
            + "issue_date DATE NULL,"
            + "expiry_date DATE NULL,"
            + "accreditation_id VARCHAR(128) NULL,"
            + "organization_url VARCHAR(256) NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at DATETIME NULL,"
            + "deleted_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "certificates"
        )) return false;


        // ------------------ Skills: Global ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS skills ("
            + "skill_id CHAR(36) PRIMARY KEY,"
            + "name VARCHAR(128) NOT NULL,"
            + "description VARCHAR(256) NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "skills"
        )) return false;

        
        // ------------------ Skills: UserSkills ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS user_skills ("
            + "user_skill_id CHAR(36) PRIMARY KEY,"
            + "user_id CHAR(36) NOT NULL,"
            + "skill_id CHAR(36) NOT NULL,"
            + "level VARCHAR(32) NULL,"
            + "is_main TINYINT NOT NULL DEFAULT 0,"
            + "order_index INT NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "user_skills"
        )) return false;

        
        // ------------------ Skills: CertificateSkills ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS certificate_skills ("
            + "certificate_skill_id CHAR(36) PRIMARY KEY,"
            + "certificate_id CHAR(36) NOT NULL,"
            + "skill_id CHAR(36) NOT NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "certificate_skills"
        )) return false;

        
        // ------------------ Languages: Dictionary ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS languages ("
            + "language_id CHAR(36) PRIMARY KEY,"
            + "name VARCHAR(64) NOT NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "languages"
        )) return false;


        
        // ------------------ Languages: UserLanguages ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS user_languages ("
            + "user_language_id CHAR(36) PRIMARY KEY,"
            + "user_id CHAR(36) NOT NULL,"
            + "language_id CHAR(36) NOT NULL,"
            + "level VARCHAR(64) NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "user_languages"
        )) return false;


        // ------------------ Skills: Recommended By Position ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS recommended_skills_by_position ("
            + "rsp_id CHAR(36) PRIMARY KEY,"
            + "position VARCHAR(128) NOT NULL,"
            + "skill_id CHAR(36) NOT NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "recommended_skills_by_position"
        )) return false;


        // ------------------ Recommendations ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS recommendations ("
            + "recommendation_id CHAR(36) PRIMARY KEY,"
            + "author_id CHAR(36) NOT NULL,"
            + "user_id CHAR(36) NOT NULL,"
            + "text TEXT NOT NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at DATETIME NULL,"
            + "deleted_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "recommendations"
        )) return false;

        // ------------------ Network: Contacts ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS contacts ("
            + "contact_id CHAR(36) PRIMARY KEY,"
            + "requester_id CHAR(36) NOT NULL,"
            + "receiver_id CHAR(36) NOT NULL,"
            + "status VARCHAR(32) NOT NULL,"
            + "requested_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "responded_at DATETIME NULL,"
            + "status_changed_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "contacts"
        )) return false;

        // ------------------ Network: Follows ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS follows ("
            + "follow_id CHAR(36) PRIMARY KEY,"
            + "follower_id CHAR(36) NOT NULL,"
            + "following_id CHAR(36) NOT NULL,"
            + "followed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "unfollowed_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "follows"
        )) return false;

        
        // ------------------ Network: BlockedUsers ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS blocked_users ("
            + "block_id CHAR(36) PRIMARY KEY,"
            + "user_id CHAR(36) NOT NULL,"
            + "blocked_user_id CHAR(36) NOT NULL,"
            + "blocked_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "unblocked_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "blocked_users"
        )) return false;

        
            // ------------------ Groups ------------------
           if (!exec(
               "CREATE TABLE IF NOT EXISTS user_groups ("
               + "group_id CHAR(36) PRIMARY KEY,"
               + "owner_id CHAR(36) NOT NULL,"
               + "name VARCHAR(128) NOT NULL,"
               + "description TEXT NULL,"
               + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
               + "updated_at DATETIME NULL,"
               + "deleted_at DATETIME NULL"
               + ") ENGINE=INNODB "
               + "DEFAULT CHARSET=utf8mb4 "
               + "COLLATE=utf8mb4_unicode_ci",
               "user_groups"
           )) return false;


        
        // ------------------ Groups: Members ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS group_members ("
            + "group_member_id CHAR(36) PRIMARY KEY,"
            + "group_id CHAR(36) NOT NULL,"
            + "user_id CHAR(36) NOT NULL,"
            + "role VARCHAR(32) NOT NULL DEFAULT 'member',"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at DATETIME NULL,"
            + "deleted_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "group_members"
        )) return false;

        // ------------------ Pages ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS pages ("
            + "page_id CHAR(36) PRIMARY KEY,"
            + "owner_id CHAR(36) NOT NULL,"
            + "name VARCHAR(128) NOT NULL,"
            + "description TEXT NULL,"
            + "logo_url VARCHAR(256) NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at DATETIME NULL,"
            + "deleted_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "pages"
        )) return false;

        // ------------------ Pages: Admins ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS page_admins ("
            + "page_admin_id CHAR(36) PRIMARY KEY,"
            + "page_id CHAR(36) NOT NULL,"
            + "user_id CHAR(36) NOT NULL,"
            + "role VARCHAR(32) NOT NULL,"
            + "assigned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "revoked_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "page_admins"
        )) return false;



        // ------------------ Pages: Followers ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS page_followers ("
            + "page_follower_id CHAR(36) PRIMARY KEY,"
            + "page_id CHAR(36) NOT NULL,"
            + "user_id CHAR(36) NOT NULL,"
            + "followed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "unfollowed_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "page_followers"
        )) return false;

        
        // ------------------ Content: Posts ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS posts ("
            + "post_id CHAR(36) PRIMARY KEY,"
            + "user_id CHAR(36) NOT NULL,"
            + "content TEXT NULL,"
            + "visibility VARCHAR(32) NOT NULL DEFAULT 'public',"
            + "reaction_count INT NOT NULL DEFAULT 0,"
            + "comment_count INT NOT NULL DEFAULT 0,"
            + "repost_count INT NOT NULL DEFAULT 0,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "edited_at DATETIME NULL,"
            + "deleted_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "posts"
        )) return false;

        // ------------------ Content: Media ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS media ("
            + "media_id CHAR(36) PRIMARY KEY,"
            + "url VARCHAR(512) NOT NULL,"
            + "type VARCHAR(32) NOT NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "media"
        )) return false;

        
        // ------------------ Content: PostMedia ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS post_media ("
            + "post_media_id CHAR(36) PRIMARY KEY,"
            + "post_id CHAR(36) NOT NULL,"
            + "media_id CHAR(36) NOT NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "post_media"
        )) return false;

        // ------------------ Content: Reactions ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS reactions ("
            + "reaction_id CHAR(36) PRIMARY KEY,"
            + "user_id CHAR(36) NOT NULL,"
            + "post_id CHAR(36) NOT NULL,"
            + "reaction_type VARCHAR(32) NOT NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "reactions"
        )) return false;

        // ------------------ Content: Comments ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS comments ("
            + "comment_id CHAR(36) PRIMARY KEY,"
            + "post_id CHAR(36) NOT NULL,"
            + "user_id CHAR(36) NOT NULL,"
            + "parent_comment_id CHAR(36) NULL,"
            + "content TEXT NOT NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at DATETIME NULL,"
            + "deleted_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "comments"
        )) return false;


        // ------------------ Content: Hashtags ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS hashtags ("
            + "hashtag_id CHAR(36) PRIMARY KEY,"
            + "name VARCHAR(64) NOT NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at DATETIME NULL,"
            + "UNIQUE(name)"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "hashtags"
        )) return false;

        // ------------------ Content: PostHashtags ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS post_hashtags ("
            + "post_hashtag_id CHAR(36) PRIMARY KEY,"
            + "post_id CHAR(36) NOT NULL,"
            + "hashtag_id CHAR(36) NOT NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "post_hashtags"
        )) return false;

        
        // ------------------ Content: UserHashtagFollows ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS user_hashtag_follows ("
            + "user_hashtag_follow_id CHAR(36) PRIMARY KEY,"
            + "user_id CHAR(36) NOT NULL,"
            + "hashtag_id CHAR(36) NOT NULL,"
            + "followed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "unfollowed_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "user_hashtag_follows"
        )) return false;

        
        // ------------------ Content: SavedPosts ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS saved_posts ("
            + "saved_post_id CHAR(36) PRIMARY KEY,"
            + "user_id CHAR(36) NOT NULL,"
            + "post_id CHAR(36) NOT NULL,"
            + "saved_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "unsaved_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "saved_posts"
        )) return false;

        
        
        // ------------------ Content: Reposts ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS reposts ("
            + "repost_id CHAR(36) PRIMARY KEY,"
            + "user_id CHAR(36) NOT NULL,"
            + "original_post_id CHAR(36) NOT NULL,"
            + "reposted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "removed_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "reposts"
        )) return false;

        // ------------------ Messaging: Chats ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS chats ("
            + "chat_id CHAR(36) PRIMARY KEY,"
            + "created_by CHAR(36) NOT NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "deleted_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "chats"
        )) return false;

        // ------------------ Messaging: ChatMembers ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS chat_members ("
            + "chat_member_id CHAR(36) PRIMARY KEY,"
            + "chat_id CHAR(36) NOT NULL,"
            + "user_id CHAR(36) NOT NULL,"
            + "folder VARCHAR(32) NOT NULL DEFAULT 'sorted',"
            + "status VARCHAR(32) NOT NULL DEFAULT 'inbox',"
            + "is_favorite TINYINT NOT NULL DEFAULT 0,"
            + "has_unread TINYINT NOT NULL DEFAULT 0,"
            + "joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at DATETIME NULL,"
            + "left_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "chat_members"
        )) return false;


        // ------------------ Messaging: Messages ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS messages ("
            + "message_id CHAR(36) PRIMARY KEY,"
            + "chat_id CHAR(36) NOT NULL,"
            + "sender_id CHAR(36) NOT NULL,"
            + "content TEXT NULL,"
            + "sent_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "edited_at DATETIME NULL,"
            + "deleted_at DATETIME NULL,"
            + "is_draft TINYINT NOT NULL DEFAULT 0"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "messages"
        )) return false;

        // ------------------ Messaging: MessageReads ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS message_reads ("
            + "message_read_id CHAR(36) PRIMARY KEY,"
            + "message_id CHAR(36) NOT NULL,"
            + "user_id CHAR(36) NOT NULL,"
            + "read_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "message_reads"
        )) return false;

        // ------------------ Messaging: MessageMedia ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS message_media ("
            + "message_media_id CHAR(36) PRIMARY KEY,"
            + "message_id CHAR(36) NOT NULL,"
            + "media_id CHAR(36) NOT NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "INDEX idx_message (message_id)"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "message_media"
        )) return false;

        // ------------------ Jobs: Vacancies ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS vacancies ("
            + "vacancy_id CHAR(36) PRIMARY KEY,"
            + "company_id CHAR(36) NOT NULL,"
            + "posted_by CHAR(36) NOT NULL,"
            + "title VARCHAR(128) NOT NULL,"
            + "job_type VARCHAR(32) NULL,"
            + "schedule VARCHAR(32) NULL,"
            + "location VARCHAR(128) NULL,"
            + "salary_from INT NULL,"
            + "salary_to INT NULL,"
            + "salary_currency VARCHAR(8) NULL,"
            + "description TEXT NULL,"
            + "posted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at DATETIME NULL,"
            + "deleted_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "vacancies"
        )) return false;

        
        // ------------------ Jobs: UserVacanciesFavorites ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS user_vacancies_favorites ("
            + "uvf_id CHAR(36) PRIMARY KEY,"
            + "user_id CHAR(36) NOT NULL,"
            + "vacancy_id CHAR(36) NOT NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "deleted_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "user_vacancies_favorites"
        )) return false;

        // ------------------ Jobs: JobApplications ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS job_applications ("
            + "job_application_id CHAR(36) PRIMARY KEY,"
            + "vacancy_id CHAR(36) NOT NULL,"
            + "user_id CHAR(36) NOT NULL,"
            + "status VARCHAR(32) NOT NULL DEFAULT 'applied',"
            + "applied_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "status_changed_at DATETIME NULL,"
            + "withdrawn_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "job_applications"
        )) return false;

        // ------------------ Jobs: JobSearchQueries ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS job_search_queries ("
            + "job_search_id CHAR(36) PRIMARY KEY,"
            + "user_id CHAR(36) NOT NULL,"
            + "query VARCHAR(256) NULL,"
            + "location VARCHAR(128) NULL,"
            + "radius INT NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at DATETIME NULL,"
            + "deleted_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "job_search_queries"
        )) return false;

        // ------------------ Jobs: JobSearchResults ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS job_search_results ("
            + "job_search_result_id CHAR(36) PRIMARY KEY,"
            + "search_id CHAR(36) NOT NULL,"
            + "vacancy_id CHAR(36) NOT NULL,"
            + "order_index INT NOT NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "deleted_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "job_search_results"
        )) return false;

        // ------------------ Jobs: RecommendedJobQueries ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS recommended_job_queries ("
            + "recommended_job_query_id CHAR(36) PRIMARY KEY,"
            + "query VARCHAR(256) NOT NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "recommended_job_queries"
        )) return false;

        // ------------------ Notifications ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS notifications ("
            + "notification_id CHAR(36) PRIMARY KEY,"
            + "user_id CHAR(36) NOT NULL,"
            + "type VARCHAR(32) NOT NULL,"                     // vacancy, post, mention, connection, message
            + "title VARCHAR(256) NULL,"
            + "body TEXT NULL,"
            + "entity_type VARCHAR(32) NULL,"                  // post, vacancy, user, message, etc.
            + "entity_id CHAR(36) NULL,"
            + "is_read TINYINT NOT NULL DEFAULT 0,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at DATETIME NULL,"
            + "deleted_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "notifications"
        )) return false;
        
        // ------------------ UserActivity (Audit log) ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS user_activity ("
            + "activity_id CHAR(36) PRIMARY KEY,"
            + "user_id CHAR(36) NOT NULL,"
            + "action VARCHAR(64) NOT NULL,"           // post_create, like, follow, message_send
            + "entity_type VARCHAR(32) NOT NULL,"      // post, user, message, event
            + "entity_id CHAR(36) NULL,"
            + "meta JSON NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "INDEX idx_user_time (user_id, created_at),"
            + "INDEX idx_entity (entity_type, entity_id)"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "user_activity"
        )) return false;


        // ------------------ Mentions ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS mentions ("
            + "mention_id CHAR(36) PRIMARY KEY,"
            + "post_id CHAR(36) NOT NULL,"
            + "mentioned_user_id CHAR(36) NOT NULL,"
            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "deleted_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "mentions"
        )) return false;
        
        // ------------------ Events ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS events ("
            + "event_id CHAR(36) PRIMARY KEY,"

            + "organizer_type VARCHAR(16) NOT NULL,"      // 'user' | 'page'
            + "organizer_id CHAR(36) NOT NULL,"            // Users.id or Pages.id

            + "title VARCHAR(256) NOT NULL,"
            + "description TEXT NULL,"

            + "cover_image_url VARCHAR(256) NULL,"
            + "location VARCHAR(256) NULL,"
            + "is_online TINYINT NOT NULL DEFAULT 0,"
            + "external_link VARCHAR(256) NULL,"
            + "timezone VARCHAR(64) NULL,"

            + "visibility VARCHAR(32) NOT NULL DEFAULT 'public',"   // public | connections | followers
            + "allow_comments TINYINT NOT NULL DEFAULT 1,"

            + "start_at DATETIME NOT NULL,"
            + "end_at DATETIME NULL,"

            + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at DATETIME NULL,"
            + "deleted_at DATETIME NULL"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "events"
        )) return false;

        // ------------------ EventAttendees ------------------
        if (!exec(
            "CREATE TABLE IF NOT EXISTS event_attendees ("
            + "event_attendee_id CHAR(36) PRIMARY KEY,"
            + "event_id CHAR(36) NOT NULL,"
            + "user_id CHAR(36) NOT NULL,"

            + "status VARCHAR(32) NOT NULL,"               // going | interested | declined
            + "joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at DATETIME NULL,"
            + "deleted_at DATETIME NULL,"

            + "UNIQUE(event_id, user_id)"
            + ") ENGINE=INNODB "
            + "DEFAULT CHARSET=utf8mb4 "
            + "COLLATE=utf8mb4_unicode_ci",
            "event_attendees"
        )) return false;


        
        return true;
    }

 
}
