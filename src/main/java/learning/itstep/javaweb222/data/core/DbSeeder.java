
package learning.itstep.javaweb222.data.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import learning.itstep.javaweb222.services.kdf.KdfService;

@Singleton
public class DbSeeder {

    private final DbProvider db;
    private final Logger logger;
    private final KdfService kdf;

    @Inject
    public DbSeeder(DbProvider db, Logger logger, KdfService kdf) {
        this.db = db;
        this.logger = logger;
        this.kdf = kdf;
    }
    
    private boolean exec(String sql, String tag) {
        try (Statement st = db.getConnection().createStatement()) {
            st.executeUpdate(sql);
            return true;
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "DbSeeder::{0} {1}",
                    new Object[]{tag, ex.getMessage() + " | " + sql});
            return false;
        }
    }
    
    public boolean seed() {

        // ------------------ Roles ------------------
        String sql =
            "INSERT INTO user_roles(role_id, description, can_create, can_read, can_update, can_delete) " +
            "VALUES('admin','Root Administrator',1,1,1,1) " +
            "ON DUPLICATE KEY UPDATE " +
            "description=VALUES(description), " +
            "can_create=VALUES(can_create), " +
            "can_read=VALUES(can_read), " +
            "can_update=VALUES(can_update), " +
            "can_delete=VALUES(can_delete)";

        if (!exec(sql, "seed user_roles admin")) return false;

        sql =
            "INSERT INTO user_roles(role_id, description, can_create, can_read, can_update, can_delete) " +
            "VALUES('guest','Self Registered User',0,1,0,0) " +
            "ON DUPLICATE KEY UPDATE " +
            "description=VALUES(description), " +
            "can_create=VALUES(can_create), " +
            "can_read=VALUES(can_read), " +
            "can_update=VALUES(can_update), " +
            "can_delete=VALUES(can_delete)";

        if (!exec(sql, "seed user_roles guest")) return false;

        // ------------------ Default admin user ------------------
        sql =
            "INSERT INTO users(" +
            "user_id, email, first_name, second_name, auth_provider" +
            ") VALUES(" +
            "'69231c55-9851-11f0-b1b7-62517600596c'," +
            "'admin@localhost'," +
            "'Default'," +
            "'Administrator'," +
            "'local'" +
            ") ON DUPLICATE KEY UPDATE " +
            "email=VALUES(email), " +
            "first_name=VALUES(first_name), " +
            "second_name=VALUES(second_name)";

        if (!exec(sql, "seed users admin")) return false;

        // ------------------ AuthCredentials (login + password) ------------------
        String salt = "admin";
        String passwordHash = kdf.dk("admin", salt);

        sql =
            "INSERT INTO auth_credentials(" +
            "auth_id, user_id, role_id, login, salt, password_hash, auth_provider" +
            ") VALUES(" +
            "'35326873-9852-11f0-b1b7-62517600596c'," +
            "'69231c55-9851-11f0-b1b7-62517600596c'," +
            "'admin'," +
            "'admin'," +
            "'" + salt + "'," +
            "'" + passwordHash + "'," +
            "'local'" +
            ") ON DUPLICATE KEY UPDATE " +
            "role_id=VALUES(role_id), " +
            "login=VALUES(login), " +
            "salt=VALUES(salt), " +
            "password_hash=VALUES(password_hash)";

        if (!exec(sql, "seed auth_credentials admin")) return false;

        
        
        // ------------------ Admin profile data ------------------
        sql =
            "UPDATE users SET " +
            "profile_title='Lead UI/UX Designer', " +
            "headline='Lead UI/UX Designer · CD Project Red', " +
            "gen_info='Experienced UI/UX designer with focus on product design.', " +
            "university='Warsaw University', " +
            "location='Warsaw, Poland', " +
            "portfolio_url='https://portfolio.example.com', " +
            "avatar_url='admin.jpg' " +
            "WHERE user_id='69231c55-9851-11f0-b1b7-62517600596c'";

        if (!exec(sql, "seed users admin profile")) return false;

        // ------------------ Admin posts (safe) ------------------
        sql =
            "INSERT INTO posts (post_id, user_id, content) " +
            "SELECT UUID(), '69231c55-9851-11f0-b1b7-62517600596c', 'Seed post 1' " +
            "WHERE NOT EXISTS (" +
            "   SELECT 1 FROM posts " +
            "   WHERE user_id='69231c55-9851-11f0-b1b7-62517600596c' " +
            "     AND content='Seed post 1'" +
            ")";

        if (!exec(sql, "seed admin post 1")) return false;

        sql =
            "INSERT INTO posts (post_id, user_id, content) " +
            "SELECT UUID(), '69231c55-9851-11f0-b1b7-62517600596c', 'Seed post 2' " +
            "WHERE NOT EXISTS (" +
            "   SELECT 1 FROM posts " +
            "   WHERE user_id='69231c55-9851-11f0-b1b7-62517600596c' " +
            "     AND content='Seed post 2'" +
            ")";

        if (!exec(sql, "seed admin post 2")) return false;

        sql =
            "INSERT INTO posts (post_id, user_id, content) " +
            "SELECT UUID(), '69231c55-9851-11f0-b1b7-62517600596c', 'Seed post 3' " +
            "WHERE NOT EXISTS (" +
            "   SELECT 1 FROM posts " +
            "   WHERE user_id='69231c55-9851-11f0-b1b7-62517600596c' " +
            "     AND content='Seed post 3'" +
            ")";

        if (!exec(sql, "seed admin post 3")) return false;


   // ------------------ Analytics: Profile Views (seed once) ------------------
    sql =
        "INSERT INTO profile_views (pv_id, profile_owner_id, viewer_user_id, source) " +
        "SELECT UUID(), '69231c55-9851-11f0-b1b7-62517600596c', NULL, 'seed' " +
        "FROM dual WHERE NOT EXISTS (" +
        "   SELECT 1 FROM profile_views " +
        "   WHERE profile_owner_id='69231c55-9851-11f0-b1b7-62517600596c' " +
        "     AND source='seed'" +
        ")";

    if (!exec(sql, "seed profile_views admin")) return false;

    // ------------------ Analytics: Post Views (seed once) ------------------
    sql =
        "INSERT INTO post_views (post_view_id, post_id, viewer_user_id, source) " +
        "SELECT UUID(), p.post_id, NULL, 'seed' " +
        "FROM posts p " +
        "WHERE p.user_id='69231c55-9851-11f0-b1b7-62517600596c' " +
        "AND NOT EXISTS (" +
        "   SELECT 1 FROM post_views pv " +
        "   WHERE pv.post_id=p.post_id AND pv.source='seed'" +
        ")";

    if (!exec(sql, "seed post_views admin")) return false;


      
    
    
    
    
    
    
    // ------------------ Companies ------------------
    sql =
        "INSERT INTO companies (company_id, name, logo_url, industry, location, website_url, description) " +
        "SELECT UUID(), 'CD Project Red', 'cdpr.png', 'Game Development', 'Warsaw, Poland', " +
        "'https://en.cdprojektred.com', 'AAA game development studio' " +
        "WHERE NOT EXISTS (SELECT 1 FROM companies WHERE name='CD Project Red')";

    if (!exec(sql, "seed company CDPR")) return false;

    sql =
        "INSERT INTO companies (company_id, name, logo_url, industry, location, website_url, description) " +
        "SELECT UUID(), 'Microsoft', 'microsoft.png', 'Technology', 'Redmond, USA', " +
        "'https://www.microsoft.com', 'Global technology company' " +
        "WHERE NOT EXISTS (SELECT 1 FROM companies WHERE name='Microsoft')";

    if (!exec(sql, "seed company Microsoft")) return false;

    sql =
        "INSERT INTO companies (company_id, name, logo_url, industry, location, website_url, description) " +
        "SELECT UUID(), 'Sony', 'sony.png', 'Electronics & Entertainment', 'Tokyo, Japan', " +
        "'https://www.sony.com', 'Multinational conglomerate' " +
        "WHERE NOT EXISTS (SELECT 1 FROM companies WHERE name='Sony')";

    if (!exec(sql, "seed company Sony")) return false;

    // ------------------ Admin experiences ------------------
    sql =
        "INSERT INTO experiences (" +
        "experience_id, user_id, company_id, position, employment_type, " +
        "work_location_type, location, start_date, end_date, description" +
        ") " +

        // ===== CD Project Red =====
        "SELECT UUID(), " +
        "'69231c55-9851-11f0-b1b7-62517600596c', c.company_id, " +
        "'Lead UI/UX Designer', 'Full-time', 'On-site', 'Warsaw, Poland', " +
        "'2021-11-01', NULL, " +
        "'Created and optimized responsive web and app interfaces.\n" +
        "Led user testing and data-driven design iterations.\n" +
        "Unified design language across products.\n" +
        "Partnered with engineering teams for pixel-perfect designs.' " +
        "FROM companies c " +
        "WHERE c.name='CD Project Red' " +
        "AND NOT EXISTS (" +
        "   SELECT 1 FROM experiences e " +
        "   WHERE e.user_id='69231c55-9851-11f0-b1b7-62517600596c' " +
        "     AND e.company_id=c.company_id" +
        ")";

    if (!exec(sql, "seed experience CDPR")) return false;


    // ------------------ Academies ------------------
    sql =
        "INSERT INTO academies (academy_id, name, logo_url, website_url) " +
        "SELECT UUID(), " +
        "'University of California, Los Angeles (UCLA)', " +
        "'ucla.png', " +
        "'https://www.ucla.edu' " +
        "WHERE NOT EXISTS (" +
        "   SELECT 1 FROM academies " +
        "   WHERE name='University of California, Los Angeles (UCLA)'" +
        ")";

    if (!exec(sql, "seed academy UCLA")) return false;

    // ------------------ Admin education ------------------
       sql =
           "INSERT INTO educations (" +
           "education_id, user_id, academy_id, institution, degree, field_of_study, " +
           "start_date, end_date, source" +
           ") " +
           "SELECT UUID(), " +
           "'69231c55-9851-11f0-b1b7-62517600596c', " +
           "a.academy_id, " +
           "a.name, " +
           "'Bachelor', " +
           "'Computer Science', " +
           "'2014-09-01', " +
           "'2018-06-01', " +
           "'seed' " +
           "FROM academies a " +
           "WHERE a.name='University of California, Los Angeles (UCLA)' " +
           "AND NOT EXISTS (" +
           "   SELECT 1 FROM educations e " +
           "   WHERE e.user_id='69231c55-9851-11f0-b1b7-62517600596c' " +
           "     AND e.institution=a.name" +
           ")";

       if (!exec(sql, "seed education UCLA")) return false;

    
    
    
        return true;
    }
    
    
}
