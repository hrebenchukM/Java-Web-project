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
        "7a9f1c21-9851-11f0-b1b7-62517600596c";

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
            "header_url='portfolio_header.jpg', " +
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

        return true;
    }
}
