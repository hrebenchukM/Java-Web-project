package learning.itstep.javaweb222.data.company;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import learning.itstep.javaweb222.data.core.DbProvider;

@Singleton
public class CompanyDao {

    private final DbProvider db;
    private final Logger logger;

    @Inject
    public CompanyDao(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    public String getCompanyNameById(String companyId) {
        String sql = "SELECT name FROM companies WHERE company_id = ?";

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, companyId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "CompanyDao::getCompanyNameById {0}", ex.getMessage());
        }
        return null;
    }
}
