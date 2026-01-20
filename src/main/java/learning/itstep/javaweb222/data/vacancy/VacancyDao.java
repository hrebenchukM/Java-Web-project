package learning.itstep.javaweb222.data.vacancy;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import learning.itstep.javaweb222.data.core.DbProvider;
import learning.itstep.javaweb222.data.dto.Vacancy;

import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    public void addVacancy(Vacancy v) {
        String sql = """
            INSERT INTO vacancies
            (vacancy_id, company_id, posted_by, title, description)
            VALUES(UUID(), ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, v.getCompanyId().toString());
            ps.setString(2, v.getPostedBy().toString());
            ps.setString(3, v.getTitle());
            ps.setString(4, v.getDescription());
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "VacancyDao::addVacancy {0}", ex.getMessage());
        }
    }
}
