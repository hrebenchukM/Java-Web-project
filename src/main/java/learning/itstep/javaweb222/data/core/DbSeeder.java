
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

    // ------------------ Academies: Certificates ------------------
    sql =
        "INSERT INTO academies (academy_id, name, logo_url, website_url) " +
        "SELECT UUID(), 'Creolab Design Courses', 'creolab.png', 'https://creolab.io' " +
        "WHERE NOT EXISTS (SELECT 1 FROM academies WHERE name='Creolab Design Courses')";

    if (!exec(sql, "seed academy Creolab")) return false;

    sql =
        "INSERT INTO academies (academy_id, name, logo_url, website_url) " +
        "SELECT UUID(), 'Cybergenia IT Academy', 'cybergenia.png', 'https://cybergenia.com' " +
        "WHERE NOT EXISTS (SELECT 1 FROM academies WHERE name='Cybergenia IT Academy')";

    if (!exec(sql, "seed academy Cybergenia")) return false;

    
    sql =
        "INSERT INTO certificates (" +
        "certificate_id, user_id, academy_id, name, download_ref, issue_date, expiry_date" +
        ") " +
        "SELECT UUID(), " +
        "'69231c55-9851-11f0-b1b7-62517600596c', " +
        "a.academy_id, " +
        "'UI/UX Designer Certificate', " +
        "'d752710b-e2d3-49f5-862c-983635d6c4b8.pdf', " +
        "'2019-08-01', " +
        "'2020-08-01' " +
        "FROM academies a " +
        "WHERE a.name='Creolab Design Courses' " +
        "AND NOT EXISTS (" +
        "   SELECT 1 FROM certificates c " +
        "   WHERE c.user_id='69231c55-9851-11f0-b1b7-62517600596c' " +
        "     AND c.name='UI/UX Designer Certificate'" +
        ")";

         if (!exec(sql, "seed certificate Creolab UI/UX")) return false;

         sql =
        "INSERT INTO certificates (" +
        "certificate_id, user_id, academy_id, name, download_ref, issue_date, expiry_date" +
        ") " +
        "SELECT UUID(), " +
        "'69231c55-9851-11f0-b1b7-62517600596c', " +
        "a.academy_id, " +
        "'User Experience Specialist', " +
        "'d752710b-e2d3-49f5-862c-983635d6c4b8.pdf', " +
        "'2017-11-01', " +
        "'2018-11-01' " +
        "FROM academies a " +
        "WHERE a.name='Cybergenia IT Academy' " +
        "AND NOT EXISTS (" +
        "   SELECT 1 FROM certificates c " +
        "   WHERE c.user_id='69231c55-9851-11f0-b1b7-62517600596c' " +
        "     AND c.name='User Experience Specialist'" +
        ")";

    if (!exec(sql, "seed certificate Cybergenia UX")) return false;


            // ------------------ Skills: Global ------------------
        sql =
            "INSERT INTO skills (skill_id, name) " +
            "SELECT UUID(), 'Communication skills' " +
            "WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name='Communication skills')";
        if (!exec(sql, "seed skill Communication")) return false;

        sql =
            "INSERT INTO skills (skill_id, name) " +
            "SELECT UUID(), 'Technical skills' " +
            "WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name='Technical skills')";
        if (!exec(sql, "seed skill Technical")) return false;

        sql =
            "INSERT INTO skills (skill_id, name) " +
            "SELECT UUID(), 'UI/UX Design' " +
            "WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name='UI/UX Design')";
        if (!exec(sql, "seed skill UIUX")) return false;

        sql =
            "INSERT INTO skills (skill_id, name) " +
            "SELECT UUID(), 'Figma' " +
            "WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name='Figma')";
        if (!exec(sql, "seed skill Figma")) return false;

        
        // ------------------ User Skills ------------------
        sql =
            "INSERT INTO user_skills (user_skill_id, user_id, skill_id, level, is_main, order_index) " +
            "SELECT UUID(), " +
            "'69231c55-9851-11f0-b1b7-62517600596c', s.skill_id, " +
            "'advanced', 1, 1 " +
            "FROM skills s " +
            "WHERE s.name='Communication skills' " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM user_skills us " +
            "   WHERE us.user_id='69231c55-9851-11f0-b1b7-62517600596c' " +
            "     AND us.skill_id=s.skill_id" +
            ")";
        if (!exec(sql, "seed user skill Communication")) return false;

        sql =
            "INSERT INTO user_skills (user_skill_id, user_id, skill_id, level, is_main, order_index) " +
            "SELECT UUID(), " +
            "'69231c55-9851-11f0-b1b7-62517600596c', s.skill_id, " +
            "'intermediate', 0, 2 " +
            "FROM skills s " +
            "WHERE s.name='Technical skills' " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM user_skills us " +
            "   WHERE us.user_id='69231c55-9851-11f0-b1b7-62517600596c' " +
            "     AND us.skill_id=s.skill_id" +
            ")";
        if (!exec(sql, "seed user skill Technical")) return false;

        
        
        
        
        // ------------------ User: Designer ------------------
