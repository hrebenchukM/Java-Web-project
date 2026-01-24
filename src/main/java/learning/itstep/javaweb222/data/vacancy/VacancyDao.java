package learning.itstep.javaweb222.data.vacancy;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import learning.itstep.javaweb222.data.core.DbProvider;
import learning.itstep.javaweb222.data.dto.Vacancy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class VacancyDao {

    private final DbProvider db;
    private final Logger logger;

    @Inject
    public VacancyDao(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    // ==================== READ: ALL VACANCIES WITH COMPANY ====================
    public List<Vacancy> getAllWithCompany() {

        List<Vacancy> list = new ArrayList<>();

        String sql = """
            SELECT
                v.*,
                c.*
            FROM vacancies v
            JOIN companies c ON c.company_id = v.company_id
            WHERE v.deleted_at IS NULL
            ORDER BY v.posted_at DESC
        """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(Vacancy.fromResultSet(rs));
            }
        }
        catch (SQLException ex) {
            logger.log(
                Level.WARNING,
                "VacancyDao::getAllWithCompany {0}",
                ex.getMessage() + " | " + sql
            );
        }

        return list;
    }

    // ==================== READ: BEST VACANCIES ====================
    public List<Vacancy> getBestWithCompany(int limit) {

        List<Vacancy> list = new ArrayList<>();

        String sql = """
            SELECT
                v.*,
                c.*
            FROM vacancies v
            JOIN companies c ON c.company_id = v.company_id
            WHERE v.deleted_at IS NULL
            ORDER BY v.posted_at DESC
            LIMIT ?
        """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(Vacancy.fromResultSet(rs));
                }
            }
        }
        catch (SQLException ex) {
            logger.log(
                Level.WARNING,
                "VacancyDao::getBestWithCompany {0}",
                ex.getMessage() + " | " + sql
            );
        }

        return list;
    }

    // ==================== CREATE VACANCY ====================
    public void addVacancy(Vacancy v) {

        String sql = """
            INSERT INTO vacancies (
                vacancy_id,
                company_id,
                posted_by,
                title,
                job_type,
                schedule,
                location,
                description
            ) VALUES (
                UUID(), ?, ?, ?, ?, ?, ?, ?
            )
        """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {

            ps.setString(1, v.getCompanyId().toString());
            ps.setString(2, v.getPostedBy().toString());
            ps.setString(3, v.getTitle());
            ps.setString(4, v.getJobType());
            ps.setString(5, v.getSchedule());
            ps.setString(6, v.getLocation());
            ps.setString(7, v.getDescription());

            ps.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(
                Level.WARNING,
                "VacancyDao::addVacancy {0}",
                ex.getMessage() + " | " + sql
            );
        }
    }
}
