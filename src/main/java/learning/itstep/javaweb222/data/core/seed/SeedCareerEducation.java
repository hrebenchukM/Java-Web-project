package learning.itstep.javaweb222.data.core.seed;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import learning.itstep.javaweb222.data.core.DbProvider;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class SeedCareerEducation {

    private final DbProvider db;
    private final Logger logger;

    public static final String ADMIN_ID =
            "69231c55-9851-11f0-b1b7-62517600596c";

    @Inject
    public SeedCareerEducation(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    private boolean exec(String sql, String tag) {
        try (Statement st = db.getConnection().createStatement()) {
            st.executeUpdate(sql);
            return true;
        }
        catch (SQLException ex) {
            logger.log(
                Level.WARNING,
                "SeedCareerEducation::{0} {1}",
                new Object[]{tag, ex.getMessage() + " | " + sql}
            );
            return false;
        }
    }

    public boolean seed() {

        String sql;

        // ================== COMPANIES ==================
        sql =
            "INSERT INTO companies (company_id, name, logo_url, industry, location, website_url, description) " +
            "SELECT UUID(), 'CD Project Red', 'cdpr.png', 'Game Development', 'Warsaw, Poland', " +
            "'https://en.cdprojektred.com', 'AAA game development studio' " +
            "WHERE NOT EXISTS (SELECT 1 FROM companies WHERE name='CD Project Red')";
        if (!exec(sql, "company CDPR")) return false;

        sql =
            "INSERT INTO companies (company_id, name, logo_url, industry, location, website_url, description) " +
            "SELECT UUID(), 'Microsoft', 'microsoft.png', 'Technology', 'Redmond, USA', " +
            "'https://www.microsoft.com', 'Global technology company' " +
            "WHERE NOT EXISTS (SELECT 1 FROM companies WHERE name='Microsoft')";
        if (!exec(sql, "company Microsoft")) return false;

        sql =
            "INSERT INTO companies (company_id, name, logo_url, industry, location, website_url, description) " +
            "SELECT UUID(), 'Sony', 'sony.png', 'Electronics & Entertainment', 'Tokyo, Japan', " +
            "'https://www.sony.com', 'Multinational conglomerate' " +
            "WHERE NOT EXISTS (SELECT 1 FROM companies WHERE name='Sony')";
        if (!exec(sql, "company Sony")) return false;


        // ================== EXPERIENCES ==================
        sql =
            "INSERT INTO experiences (" +
            "experience_id, user_id, company_id, position, employment_type, " +
            "work_location_type, location, start_date, end_date, description" +
            ") " +
            "SELECT UUID(), '" + ADMIN_ID + "', c.company_id, " +
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
            "   WHERE e.user_id='" + ADMIN_ID + "' AND e.company_id=c.company_id" +
            ")";
        if (!exec(sql, "experience CDPR")) return false;


        // ================== ACADEMIES ==================
        sql =
            "INSERT INTO academies (academy_id, name, logo_url, website_url) " +
            "SELECT UUID(), 'University of California, Los Angeles (UCLA)', " +
            "'ucla.png', 'https://www.ucla.edu' " +
            "WHERE NOT EXISTS (" +
            "   SELECT 1 FROM academies WHERE name='University of California, Los Angeles (UCLA)'" +
            ")";
        if (!exec(sql, "academy UCLA")) return false;

        sql =
            "INSERT INTO academies (academy_id, name, logo_url, website_url) " +
            "SELECT UUID(), 'Creolab Design Courses', 'creolab.png', 'https://creolab.io' " +
            "WHERE NOT EXISTS (SELECT 1 FROM academies WHERE name='Creolab Design Courses')";
        if (!exec(sql, "academy Creolab")) return false;

        sql =
            "INSERT INTO academies (academy_id, name, logo_url, website_url) " +
            "SELECT UUID(), 'Cybergenia IT Academy', 'cybergenia.png', 'https://cybergenia.com' " +
            "WHERE NOT EXISTS (SELECT 1 FROM academies WHERE name='Cybergenia IT Academy')";
        if (!exec(sql, "academy Cybergenia")) return false;


        // ================== EDUCATION ==================
        sql =
            "INSERT INTO educations (" +
            "education_id, user_id, academy_id, institution, degree, field_of_study, " +
            "start_date, end_date, source" +
            ") " +
            "SELECT UUID(), '" + ADMIN_ID + "', a.academy_id, a.name, " +
            "'Bachelor', 'Computer Science', '2014-09-01', '2018-06-01', 'seed' " +
            "FROM academies a " +
            "WHERE a.name='University of California, Los Angeles (UCLA)' " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM educations e " +
            "   WHERE e.user_id='" + ADMIN_ID + "' AND e.institution=a.name" +
            ")";
        if (!exec(sql, "education UCLA")) return false;


        // ================== CERTIFICATES ==================
        sql =
            "INSERT INTO certificates (" +
            "certificate_id, user_id, academy_id, name, download_ref, issue_date, expiry_date" +
            ") " +
            "SELECT UUID(), '" + ADMIN_ID + "', a.academy_id, " +
            "'UI/UX Designer Certificate', " +
            "'d752710b-e2d3-49f5-862c-983635d6c4b8.pdf', " +
            "'2019-08-01', '2020-08-01' " +
            "FROM academies a " +
            "WHERE a.name='Creolab Design Courses' " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM certificates c " +
            "   WHERE c.user_id='" + ADMIN_ID + "' AND c.name='UI/UX Designer Certificate'" +
            ")";
        if (!exec(sql, "certificate Creolab")) return false;

        sql =
            "INSERT INTO certificates (" +
            "certificate_id, user_id, academy_id, name, download_ref, issue_date, expiry_date" +
            ") " +
            "SELECT UUID(), '" + ADMIN_ID + "', a.academy_id, " +
            "'User Experience Specialist', " +
            "'d752710b-e2d3-49f5-862c-983635d6c4b8.pdf', " +
            "'2017-11-01', '2018-11-01' " +
            "FROM academies a " +
            "WHERE a.name='Cybergenia IT Academy' " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM certificates c " +
            "   WHERE c.user_id='" + ADMIN_ID + "' AND c.name='User Experience Specialist'" +
            ")";
        if (!exec(sql, "certificate Cybergenia")) return false;


        // ================== SKILLS ==================
        sql =
            "INSERT INTO skills (skill_id, name) " +
            "SELECT UUID(), 'Communication skills' " +
            "WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name='Communication skills')";
        if (!exec(sql, "skill Communication")) return false;

        sql =
            "INSERT INTO skills (skill_id, name) " +
            "SELECT UUID(), 'Technical skills' " +
            "WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name='Technical skills')";
        if (!exec(sql, "skill Technical")) return false;

        sql =
            "INSERT INTO skills (skill_id, name) " +
            "SELECT UUID(), 'UI/UX Design' " +
            "WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name='UI/UX Design')";
        if (!exec(sql, "skill UIUX")) return false;

        sql =
            "INSERT INTO skills (skill_id, name) " +
            "SELECT UUID(), 'Figma' " +
            "WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name='Figma')";
        if (!exec(sql, "skill Figma")) return false;


        // ================== USER SKILLS ==================
        sql =
            "INSERT INTO user_skills (user_skill_id, user_id, skill_id, level, is_main, order_index) " +
            "SELECT UUID(), '" + ADMIN_ID + "', s.skill_id, 'advanced', 1, 1 " +
            "FROM skills s WHERE s.name='Communication skills' " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM user_skills us " +
            "   WHERE us.user_id='" + ADMIN_ID + "' AND us.skill_id=s.skill_id" +
            ")";
        if (!exec(sql, "user skill Communication")) return false;

        sql =
            "INSERT INTO user_skills (user_skill_id, user_id, skill_id, level, is_main, order_index) " +
            "SELECT UUID(), '" + ADMIN_ID + "', s.skill_id, 'intermediate', 0, 2 " +
            "FROM skills s WHERE s.name='Technical skills' " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM user_skills us " +
            "   WHERE us.user_id='" + ADMIN_ID + "' AND us.skill_id=s.skill_id" +
            ")";
        if (!exec(sql, "user skill Technical")) return false;

        return true;
    }
}