sql =
    "INSERT INTO users (" +
    "user_id, email, first_name, second_name, avatar_url, profile_title, location, auth_provider" +
    ") VALUES (" +
    "'7a9f1c21-9851-11f0-b1b7-62517600596c'," +
    "'designer@demo.com'," +
    "'Emma'," +
    "'Stone'," +
    "'emma.jpg'," +
    "'Junior UI/UX Designer'," +
    "'Berlin, Germany'," +
    "'local'" +
    ") ON DUPLICATE KEY UPDATE email=VALUES(email)";

if (!exec(sql, "seed user designer")) return false;

       String salt1 = "designer";
String pass1 = kdf.dk("designer", salt1);

sql =
    "INSERT INTO auth_credentials (" +
    "auth_id, user_id, role_id, login, salt, password_hash, auth_provider" +
    ") VALUES (" +
     "'7a9f9f44-9852-11f0-b1b7-62517600596c'," +
    "'7a9f1c21-9851-11f0-b1b7-62517600596c'," +
    "'guest'," +
    "'designer'," +
    "'" + salt1 + "'," +
    "'" + pass1 + "'," +
    "'local'" +
    ") ON DUPLICATE KEY UPDATE login=VALUES(login)";

if (!exec(sql, "seed auth designer")) return false;
 
        
        // ------------------ User: Developer ------------------
sql =
    "INSERT INTO users (" +
    "user_id, email, first_name, second_name, avatar_url, profile_title, location, auth_provider" +
    ") VALUES (" +
    "'8c21d9a2-9851-11f0-b1b7-62517600596c'," +
    "'dev@demo.com'," +
    "'Lucas'," +
    "'Brown'," +
    "'lucas.jpg'," +
    "'Frontend Developer'," +
    "'Amsterdam, Netherlands'," +
    "'local'" +
    ") ON DUPLICATE KEY UPDATE email=VALUES(email)";

if (!exec(sql, "seed user developer")) return false;

    String salt2 = "dev";
String pass2 = kdf.dk("dev", salt2);

sql =
    "INSERT INTO auth_credentials (" +
    "auth_id, user_id, role_id, login, salt, password_hash, auth_provider" +
    ") VALUES (" +
    "'8c220f13-9852-11f0-b1b7-62517600596c'," +
    "'8c21d9a2-9851-11f0-b1b7-62517600596c'," +
    "'guest'," +
    "'dev'," +
    "'" + salt2 + "'," +
    "'" + pass2 + "'," +
    "'local'" +
    ") ON DUPLICATE KEY UPDATE login=VALUES(login)";

if (!exec(sql, "seed auth developer")) return false;
    
// ------------------ Post: Designer ------------------
sql =
    "INSERT INTO posts (post_id, user_id, content) " +
    "SELECT UUID(), '7a9f1c21-9851-11f0-b1b7-62517600596c', " +
    "'Design is not just what it looks like. Design is how it works. – Steve Jobs' " +
    "WHERE NOT EXISTS (" +
    "   SELECT 1 FROM posts " +
    "   WHERE user_id='7a9f1c21-9851-11f0-b1b7-62517600596c'" +
    ")";

