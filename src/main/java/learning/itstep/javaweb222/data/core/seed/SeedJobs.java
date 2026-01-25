package learning.itstep.javaweb222.data.core.seed;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import learning.itstep.javaweb222.data.core.DbProvider;

@Singleton
public class SeedJobs {

    private final DbProvider db;
    private final Logger logger;

    @Inject
    public SeedJobs(DbProvider db, Logger logger) {
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
                "SeedJobs::{0} {1}",
                new Object[]{ tag, ex.getMessage() + " | " + sql }
            );
            return false;
        }
    }

    public boolean seed() {

        String sql;

        // ==============================
        // Companies (for jobs)
        // ==============================

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


        // ==============================
        // Vacancies (Jobs)
        // ==============================

        // ---- High salary ----
        sql =
            "INSERT INTO vacancies (vacancy_id, company_id, posted_by, title, location, salary_from, salary_to) " +
            "SELECT UUID(), c.company_id, c.company_id, " +
            "'Walmart', 'Denison, AL', 145000, 225000 " +
            "FROM companies c WHERE c.name='Classpass' " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM vacancies v " +
            "   WHERE v.company_id=c.company_id AND v.title='Walmart'" +
            ")";
        if (!exec(sql, "seed vacancy Classpass Walmart")) return false;

        sql =
            "INSERT INTO vacancies (vacancy_id, company_id, posted_by, title, location, salary_from, salary_to) " +
            "SELECT UUID(), c.company_id, c.company_id, " +
            "'Walmart', 'Las Vegas, NM', 155000, 215000 " +
            "FROM companies c WHERE c.name='Airtable' " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM vacancies v " +
            "   WHERE v.company_id=c.company_id AND v.location='Las Vegas, NM'" +
            ")";
        if (!exec(sql, "seed vacancy Airtable Walmart")) return false;

        sql =
            "INSERT INTO vacancies (vacancy_id, company_id, posted_by, title, location, salary_from, salary_to) " +
            "SELECT UUID(), c.company_id, c.company_id, " +
            "'Varsity Tutors (Remote)', 'Remote', 205000, 285000 " +
            "FROM companies c WHERE c.name='Wealthsimple' " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM vacancies v " +
            "   WHERE v.company_id=c.company_id AND v.title LIKE 'Varsity Tutors%'" +
            ")";
        if (!exec(sql, "seed vacancy Wealthsimple")) return false;


        // ---- Design jobs ----
        sql =
            "INSERT INTO vacancies (vacancy_id, company_id, posted_by, title, location) " +
            "SELECT UUID(), c.company_id, c.company_id, " +
            "'Graphic Designer', 'United States (Remote)' " +
            "FROM companies c WHERE c.name='Dribbble' " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM vacancies v " +
            "   WHERE v.company_id=c.company_id AND v.title='Graphic Designer'" +
            ")";
        if (!exec(sql, "seed vacancy Dribbble Designer")) return false;

        sql =
            "INSERT INTO vacancies (vacancy_id, company_id, posted_by, title, location) " +
            "SELECT UUID(), c.company_id, c.company_id, " +
            "'Graphic Designer', 'Florianópolis, Brazil (Remote)' " +
            "FROM companies c WHERE c.name='Freshworks' " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM vacancies v " +
            "   WHERE v.company_id=c.company_id AND v.location LIKE 'Florianópolis%'" +
            ")";
        if (!exec(sql, "seed vacancy Freshworks Designer")) return false;


        // ==============================
        // Recommended job search queries
        // ==============================

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
