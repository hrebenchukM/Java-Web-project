
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
import learning.itstep.javaweb222.data.dto.Academy;
import learning.itstep.javaweb222.data.dto.Company;
import learning.itstep.javaweb222.data.dto.Education;
import learning.itstep.javaweb222.data.dto.Experience;
import learning.itstep.javaweb222.data.dto.User;
import learning.itstep.javaweb222.data.dto.UserLanguage;
import learning.itstep.javaweb222.data.dto.UserSkill;
import learning.itstep.javaweb222.models.profile.EducationBlockModel;
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
SELECT
        e.experience_id,
        e.user_id,
        e.company_id,
        e.position,
        e.employment_type,
        e.work_location_type,
        e.location,
        e.start_date,
        e.end_date,
        e.description,
        e.created_at,
        e.updated_at,
        e.deleted_at,
    
        c.company_id,
        c.owner_user_id,
        c.name,
        c.logo_url,
        c.industry,
        c.location,
        c.website_url,
        c.description,
        c.created_at,
        c.updated_at,
        c.deleted_at
    
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

  public List<EducationBlockModel> getEducationBlocksByUser(String userId) {

    String sql = """
        SELECT
            e.education_id,
            e.user_id,
            e.academy_id,
            e.institution,
            e.degree,
            e.field_of_study,
            e.start_date,
            e.end_date,
            e.source,
            e.created_at,
            e.updated_at,
            e.deleted_at,

            a.academy_id,
            a.name,
            a.logo_url,
            a.website_url,
            a.created_at,
            a.updated_at

        FROM educations e
        LEFT JOIN academies a ON e.academy_id = a.academy_id
        WHERE e.user_id = ?
          AND e.deleted_at IS NULL
        ORDER BY e.start_date DESC
    """;

    List<EducationBlockModel> res = new ArrayList<>();

    try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
        ps.setString(1, userId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            EducationBlockModel block = new EducationBlockModel()
                .setEducation(Education.fromResultSet(rs))
                .setAcademy(
                    rs.getString("academy_id") == null
                        ? null
                        : Academy.fromResultSet(rs)
                );

            res.add(block);
        }
    }
    catch (SQLException ex) {
        logger.log(Level.WARNING,
            "ProfileDao::getEducationBlocksByUser {0}",
            ex.getMessage() + " | " + sql);
    }

    return res;
}


public void addEducation(String userId, Education e) throws Exception {

    UUID userGuid;
    try {
        userGuid = UUID.fromString(userId);
    }
    catch (Exception ex) {
        throw new Exception("Invalid userId UUID");
    }

    // ===== get or create academy =====
    UUID academyId = null;

    if (e.getInstitution() != null && !e.getInstitution().isBlank()) {

        String findSql = "SELECT academy_id FROM academies WHERE name = ?";

        try (PreparedStatement ps = db.getConnection().prepareStatement(findSql)) {
            ps.setString(1, e.getInstitution());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                academyId = UUID.fromString(rs.getString("academy_id"));
            }
        }

        if (academyId == null) {
            String insertSql = """
                INSERT INTO academies (academy_id, name)
                VALUES (UUID(), ?)
            """;

            try (PreparedStatement ps = db.getConnection().prepareStatement(insertSql)) {
                ps.setString(1, e.getInstitution());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = db.getConnection().prepareStatement(findSql)) {
                ps.setString(1, e.getInstitution());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    academyId = UUID.fromString(rs.getString("academy_id"));
                }
            }
        }
    }

    // ===== insert education =====
    String sql = """
        INSERT INTO educations (
            education_id,
            user_id,
            academy_id,
            institution,
            degree,
            field_of_study,
            start_date,
            end_date,
            source
        ) VALUES (
            UUID(), ?, ?, ?, ?, ?, ?, ?, ?
        )
    """;

    try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {

        ps.setString(1, userGuid.toString());

        if (academyId != null)
            ps.setString(2, academyId.toString());
        else
            ps.setNull(2, java.sql.Types.CHAR);

        ps.setString(3, e.getInstitution());
        ps.setString(4, e.getDegree());
        ps.setString(5, e.getFieldOfStudy());

        ps.setDate(6,
            e.getStartDate() == null ? null :
            new java.sql.Date(e.getStartDate().getTime())
        );

        ps.setDate(7,
            e.getEndDate() == null ? null :
            new java.sql.Date(e.getEndDate().getTime())
        );

        ps.setString(8, e.getSource());

        ps.executeUpdate();
    }
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

    public void addExperience(String userId, Experience e) throws Exception {

        // === UUID validation ===
        UUID userGuid;
        UUID companyGuid;

        try {
            userGuid = UUID.fromString(userId);
            companyGuid = e.getCompanyId();
        }
        catch (Exception ignore) {
            throw new Exception("Invalid UUID format");
        }

        // === check company exists ===
        String sql = "SELECT company_id FROM companies WHERE company_id = ?";

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, companyGuid.toString());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new Exception("Company not found");
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING,
                "ProfileDao::addExperience {0}",
                ex.getMessage() + " | " + sql);
            throw ex;
        }

        // === insert experience ===
        sql = """
            INSERT INTO experiences (
                experience_id,
                user_id,
                company_id,
                position,
                employment_type,
                work_location_type,
                location,
                start_date,
                end_date,
                description
            ) VALUES (
                UUID(), ?, ?, ?, ?, ?, ?, ?, ?, ?
            )
        """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {

            ps.setString(1, userGuid.toString());
            ps.setString(2, companyGuid.toString());
            ps.setString(3, e.getPosition());
            ps.setString(4, e.getEmploymentType());
            ps.setString(5, e.getWorkLocationType());
            ps.setString(6, e.getLocation());
            ps.setDate(7, new java.sql.Date(e.getStartDate().getTime()));
            ps.setDate(8,
                e.getEndDate() == null
                    ? null
                    : new java.sql.Date(e.getEndDate().getTime())
            );
            ps.setString(9, e.getDescription());

            ps.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING,
                "ProfileDao::addExperience {0}",
                ex.getMessage() + " | " + sql);
            throw ex;
        }
    }

  
    
}