if (!exec(sql, "seed post designer")) return false;

        
// ------------------ Post: Developer ------------------
sql =
    "INSERT INTO posts (post_id, user_id, content) " +
    "SELECT UUID(), '8c21d9a2-9851-11f0-b1b7-62517600596c', " +
    "'Clean code always looks like it was written by someone who cares.' " +
    "WHERE NOT EXISTS (" +
    "   SELECT 1 FROM posts " +
    "   WHERE user_id='8c21d9a2-9851-11f0-b1b7-62517600596c'" +
    ")";

if (!exec(sql, "seed post developer")) return false;




// ------------------ Media: Posts ------------------
sql =
    "INSERT INTO media (media_id, url, type) " +
    "SELECT 'b3333c55-9853-11f0-b1b7-62517600596c', 'post_designer_1.jpg', 'image' " +
    "WHERE NOT EXISTS (" +
    "   SELECT 1 FROM media WHERE url='post_designer_1.jpg'" +
    ")";

if (!exec(sql, "seed media designer post")) return false;

sql =
    "INSERT INTO media (media_id, url, type) " +
    "SELECT 'b4444c55-9853-11f0-b1b7-62517600596c', 'post_developer_1.jpg', 'image' " +
    "WHERE NOT EXISTS (" +
    "   SELECT 1 FROM media WHERE url='post_developer_1.jpg'" +
    ")";

if (!exec(sql, "seed media developer post")) return false;


sql =
    "INSERT INTO media (media_id, url, type) " +
    "SELECT 'b5555c55-9853-11f0-b1b7-62517600596c', 'post_admin_3.jpg', 'image' " +
    "WHERE NOT EXISTS (" +
    "   SELECT 1 FROM media WHERE url='post_admin_3.jpg'" +
    ")";

if (!exec(sql, "seed media admin post 3")) return false;

sql =
    "INSERT INTO media (media_id, url, type) " +
    "SELECT 'b1111c55-9853-11f0-b1b7-62517600596c', 'post_admin_1.jpg', 'image' " +
    "WHERE NOT EXISTS (" +
    "   SELECT 1 FROM media WHERE url='post_admin_1.jpg'" +
    ")";

if (!exec(sql, "seed media admin post 1")) return false;

sql =
    "INSERT INTO media (media_id, url, type) " +
    "SELECT 'b2222c55-9853-11f0-b1b7-62517600596c', 'post_admin_2.jpg', 'image' " +
    "WHERE NOT EXISTS (" +
    "   SELECT 1 FROM media WHERE url='post_admin_2.jpg'" +
    ")";

if (!exec(sql, "seed media admin post 2")) return false;




        // ------------------ PostMedia: Admin posts ------------------
sql =
    "INSERT INTO post_media (post_media_id, post_id, media_id) " +
    "SELECT UUID(), p.post_id, 'b1111c55-9853-11f0-b1b7-62517600596c' " +
    "FROM posts p " +
    "WHERE p.user_id='69231c55-9851-11f0-b1b7-62517600596c' " +
    "  AND p.content='Seed post 1' " +
    "  AND NOT EXISTS (" +
    "      SELECT 1 FROM post_media pm WHERE pm.post_id=p.post_id" +
    "  )";

if (!exec(sql, "seed post_media admin post 1")) return false;

sql =
    "INSERT INTO post_media (post_media_id, post_id, media_id) " +
    "SELECT UUID(), p.post_id, 'b2222c55-9853-11f0-b1b7-62517600596c' " +
    "FROM posts p " +
    "WHERE p.user_id='69231c55-9851-11f0-b1b7-62517600596c' " +
    "  AND p.content='Seed post 2' " +
    "  AND NOT EXISTS (" +
    "      SELECT 1 FROM post_media pm WHERE pm.post_id=p.post_id" +
    "  )";

if (!exec(sql, "seed post_media admin post 2")) return false;


