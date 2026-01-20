
package learning.itstep.javaweb222.data.profile;

import java.util.Locale;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import learning.itstep.javaweb222.data.core.DbProvider;
import learning.itstep.javaweb222.data.dto.Company;
import learning.itstep.javaweb222.data.dto.Education;
import learning.itstep.javaweb222.data.dto.Experience;
import learning.itstep.javaweb222.data.dto.User;
import learning.itstep.javaweb222.data.dto.UserLanguage;
import learning.itstep.javaweb222.data.dto.UserSkill;
import learning.itstep.javaweb222.models.profile.ExperienceBlockModel;

@Singleton
public class ProfileDao {
    
    
    
    private final DbProvider db;
    private final Logger logger;

    @Inject
    public ProfileDao(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }
    
    // ================== EXPERIENCES ==================

   public List<ExperienceBlockModel> getExperienceBlocksByUser(String userId) {

    String sql = """
        SELECT e.*, c.*
        FROM experiences e
        JOIN companies c ON e.company_id = c.company_id
        WHERE e.user_id = ?
          AND e.deleted_at IS NULL
        ORDER BY e.start_date DESC
    """;

    List<ExperienceBlockModel> res = new ArrayList<>();

    try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
        ps.setString(1, userId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            ExperienceBlockModel block = new ExperienceBlockModel()
                .setExperience( Experience.fromResultSet(rs) )
                .setCompany( Company.fromResultSet(rs) );

            res.add(block);
        }
    }
    catch (SQLException ex) {
        logger.log(Level.WARNING,
            "ProfileDao::getExperienceBlocksByUser {0}",
            ex.getMessage() + " | " + sql);
    }

    return res;
}


    // ================== EDUCATIONS ==================

    public List<Education> getEducationsByUser(String userId) {
        String sql = """
            SELECT * FROM educations
            WHERE user_id = ? AND deleted_at IS NULL
            ORDER BY start_date DESC
        """;

        List<Education> res = new ArrayList<>();
        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, userId);
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                res.add(Education.fromResultSet(rs));
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "ProfileDao::getEducationsByUser {0}",
                    ex.getMessage() + " | " + sql);
        }
        return res;
    }

    // ================== SKILLS ==================

    public List<UserSkill> getSkillsByUser(String userId) {
        String sql = """
            SELECT us.*, s.name, s.description
            FROM user_skills us
            JOIN skills s ON us.skill_id = s.skill_id
            WHERE us.user_id = ?
            ORDER BY us.is_main DESC, us.order_index
        """;

        List<UserSkill> res = new ArrayList<>();
        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, userId);
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                res.add(UserSkill.fromResultSet(rs));
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "ProfileDao::getSkillsByUser {0}",
                    ex.getMessage() + " | " + sql);
        }
        return res;
    }

    // ================== LANGUAGES ==================

    public List<UserLanguage> getLanguagesByUser(String userId) {
        String sql = """
            SELECT ul.*, l.name
            FROM user_languages ul
            JOIN languages l ON ul.language_id = l.language_id
            WHERE ul.user_id = ?
        """;

        List<UserLanguage> res = new ArrayList<>();
        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, userId);
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                res.add(UserLanguage.fromResultSet(rs));
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "ProfileDao::getLanguagesByUser {0}",
                    ex.getMessage() + " | " + sql);
        }
        return res;
    }


    // ================== ANALYTICS ==================

    public int getProfileViewsCount(String userId) {
        String sql = """
            SELECT COUNT(*) FROM profile_views
            WHERE profile_owner_id = ?
        """;

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, userId);
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING,
                "ProfileDao::getProfileViewsCount {0}",
                ex.getMessage() + " | " + sql);
        }
        return 0;
    }

    public int getPostViewsCount(String userId) {
        String sql = """
            SELECT COUNT(*) 
            FROM post_views pv
            JOIN posts p ON pv.post_id = p.post_id
            WHERE p.user_id = ?
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "ProfileDao::getPostViewsCount {0}", ex.getMessage());
        }
        return 0;
    }
   
  
    
}
