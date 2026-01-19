
package learning.itstep.javaweb222.data.rate;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import learning.itstep.javaweb222.data.core.DbProvider;
import learning.itstep.javaweb222.data.dto.Rate;
import learning.itstep.javaweb222.models.rate.RateFormModel;
import learning.itstep.javaweb222.rest.RestPagination;


@Singleton
public class RateDao {
    
    private final DbProvider db;
    private final Logger logger;
    
    @Inject
    public RateDao(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    
     public int getRatesCountById(String id) {
        String sql = "SELECT COUNT(*) FROM rates r " +
                "WHERE r.item_id = ? AND r.rate_deleted_at IS NULL";
        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, id);
            ResultSet rs = prep.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "RateDao::getRatesCountById {0}",
                    ex.getMessage() + " | " + sql);
            return 0;
        }
    }
     
    public List<Rate> getRates(String id, RestPagination pagination) throws Exception {
    int skip = (pagination.getCurrentPage() - 1) * pagination.getPerPage();
  String sql = "SELECT * FROM rates r " +
        "LEFT JOIN users u ON r.user_id = u.user_id " +
        "WHERE r.item_id = ? AND r.rate_deleted_at IS NULL " +
        "ORDER BY r.rate_created_at " +
        String.format(" LIMIT %d, %d", skip, pagination.getPerPage());

    try( PreparedStatement prep = db.getConnection().prepareStatement(sql) ) {
        prep.setString(1, id);
        ResultSet rs = prep.executeQuery();
        List<Rate> res = new ArrayList<>();
        while( rs.next() ) {
            res.add( Rate.fromResultSet(rs) ) ;
        }
        return res;
    }
    catch(SQLException ex) {
        logger.log(Level.WARNING, "RateDao::getRates {0}",
                ex.getMessage() + " | " + sql);
        return null;
    }
}

    

    public void addRate(RateFormModel rateFormModel) throws Exception {
        // Перевіряємо дані на валідні UUID
        UUID.fromString( rateFormModel.getCartItemId() );
        UUID.fromString( rateFormModel.getProductId()  );
        
        //  Перевіряємо, чи існують ІД сутностей, а також 
        //  - що у переданому кошику є продукт, що коментується
        //  - немає попереднього коментаря на нього.
        String sql = "SELECT * FROM cart_items ci "
                + "JOIN products p ON ci.ci_product_id = p.product_id "
                + "LEFT JOIN rates r ON ci.ci_id = r.ci_id "
                + "WHERE ci.ci_id = ? AND p.product_id = ?";
        try( PreparedStatement prep = db.getConnection().prepareStatement(sql) ) {
            prep.setString( 1, rateFormModel.getCartItemId() );
            prep.setString( 2, rateFormModel.getProductId()  );
            ResultSet rs = prep.executeQuery();
            if( ! rs.next() ) {
                throw new Exception("CartItemId or ProductId not found");
            }
            if( rs.getString("rate_id") != null ) {
                throw new Exception("Rate exists already");
            }            
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "RateDao::addRate {0} ",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
        
        sql = "INSERT INTO rates(rate_id, user_id, ci_id, item_id, "
                + "rate_text, rate_stars ) VALUES( UUID(), ?, ?, ?, ?, ? )";
        try( PreparedStatement prep = db.getConnection().prepareStatement(sql) ) {
            prep.setString( 1, rateFormModel.getUserId() );
            prep.setString( 2, rateFormModel.getCartItemId() );
            prep.setString( 3, rateFormModel.getProductId() );
            prep.setString( 4, rateFormModel.getComment() );
            prep.setInt(    5, rateFormModel.getRate() );
            prep.executeUpdate();
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "RateDao::addRate {0} ",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
    }
}