// ------------------ PostMedia: Designer ------------------
sql =
    "INSERT INTO post_media (post_media_id, post_id, media_id) " +
    "SELECT UUID(), p.post_id, 'b3333c55-9853-11f0-b1b7-62517600596c' " +
    "FROM posts p " +
    "WHERE p.user_id='7a9f1c21-9851-11f0-b1b7-62517600596c' " +
    "  AND NOT EXISTS (" +
    "      SELECT 1 FROM post_media pm WHERE pm.post_id=p.post_id" +
    "  )";

if (!exec(sql, "seed post_media designer")) return false;

// ------------------ PostMedia: Developer ------------------
sql =
    "INSERT INTO post_media (post_media_id, post_id, media_id) " +
    "SELECT UUID(), p.post_id, 'b4444c55-9853-11f0-b1b7-62517600596c' " +
    "FROM posts p " +
    "WHERE p.user_id='8c21d9a2-9851-11f0-b1b7-62517600596c' " +
    "  AND NOT EXISTS (" +
    "      SELECT 1 FROM post_media pm WHERE pm.post_id=p.post_id" +
    "  )";

if (!exec(sql, "seed post_media developer")) return false;


// ------------------ Companies: Vacancies demo ------------------
sql =
    "INSERT INTO companies (company_id, name, logo_url, location) " +
    "SELECT UUID(), 'Classpass', 'classpass.jpg', 'USA' " +
    "WHERE NOT EXISTS (SELECT 1 FROM companies WHERE name='Classpass')";
if (!exec(sql, "seed company Classpass")) return false;

sql =
    "INSERT INTO companies (company_id, name, logo_url, location) " +
    "SELECT UUID(), 'Airtable', 'airtable.jpg', 'USA' " +
    "WHERE NOT EXISTS (SELECT 1 FROM companies WHERE name='Airtable')";
if (!exec(sql, "seed company Airtable")) return false;

sql =
    "INSERT INTO companies (company_id, name, logo_url, location) " +
    "SELECT UUID(), 'Wealthsimple', 'wealthsimple.jpg', 'Remote' " +
    "WHERE NOT EXISTS (SELECT 1 FROM companies WHERE name='Wealthsimple')";
if (!exec(sql, "seed company Wealthsimple")) return false;

sql =
    "INSERT INTO companies (company_id, name, logo_url, location) " +
    "SELECT UUID(), 'Dribbble', 'dribbble.jpg', 'Remote' " +
    "WHERE NOT EXISTS (SELECT 1 FROM companies WHERE name='Dribbble')";
if (!exec(sql, "seed company Dribbble")) return false;

sql =
    "INSERT INTO companies (company_id, name, logo_url, location) " +
    "SELECT UUID(), 'Freshworks', 'freshworks.jpg', 'Brazil' " +
    "WHERE NOT EXISTS (SELECT 1 FROM companies WHERE name='Freshworks')";
if (!exec(sql, "seed company Freshworks")) return false;



// ------------------ Vacancies: Best ------------------
sql =
    "INSERT INTO vacancies (vacancy_id, company_id, posted_by, title, location, salary_from, salary_to) " +
    "SELECT UUID(), c.company_id, c.company_id, " +
    "'Walmart', 'Denison, AL', 145000, 225000 " +
    "FROM companies c WHERE c.name='Classpass' " +
    "AND NOT EXISTS (SELECT 1 FROM vacancies v WHERE v.company_id=c.company_id AND v.title='Walmart')";
if (!exec(sql, "seed vacancy Classpass Walmart")) return false;

sql =
    "INSERT INTO vacancies (vacancy_id, company_id, posted_by, title, location, salary_from, salary_to) " +
    "SELECT UUID(), c.company_id, c.company_id, " +
    "'Walmart', 'Las Vegas, NM', 155000, 215000 " +
    "FROM companies c WHERE c.name='Airtable' " +
    "AND NOT EXISTS (SELECT 1 FROM vacancies v WHERE v.company_id=c.company_id AND v.location='Las Vegas, NM')";
