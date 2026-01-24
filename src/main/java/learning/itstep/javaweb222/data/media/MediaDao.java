package learning.itstep.javaweb222.data.media;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import learning.itstep.javaweb222.data.core.DbProvider;
import learning.itstep.javaweb222.data.dto.Media;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

@Singleton
public class MediaDao {

    private final DbProvider db;
    private final Logger logger;

    @Inject
    public MediaDao(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    public UUID addMedia(Media media) {
        if (media.getId() == null) {
            media.setId(db.getDbIdentity());
        }

        String sql =
            "INSERT INTO media (media_id, url, type) VALUES (?, ?, ?)";

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, media.getId().toString());
            prep.setString(2, media.getUrl());
            prep.setString(3, media.getType());
            prep.executeUpdate();
            return media.getId();
        }
        catch (SQLException ex) {
            logger.warning("MediaDao::addMedia " + ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }
}
