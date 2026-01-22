package learning.itstep.javaweb222.data.company;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
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

    public UUID getOrCreateCompanyByName(String name, String ownerUserId) throws Exception {

        String sql = "SELECT company_id FROM companies WHERE name = ?";

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return UUID.fromString(rs.getString("company_id"));
            }
        }

        UUID id = db.getDbIdentity();

        sql = """
            INSERT INTO companies (
                company_id,
                owner_user_id,
                name,
                created_at
            ) VALUES (?, ?, ?, CURRENT_TIMESTAMP)
        """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ps.setString(2, ownerUserId);
            ps.setString(3, name);
            ps.executeUpdate();
        }

        return id;
    }
}
