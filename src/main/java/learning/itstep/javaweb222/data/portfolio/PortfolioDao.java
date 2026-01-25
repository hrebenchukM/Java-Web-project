package learning.itstep.javaweb222.data.portfolio;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import learning.itstep.javaweb222.data.core.DbProvider;
import learning.itstep.javaweb222.data.dto.*;
import learning.itstep.javaweb222.models.profile.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class PortfolioDao {

    private final DbProvider db;
    private final Logger logger;

    @Inject
    public PortfolioDao(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    // ================== USER ==================

    public User getUser(String userId) {
        String sql = """
            SELECT *
            FROM users
            WHERE user_id = ?
              AND deleted_at IS NULL
        """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return User.fromResultSet(rs);
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING,
                "PortfolioDao::getUser {0}",
                ex.getMessage() + " | " + sql);
        }
        return null;
    }

    // ================== EXPERIENCE ==================

    public List<ExperienceBlockModel> getExperienceBlocks(String userId) {

        String sql = """
            SELECT
                e.*,
                c.*
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
                res.add(
                    new ExperienceBlockModel()
                        .setExperience(Experience.fromResultSet(rs))
                        .setCompany(Company.fromResultSet(rs))
                );
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING,
                "PortfolioDao::getExperienceBlocks {0}",
                ex.getMessage() + " | " + sql);
        }

        return res;
    }

    // ================== EDUCATION ==================

    public List<EducationBlockModel> getEducationBlocks(String userId) {

        String sql = """
            SELECT
                e.*,
                a.*
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
                res.add(
                    new EducationBlockModel()
                        .setEducation(Education.fromResultSet(rs))
                        .setAcademy(
                            rs.getString("academy_id") == null
                                ? null
                                : Academy.fromResultSet(rs)
                        )
                );
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING,
                "PortfolioDao::getEducationBlocks {0}",
                ex.getMessage() + " | " + sql);
        }

        return res;
    }

    // ================== CERTIFICATES ==================

    public List<CertificateBlockModel> getCertificateBlocks(String userId) {

        String sql = """
            SELECT
                c.*,
                a.*
            FROM certificates c
            LEFT JOIN academies a ON c.academy_id = a.academy_id
            WHERE c.user_id = ?
              AND c.deleted_at IS NULL
            ORDER BY c.issue_date DESC
        """;

        List<CertificateBlockModel> res = new ArrayList<>();

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                res.add(
                    new CertificateBlockModel()
                        .setCertificate(Certificate.fromResultSet(rs))
                        .setAcademy(
                            rs.getString("academy_id") == null
                                ? null
                                : Academy.fromResultSet(rs)
                        )
                );
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING,
                "PortfolioDao::getCertificateBlocks {0}",
                ex.getMessage() + " | " + sql);
        }

        return res;
    }

    // ================== SKILLS ==================

    public List<UserSkill> getSkills(String userId) {

        String sql = """
            SELECT us.*, s.name, s.description
            FROM user_skills us
            JOIN skills s ON us.skill_id = s.skill_id
            WHERE us.user_id = ?
            ORDER BY us.is_main DESC, us.order_index
        """;

        List<UserSkill> res = new ArrayList<>();

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                res.add(UserSkill.fromResultSet(rs));
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING,
                "PortfolioDao::getSkills {0}",
                ex.getMessage() + " | " + sql);
        }

        return res;
    }

    // ================== LANGUAGES ==================

    public List<UserLanguage> getLanguages(String userId) {

        String sql = """
            SELECT ul.*, l.name
            FROM user_languages ul
            JOIN languages l ON ul.language_id = l.language_id
            WHERE ul.user_id = ?
        """;

        List<UserLanguage> res = new ArrayList<>();

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                res.add(UserLanguage.fromResultSet(rs));
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING,
                "PortfolioDao::getLanguages {0}",
                ex.getMessage() + " | " + sql);
        }

        return res;
    }

    // ================== RECOMMENDATIONS ==================
public List<RecommendationBlockModel> getRecommendations(String userId) {

    String sql = """
        SELECT
            r.*,
            u.user_id        AS author_id,
            u.first_name     AS author_first_name,
            u.second_name    AS author_second_name,
            u.avatar_url     AS author_avatar_url,
            u.profile_title  AS author_profile_title
        FROM recommendations r
        JOIN users u ON r.author_id = u.user_id
        WHERE r.user_id = ?
          AND r.deleted_at IS NULL
        ORDER BY r.created_at DESC
    """;

    List<RecommendationBlockModel> res = new ArrayList<>();

    try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
        ps.setString(1, userId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            User author = new User()
                .setId(UUID.fromString(rs.getString("author_id")))
                .setFirstName(rs.getString("author_first_name"))
                .setSecondName(rs.getString("author_second_name"))
                .setAvatarUrl(rs.getString("author_avatar_url"))
                .setProfileTitle(rs.getString("author_profile_title"));

            res.add(
                new RecommendationBlockModel()
                    .setRecommendation(Recommendation.fromResultSet(rs))
                    .setAuthor(author)
            );
        }
    }
    catch (SQLException ex) {
        logger.log(Level.WARNING,
            "PortfolioDao::getRecommendations {0}",
            ex.getMessage() + " | " + sql);
    }

    return res;
}


}
