package learning.itstep.javaweb222.data.core.seed;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import learning.itstep.javaweb222.data.core.DbProvider;

@Singleton
public class SeedNetwork {

    private final DbProvider db;
    private final Logger logger;

    @Inject
    public SeedNetwork(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    private boolean exec(String sql, String tag) {
        try (Statement st = db.getConnection().createStatement()) {
            st.executeUpdate(sql);
            return true;
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING,
                "SeedNetwork::{0} {1}",
                new Object[]{tag, ex.getMessage() + " | " + sql}
            );
            return false;
        }
    }

    public boolean seed() {

        String sql;
        String adminId = "69231c55-9851-11f0-b1b7-62517600596c";

        // =====================================================
        // Network users (suggested / contacts / follow)
        // =====================================================

        sql =
            "INSERT INTO users (user_id, email, first_name, second_name, avatar_url, profile_title, auth_provider) " +
            "SELECT '11111111-aaaa-11f0-b1b7-62517600596c', 'david.jonson@demo.com', 'David', 'Jonson', " +
            "'david.png', 'Lead UI/UX Designer', 'local' " +
            "WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='david.jonson@demo.com')";
        if (!exec(sql, "network user David")) return false;

        sql =
            "INSERT INTO users (user_id, email, first_name, second_name, avatar_url, profile_title, auth_provider) " +
            "SELECT '22222222-aaaa-11f0-b1b7-62517600596c', 'duncan.callahan@demo.com', 'Duncan', 'Callahan', " +
            "'duncan.png', 'Lead UI/UX Designer', 'local' " +
            "WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='duncan.callahan@demo.com')";
        if (!exec(sql, "network user Duncan")) return false;

        sql =
            "INSERT INTO users (user_id, email, first_name, second_name, avatar_url, profile_title, auth_provider) " +
            "SELECT '33333333-aaaa-11f0-b1b7-62517600596c', 'joshua.cortez@demo.com', 'Joshua', 'Cortez', " +
            "'joshua.png', 'UI/UX Designer', 'local' " +
            "WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='joshua.cortez@demo.com')";
        if (!exec(sql, "network user Joshua")) return false;

        sql =
            "INSERT INTO users (user_id, email, first_name, second_name, avatar_url, profile_title, auth_provider) " +
            "SELECT '44444444-aaaa-11f0-b1b7-62517600596c', 'jennifer.obrian@demo.com', 'Jennifer', 'OBrian', " +
            "'jennifer.png', 'UI/UX Designer', 'local' " +
            "WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='jennifer.obrian@demo.com')";
        if (!exec(sql, "network user Jennifer")) return false;

        sql =
            "INSERT INTO users (user_id, email, first_name, second_name, avatar_url, profile_title, auth_provider) " +
            "SELECT '55555555-aaaa-11f0-b1b7-62517600596c', 'emma.knight@demo.com', 'Emma', 'Knight', " +
            "'emma.png', 'Senior UI/UX Designer', 'local' " +
            "WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='emma.knight@demo.com')";
        if (!exec(sql, "network user Emma Knight")) return false;

        sql =
            "INSERT INTO users (user_id, email, first_name, second_name, avatar_url, profile_title, auth_provider) " +
            "SELECT '66666666-aaaa-11f0-b1b7-62517600596c', 'michael.kennedy@demo.com', 'Michael', 'Kennedy', " +
            "'michael.png', 'Junior UI/UX Designer', 'local' " +
            "WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='michael.kennedy@demo.com')";
        if (!exec(sql, "network user Michael")) return false;


        // =====================================================
        // Suggested connections (Admin)
        // =====================================================

        String[] suggested = {
            "11111111-aaaa-11f0-b1b7-62517600596c",
            "22222222-aaaa-11f0-b1b7-62517600596c",
            "33333333-aaaa-11f0-b1b7-62517600596c",
            "44444444-aaaa-11f0-b1b7-62517600596c",
            "55555555-aaaa-11f0-b1b7-62517600596c",
            "66666666-aaaa-11f0-b1b7-62517600596c"
        };

        for (String uid : suggested) {
            sql =
                "INSERT INTO contacts (contact_id, requester_id, receiver_id, status) " +
                "SELECT UUID(), '" + adminId + "', '" + uid + "', 'suggested' " +
                "WHERE NOT EXISTS (" +
                "   SELECT 1 FROM contacts " +
                "   WHERE requester_id='" + adminId + "' AND receiver_id='" + uid + "'" +
                ")";
            if (!exec(sql, "suggested contact " + uid)) return false;
        }


        // =====================================================
        // Accepted connections
        // =====================================================

        sql =
            "INSERT INTO contacts (contact_id, requester_id, receiver_id, status, requested_at, responded_at, status_changed_at) " +
            "SELECT UUID(), " +
            "'8c21d9a2-9851-11f0-b1b7-62517600596c', " +
            "'7a9f1c21-9851-11f0-b1b7-62517600596c', " +
            "'accepted', NOW(), NOW(), NOW() " +
            "WHERE NOT EXISTS (" +
            "   SELECT 1 FROM contacts " +
            "   WHERE requester_id='8c21d9a2-9851-11f0-b1b7-62517600596c' " +
            "     AND receiver_id='7a9f1c21-9851-11f0-b1b7-62517600596c'" +
            ")";
        if (!exec(sql, "accepted contact Lucas-Emma")) return false;


        // =====================================================
        // Following
        // =====================================================

        sql =
            "INSERT INTO users (user_id, email, first_name, second_name, avatar_url, profile_title, auth_provider) " +
            "SELECT 'bbbb2222-0000-0000-0000-000000000001', 'david.johnson@demo.com', 'David', 'Johnson', " +
            "'david.jpg', 'Tech Lead at Amazon', 'local' " +
            "WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_id='bbbb2222-0000-0000-0000-000000000001')";
        if (!exec(sql, "follow user David")) return false;

        sql =
            "INSERT INTO follows (follow_id, follower_id, following_id) " +
            "SELECT UUID(), '" + adminId + "', 'bbbb2222-0000-0000-0000-000000000001' " +
            "WHERE NOT EXISTS (" +
            "   SELECT 1 FROM follows " +
            "   WHERE follower_id='" + adminId + "' AND following_id='bbbb2222-0000-0000-0000-000000000001'" +
            ")";
        if (!exec(sql, "follow David")) return false;


        // =====================================================
        // Groups
        // =====================================================

        sql =
            "INSERT INTO user_groups (group_id, owner_id, name, description, avatar_url) " +
            "SELECT 'cccc3333-0000-0000-0000-000000000001', '" + adminId + "', " +
            "'UI/UX Design Professionals', 'Design community group', 'uiux-group.jpg' " +
            "WHERE NOT EXISTS (SELECT 1 FROM user_groups WHERE name='UI/UX Design Professionals')";
        if (!exec(sql, "group UIUX")) return false;
        // =====================================================
        // Group members
        // =====================================================

        String groupId = "cccc3333-0000-0000-0000-000000000001";

        String[] groupMembers = {
            "11111111-aaaa-11f0-b1b7-62517600596c",
            "22222222-aaaa-11f0-b1b7-62517600596c",
            "33333333-aaaa-11f0-b1b7-62517600596c",
            "44444444-aaaa-11f0-b1b7-62517600596c",
            "55555555-aaaa-11f0-b1b7-62517600596c"
        };

        for (String uid : groupMembers) {
            sql =
                "INSERT INTO group_members (group_member_id, group_id, user_id, role) " +
                "SELECT UUID(), '" + groupId + "', '" + uid + "', 'member' " +
                "WHERE NOT EXISTS (" +
                "  SELECT 1 FROM group_members " +
                "  WHERE group_id='" + groupId + "' AND user_id='" + uid + "'" +
                ")";
            if (!exec(sql, "group member " + uid)) return false;
        }
        String postId = null; // не нужен

        sql =
            "INSERT INTO posts (post_id, user_id, content) " +
            "SELECT UUID(), '11111111-aaaa-11f0-b1b7-62517600596c', " +
            "'Just finished redesigning our mobile app onboarding flow!' " +
            "WHERE NOT EXISTS (" +
            "  SELECT 1 FROM posts " +
            "  WHERE content='Just finished redesigning our mobile app onboarding flow!'" +
            ")";
        if (!exec(sql, "group post")) return false;

        sql =
         "INSERT INTO group_posts (group_post_id, group_id, post_id) " +
         "SELECT UUID(), '" + groupId + "', p.post_id " +
         "FROM posts p " +
         "WHERE p.content='Just finished redesigning our mobile app onboarding flow!' " +
         "AND NOT EXISTS (" +
         "  SELECT 1 FROM group_posts gp " +
         "  WHERE gp.group_id='" + groupId + "' AND gp.post_id=p.post_id" +
         ")";
     if (!exec(sql, "bind post to group")) return false;


             // =====================================================
        // Pages
        // =====================================================

        sql =
            "INSERT INTO pages (page_id, owner_id, name, description, logo_url) " +
            "SELECT 'dddd4444-0000-0000-0000-000000000001', '" + adminId + "', " +
            "'Google Design', 'Official Google Design page', 'google-design.jpg' " +
            "WHERE NOT EXISTS (SELECT 1 FROM pages WHERE name='Google Design')";
        if (!exec(sql, "page Google Design")) return false;


        // =====================================================
        // Events
        // =====================================================

// =====================================================
// Events
// =====================================================

sql =
    "INSERT INTO events (" +
    "event_id, organizer_type, organizer_id, title, description, " +
    "cover_image_url, location, is_online, visibility, allow_comments, " +
    "start_at, end_at" +
    ") SELECT " +
    "'eeee5555-0000-0000-0000-000000000001', " +
    "'user', '" + adminId + "', " +
    "'Design Systems Conference 2026', " +
    "'Join us for a full day of inspiring talks, workshops, and networking opportunities with leading designers from around the world.', " +
    "'design-systems-2026.jpg', " +
    "'Moscone Center, San Francisco, CA', " +
    "0, " +                         // is_online
    "'public', " +
    "1, " +                         // allow_comments
    "'2026-02-15 09:00:00', " +
    "'2026-02-15 17:00:00' " +
    "WHERE NOT EXISTS (" +
    "  SELECT 1 FROM events WHERE title='Design Systems Conference 2026'" +
    ")";
if (!exec(sql, "event Design Systems Conference")) return false;

String eventId = "eeee5555-0000-0000-0000-000000000001";

String[][] schedule = {
  {"9:00 AM", "Registration & Welcome Coffee", "", "1"},
  {"10:00 AM", "The Future of Design Systems", "Sarah Mitchell", "2"},
  {"11:00 AM", "Building Accessible Components", "James Wilson", "3"},
  {"12:00 PM", "Lunch Break", "", "4"},
  {"1:00 PM", "Design Tokens at Scale", "Emma Thompson", "5"},
  {"2:00 PM", "Workshop: Component Architecture", "Michael Chen", "6"},
  {"4:00 PM", "Panel Discussion & Q&A", "All Speakers", "7"}
};

for (String[] row : schedule) {
    sql =
        "INSERT INTO event_schedule (schedule_id, event_id, time_label, title, speaker_name, order_index) " +
        "SELECT UUID(), '" + eventId + "', '" + row[0] + "', '" + row[1] + "', " +
        (row[2].isEmpty() ? "NULL" : "'" + row[2] + "'") + ", " + row[3] +
        " WHERE NOT EXISTS (" +
        "   SELECT 1 FROM event_schedule " +
        "   WHERE event_id='" + eventId + "' AND order_index=" + row[3] +
        ")";
    if (!exec(sql, "event schedule " + row[1])) return false;
}
String[][] speakers = {
  {"Sarah Mitchell", "Design Director at Google", "sarah.jpg"},
  {"James Wilson", "Principal Designer at Meta", "james.jpg"},
  {"Emma Thompson", "Design Lead at Apple", "emma.jpg"},
  {"Michael Chen", "Senior Designer at Microsoft", "michael.jpg"}
};

for (String[] s : speakers) {
    sql =
        "INSERT INTO event_speakers (speaker_id, name, title, avatar_url) " +
        "SELECT UUID(), '" + s[0] + "', '" + s[1] + "', '" + s[2] + "' " +
        "WHERE NOT EXISTS (SELECT 1 FROM event_speakers WHERE name='" + s[0] + "')";
    if (!exec(sql, "event speaker " + s[0])) return false;
}

sql =
    "INSERT INTO event_speaker_map (event_speaker_map_id, event_id, speaker_id) " +
    "SELECT UUID(), '" + eventId + "', speaker_id " +
    "FROM event_speakers " +
    "WHERE name IN (" +
    "'Sarah Mitchell','James Wilson','Emma Thompson','Michael Chen'" +
    ") " +
    "AND NOT EXISTS (" +
    "  SELECT 1 FROM event_speaker_map esm " +
    "  WHERE esm.event_id='" + eventId + "' AND esm.speaker_id=event_speakers.speaker_id" +
    ")";
if (!exec(sql, "bind event speakers")) return false;


        return true;
    }
}
