package learning.itstep.javaweb222.data.core.seed;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import learning.itstep.javaweb222.data.core.DbProvider;

import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class SeedPortfolioDesigner {

    private final DbProvider db;
    private final Logger logger;

    public static final String USER_ID =
        "11111111-aaaa-11f0-b1b7-62517600596c";

    @Inject
    public SeedPortfolioDesigner(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    private boolean exec(String sql, String tag) {
        try (Statement st = db.getConnection().createStatement()) {
            st.executeUpdate(sql);
            return true;
        } catch (Exception ex) {
            logger.log(Level.WARNING,
                "SeedPortfolioDesigner::{0} {1}",
                new Object[]{tag, ex.getMessage()});
            return false;
        }
    }

    public boolean seed() {

        String sql;

        // ================= USER PROFILE =================
        sql =
            "UPDATE users SET " +
            "header_url='david.jpg', " +
            "profile_title='Lead UI/UX Designer', " +
            "headline='Lead UI/UX Designer · CD Project Red', " +
            "location='Berlin, Germany', " +
            "portfolio_url='https://davidsdesign.design', " +
            "gen_info='Passionate Senior UI/UX Designer.\n" +
            "Crafting engaging and seamless experiences.\n\n" +
            "Focused on design systems, usability and product thinking.' " +
            "WHERE user_id='" + USER_ID + "'";
        if (!exec(sql, "user profile")) return false;

        // ================= LANGUAGES =================
        sql =
            "INSERT INTO languages (language_id,name) " +
            "SELECT UUID(),'English' WHERE NOT EXISTS (SELECT 1 FROM languages WHERE name='English')";
        if (!exec(sql, "lang English")) return false;

        sql =
            "INSERT INTO languages (language_id,name) " +
            "SELECT UUID(),'German' WHERE NOT EXISTS (SELECT 1 FROM languages WHERE name='German')";
        if (!exec(sql, "lang German")) return false;

        sql =
            "INSERT INTO user_languages (user_language_id,user_id,language_id,level) " +
            "SELECT UUID(),'" + USER_ID + "',l.language_id,'Professional proficiency' " +
            "FROM languages l WHERE l.name='English' " +
            "AND NOT EXISTS (SELECT 1 FROM user_languages ul WHERE ul.user_id='" + USER_ID + "' AND ul.language_id=l.language_id)";
        if (!exec(sql, "user lang EN")) return false;

        sql =
            "INSERT INTO user_languages (user_language_id,user_id,language_id,level) " +
            "SELECT UUID(),'" + USER_ID + "',l.language_id,'Native or bilingual' " +
            "FROM languages l WHERE l.name='German' " +
            "AND NOT EXISTS (SELECT 1 FROM user_languages ul WHERE ul.user_id='" + USER_ID + "' AND ul.language_id=l.language_id)";
        if (!exec(sql, "user lang DE")) return false;

        // ================= MAIN SKILLS =================
        sql =
            "UPDATE user_skills SET is_main=1 WHERE user_id='" + USER_ID + "'";
        if (!exec(sql, "main skills")) return false;

        // ================= RECOMMENDATIONS =================
        sql =
            "INSERT INTO recommendations (recommendation_id,author_id,user_id,text) " +
            "SELECT UUID(),'aaaa1111-0000-0000-0000-000000000002','" + USER_ID + "'," +
            "'David is an outstanding designer with strong product thinking and attention to detail.' " +
            "WHERE NOT EXISTS (SELECT 1 FROM recommendations WHERE user_id='" + USER_ID + "')";
        if (!exec(sql, "recommendation")) return false;

        // ================= COMPANIES =================
sql =
    "INSERT INTO companies (company_id, owner_user_id, name, industry, location) " +
    "SELECT 'c1111c21-9851-11f0-b1b7-62517600596c', '" + USER_ID + "', " +
    "'CD Projekt Red', 'Game Development', 'Warsaw, Poland' " +
    "WHERE NOT EXISTS (SELECT 1 FROM companies WHERE name='CD Projekt Red')";
if (!exec(sql, "company CDPR")) return false;

sql =
    "INSERT INTO companies (company_id, owner_user_id, name, industry, location) " +
    "SELECT 'c2222c21-9851-11f0-b1b7-62517600596c', '" + USER_ID + "', " +
    "'Build.co', 'Design Platform', 'Remote' " +
    "WHERE NOT EXISTS (SELECT 1 FROM companies WHERE name='Build.co')";
if (!exec(sql, "company Build.co")) return false;
// ================= EXPERIENCES =================
sql =
    "INSERT INTO experiences " +
    "(experience_id,user_id,company_id,position,employment_type,work_location_type,location,start_date,description) " +
    "SELECT UUID(), '" + USER_ID + "', 'c1111c21-9851-11f0-b1b7-62517600596c', " +
    "'Lead UI/UX Designer','Full-time','Hybrid','Warsaw, Poland','2018-01-01', " +
    "'Led design systems and product UX.' " +
    "WHERE NOT EXISTS (SELECT 1 FROM experiences WHERE user_id='" + USER_ID + "' AND position='Lead UI/UX Designer')";
if (!exec(sql, "experience lead")) return false;

sql =
    "INSERT INTO experiences " +
    "(experience_id,user_id,company_id,position,employment_type,work_location_type,location,start_date,end_date,description) " +
    "SELECT UUID(), '" + USER_ID + "', 'c2222c21-9851-11f0-b1b7-62517600596c', " +
    "'Senior UI/UX Designer','Full-time','Remote','Remote','2016-01-01','2018-01-01', " +
    "'Designed complex UI platforms.' " +
    "WHERE NOT EXISTS (SELECT 1 FROM experiences WHERE user_id='" + USER_ID + "' AND position='Senior UI/UX Designer')";
if (!exec(sql, "experience senior")) return false;
// ================= ACADEMIES =================
sql =
    "INSERT INTO academies (academy_id,name) " +
    "SELECT 'a1111c21-9851-11f0-b1b7-62517600596c','Warsaw University' " +
    "WHERE NOT EXISTS (SELECT 1 FROM academies WHERE name='Warsaw University')";
if (!exec(sql, "academy warsaw")) return false;

sql =
    "INSERT INTO academies (academy_id,name) " +
    "SELECT 'a2222c21-9851-11f0-b1b7-62517600596c','Design Course Academy' " +
    "WHERE NOT EXISTS (SELECT 1 FROM academies WHERE name='Design Course Academy')";
if (!exec(sql, "academy course")) return false;
// ================= EDUCATION =================
sql =
    "INSERT INTO educations " +
    "(education_id,user_id,academy_id,institution,degree,field_of_study,start_date,end_date) " +
    "SELECT UUID(), '" + USER_ID + "', 'a1111c21-9851-11f0-b1b7-62517600596c', " +
    "'Warsaw University','Bachelor','UI/UX Design','2010-09-01','2014-06-01' " +
    "WHERE NOT EXISTS (SELECT 1 FROM educations WHERE user_id='" + USER_ID + "')";
if (!exec(sql, "education")) return false;
// ================= CERTIFICATES =================
sql =
    "INSERT INTO certificates " +
    "(certificate_id,user_id,academy_id,name,issue_date,expiry_date) " +
    "SELECT UUID(), '" + USER_ID + "', 'a2222c21-9851-11f0-b1b7-62517600596c', " +
    "'UI/UX Complete Certificate','2015-05-01','2020-05-01' " +
    "WHERE NOT EXISTS (SELECT 1 FROM certificates WHERE user_id='" + USER_ID + "')";
if (!exec(sql, "certificate")) return false;
// ================= SKILLS =================
sql =
    "INSERT INTO skills (skill_id,name) " +
    "SELECT UUID(),'User Interface Design' WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name='User Interface Design')";
if (!exec(sql, "skill UI")) return false;

sql =
    "INSERT INTO skills (skill_id,name) " +
    "SELECT UUID(),'User Experience Design' WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name='User Experience Design')";
if (!exec(sql, "skill UX")) return false;

sql =
    "INSERT INTO user_skills (user_skill_id,user_id,skill_id,is_main,order_index) " +
    "SELECT UUID(), '" + USER_ID + "', s.skill_id, 1, 1 FROM skills s WHERE s.name='User Interface Design' " +
    "AND NOT EXISTS (SELECT 1 FROM user_skills WHERE user_id='" + USER_ID + "')";
if (!exec(sql, "user skill UI")) return false;
// ================= SECOND RECOMMENDATION =================
sql =
    "INSERT INTO recommendations (recommendation_id,author_id,user_id,text) " +
    "SELECT UUID(),'aaaa1111-0000-0000-0000-000000000003','" + USER_ID + "'," +
    "'David consistently delivers elegant and user-centered solutions.' " +
    "WHERE NOT EXISTS (" +
    "SELECT 1 FROM recommendations WHERE user_id='" + USER_ID + "' AND text LIKE 'David consistently%')";
if (!exec(sql, "recommendation 2")) return false;

        return true;
    }
    
}