if (!exec(sql, "seed vacancy Airtable Walmart")) return false;

sql =
    "INSERT INTO vacancies (vacancy_id, company_id, posted_by, title, location, salary_from, salary_to) " +
    "SELECT UUID(), c.company_id, c.company_id, " +
    "'Varsity Tutors (Remote)', 'Remote', 205000, 285000 " +
    "FROM companies c WHERE c.name='Wealthsimple' " +
    "AND NOT EXISTS (SELECT 1 FROM vacancies v WHERE v.company_id=c.company_id AND v.title LIKE 'Varsity Tutors%')";
if (!exec(sql, "seed vacancy Wealthsimple")) return false;

// ------------------ Vacancies: Graphic Designer ------------------
sql =
    "INSERT INTO vacancies (vacancy_id, company_id, posted_by, title, location) " +
    "SELECT UUID(), c.company_id, c.company_id, " +
    "'Graphic Designer', 'United States (Remote)' " +
    "FROM companies c WHERE c.name='Dribbble' " +
    "AND NOT EXISTS (SELECT 1 FROM vacancies v WHERE v.company_id=c.company_id AND v.title='Graphic Designer')";
if (!exec(sql, "seed vacancy Dribbble Designer")) return false;

sql =
    "INSERT INTO vacancies (vacancy_id, company_id, posted_by, title, location) " +
    "SELECT UUID(), c.company_id, c.company_id, " +
    "'Graphic Designer', 'Florianópolis, Brazil (Remote)' " +
    "FROM companies c WHERE c.name='Freshworks' " +
    "AND NOT EXISTS (SELECT 1 FROM vacancies v WHERE v.company_id=c.company_id AND v.location LIKE 'Florianópolis%')";
if (!exec(sql, "seed vacancy Freshworks Designer")) return false;


// ------------------ Recommended Job Search Queries ------------------
sql =
    "INSERT INTO recommended_job_queries (recommended_job_query_id, query) " +
    "SELECT UUID(), 'marketing manager' " +
    "WHERE NOT EXISTS (SELECT 1 FROM recommended_job_queries WHERE query='marketing manager')";
if (!exec(sql, "seed job query marketing")) return false;

sql =
    "INSERT INTO recommended_job_queries (recommended_job_query_id, query) " +
    "SELECT UUID(), 'hr' " +
    "WHERE NOT EXISTS (SELECT 1 FROM recommended_job_queries WHERE query='hr')";
if (!exec(sql, "seed job query hr")) return false;

sql =
    "INSERT INTO recommended_job_queries (recommended_job_query_id, query) " +
    "SELECT UUID(), 'legal' " +
    "WHERE NOT EXISTS (SELECT 1 FROM recommended_job_queries WHERE query='legal')";
if (!exec(sql, "seed job query legal")) return false;

sql =
    "INSERT INTO recommended_job_queries (recommended_job_query_id, query) " +
    "SELECT UUID(), 'sales' " +
    "WHERE NOT EXISTS (SELECT 1 FROM recommended_job_queries WHERE query='sales')";
if (!exec(sql, "seed job query sales")) return false;

sql =
    "INSERT INTO recommended_job_queries (recommended_job_query_id, query) " +
    "SELECT UUID(), 'google' " +
    "WHERE NOT EXISTS (SELECT 1 FROM recommended_job_queries WHERE query='google')";
if (!exec(sql, "seed job query google")) return false;

sql =
    "INSERT INTO recommended_job_queries (recommended_job_query_id, query) " +
    "SELECT UUID(), 'analyst' " +
    "WHERE NOT EXISTS (SELECT 1 FROM recommended_job_queries WHERE query='analyst')";
if (!exec(sql, "seed job query analyst")) return false;

sql =
    "INSERT INTO recommended_job_queries (recommended_job_query_id, query) " +
    "SELECT UUID(), 'amazon' " +
    "WHERE NOT EXISTS (SELECT 1 FROM recommended_job_queries WHERE query='amazon')";
if (!exec(sql, "seed job query amazon")) return false;


        return true;
    }
    
    
    
    
}
