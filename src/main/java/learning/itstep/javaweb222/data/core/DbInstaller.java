
package learning.itstep.javaweb222.data.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class DbInstaller {
    private final DbProvider db;
    private final Logger logger;

    @Inject
    public DbInstaller(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }
    
    
    public boolean install() {
        String sql = "CREATE TABLE  IF NOT EXISTS  users("
                + "user_id       CHAR(36)     PRIMARY KEY,"
                + "name          VARCHAR(64)  NOT NULL,"
                + "email         VARCHAR(128) NOT NULL,"
                + "birthdate     DATETIME     NULL,"
                + "registered_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "deleted_at    DATETIME     NULL"
                + ")ENGINE = INNODB, "
                + " DEFAULT CHARSET = utf8mb4, "
                + " COLLATE utf8mb4_unicode_ci";
        try(Statement statement =  db.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DbInstaller::install {0}",
                    ex.getMessage() + " | " + sql);

            return false;
        }
        
        sql = "CREATE TABLE  IF NOT EXISTS  user_accesses("
                + "ua_id   CHAR(36)    PRIMARY KEY,"
                + "user_id CHAR(36)    NOT NULL,"
                + "role_id VARCHAR(16) NOT NULL,"
                + "login   VARCHAR(32) NOT NULL,"
                + "salt    CHAR(16)    NOT NULL,"
                + "dk      CHAR(32)    NOT NULL,"
                + "UNIQUE(login)"
                + ")ENGINE = INNODB, "
                + " DEFAULT CHARSET = utf8mb4, "
                + " COLLATE utf8mb4_unicode_ci";
        try(Statement statement =  db.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DbInstaller::install {0}",
                    ex.getMessage() + " | " + sql);

            return false;
        }
        
        
        sql = "CREATE TABLE  IF NOT EXISTS  user_roles("
                + "role_id     VARCHAR(16)  PRIMARY KEY,"
                + "description VARCHAR(256) NOT NULL,"
                + "can_create  TINYINT      NOT NULL DEFAULT 0,"
                + "can_read    TINYINT      NOT NULL DEFAULT 0,"
                + "can_update  TINYINT      NOT NULL DEFAULT 0,"
                + "can_delete  TINYINT      NOT NULL DEFAULT 0"
                + ")ENGINE = INNODB, "
                + " DEFAULT CHARSET = utf8mb4, "
                + " COLLATE utf8mb4_unicode_ci";
        try(Statement statement =  db.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DbInstaller::install {0}",
                    ex.getMessage() + " | " + sql);

            return false;
        }
        
        sql = "CREATE TABLE  IF NOT EXISTS  tokens("
                + "token_id       CHAR(36) PRIMARY KEY,"
                + "user_access_id CHAR(36) NOT NULL,"
                + "issued_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "expired_at     DATETIME NULL"
                + ")ENGINE = INNODB, "
                + " DEFAULT CHARSET = utf8mb4, "
                + " COLLATE utf8mb4_unicode_ci";
        try(Statement statement =  db.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DbInstaller::install {0}",
                    ex.getMessage() + " | " + sql);

            return false;
        }
        // ------------------ Added 2025-10-07 ----------------
        sql = "CREATE TABLE  IF NOT EXISTS  product_groups("
                + "pg_id          CHAR(36)     PRIMARY KEY,"
                + "pg_parent_id   CHAR(36)         NULL,"
                + "pg_name        VARCHAR(64)  NOT NULL,"
                + "pg_description TEXT         NOT NULL,"
                + "pg_slug        VARCHAR(64)  NOT NULL,"
                + "pg_image_url   VARCHAR(256) NOT NULL,"
                + "pg_deleted_at  DATETIME         NULL,"
                + "UNIQUE(pg_slug)"
                + ")ENGINE = INNODB, "
                + " DEFAULT CHARSET = utf8mb4, "
                + " COLLATE utf8mb4_unicode_ci";
        try(Statement statement =  db.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
logger.log(Level.WARNING, "DbInstaller::install {0}",
        ex.getMessage() + " | " + sql);

            return false;
        }
        
        // ------------------ Added 2025-10-20 ----------------
        sql = "CREATE TABLE  IF NOT EXISTS  products("
                + "product_id          CHAR(36)      PRIMARY KEY,"
                + "product_group_id    CHAR(36)      NOT NULL,"
                + "product_name        VARCHAR(64)   NOT NULL,"
                + "product_description TEXT              NULL,"
                + "product_slug        VARCHAR(64)       NULL,"
                + "product_image_url   VARCHAR(256)      NULL,"
                + "product_price       DECIMAL(12,2) NOT NULL,"
                + "product_stock       INT           NOT NULL,"
                + "product_deleted_at  DATETIME          NULL,"
                + "UNIQUE(product_slug)"
                + ")ENGINE = INNODB, "
                + " DEFAULT CHARSET = utf8mb4, "
                + " COLLATE utf8mb4_unicode_ci";
        try(Statement statement =  db.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
logger.log(Level.WARNING, "DbInstaller::install {0}",
        ex.getMessage() + " | " + sql);

            return false;
        }
        
        
        // ------------------ Added 2025-10-28 ----------------
        sql = "CREATE TABLE  IF NOT EXISTS  cart_items("
                + "ci_id         CHAR(36)      PRIMARY KEY,"
                + "ci_cart_id    CHAR(36)      NOT NULL,"
                + "ci_product_id CHAR(36)      NOT NULL,"
                + "ci_di_id      CHAR(36)          NULL  COMMENT 'ref to discount_items table',"
                + "ci_quantity   INT           NOT NULL  DEFAULT 1,"
                + "ci_price      DECIMAL(14,2) NOT NULL,"
                + "ci_deleted_at DATETIME          NULL"
                + ")ENGINE = INNODB, "
                + " DEFAULT CHARSET = utf8mb4, "
                + " COLLATE utf8mb4_unicode_ci";
        try(Statement statement =  db.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
logger.log(Level.WARNING, "DbInstaller::install {0}",
        ex.getMessage() + " | " + sql);

            return false;
        }
        sql = "CREATE TABLE  IF NOT EXISTS  carts("
                + "cart_id         CHAR(36)      PRIMARY KEY,"
                + "cart_user_id    CHAR(36)      NOT NULL,"
                + "cart_di_id      CHAR(36)          NULL  COMMENT 'ref to discount_items table',"
                + "cart_price      DECIMAL(15,2) NOT NULL,"
                + "cart_created_at DATETIME      NOT NULL  DEFAULT CURRENT_TIMESTAMP,"
                + "cart_paid_at    DATETIME          NULL,"
                + "cart_deleted_at DATETIME          NULL"
                + ")ENGINE = INNODB, "
                + " DEFAULT CHARSET = utf8mb4, "
                + " COLLATE utf8mb4_unicode_ci";
        try(Statement statement =  db.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
logger.log(Level.WARNING, "DbInstaller::install {0}",
        ex.getMessage() + " | " + sql);

            return false;
        }
        
        // додано 2025-11-26
        sql = "CREATE TABLE  IF NOT EXISTS  rates ("
                + "rate_id          CHAR(36)  PRIMARY KEY,"
                + "user_id          CHAR(36)  NOT NULL,"  
                + "ci_id            CHAR(36)      NULL  COMMENT 'cart item id',"  
                + "item_id          CHAR(36)  NOT NULL  COMMENT 'potentially linked to any table',"
                + "rate_stars       TINYINT   NOT NULL  COMMENT 'rate in stars from 1 to 5',"
                + "rate_text        TEXT          NULL,"
                + "rate_created_at  DATETIME  NOT NULL  DEFAULT CURRENT_TIMESTAMP,"
                + "rate_updated_at  DATETIME      NULL,"
                + "rate_deleted_at  DATETIME      NULL"
                + ")ENGINE = INNODB, "
                + " DEFAULT CHARSET = utf8mb4, "
                + " COLLATE utf8mb4_unicode_ci";
        try(Statement statement =  db.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
logger.log(Level.WARNING, "DbInstaller::install {0}",
        ex.getMessage() + " | " + sql);

            return false;
        }
        
        return true;
    }
    
}
