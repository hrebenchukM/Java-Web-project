package learning.itstep.javaweb222.data.core.seed;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import learning.itstep.javaweb222.data.core.DbProvider;

import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class SeedDemoChats {

    private final DbProvider db;
    private final Logger logger;

    // demo users
    private static final String USER_A = SeedRolesUsers.ADMIN_ID;
    private static final String USER_B = "11111111-aaaa-11f0-b1b7-62517600596c";
    private static final String USER_C = "22222222-bbbb-11f0-b1b7-62517600596c";

    @Inject
    public SeedDemoChats(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    private boolean exec(String sql, String tag) {
        try (Statement st = db.getConnection().createStatement()) {
            st.executeUpdate(sql);
            return true;
        }
        catch (Exception ex) {
            logger.log(
                Level.WARNING,
                "SeedDemoChats::{0} {1}",
                new Object[]{tag, ex.getMessage() + " | " + sql}
            );
            return false;
        }
    }

    public boolean seed() {

        // ================== CHAT 1 ==================
        String chat1 = "cccc3333-0000-0000-0000-000000000001";

        if (!exec(
            """
            INSERT INTO chats (chat_id, created_by)
            VALUES ('%s', '%s')
            ON DUPLICATE KEY UPDATE deleted_at = NULL
            """.formatted(chat1, USER_A),
            "chat 1"
        )) return false;

        // members (без дублей)
        exec(
            """
            INSERT INTO chat_members (chat_member_id, chat_id, user_id, has_unread)
            SELECT UUID(), '%s', '%s', 0
            WHERE NOT EXISTS (
                SELECT 1 FROM chat_members
                WHERE chat_id = '%s'
                  AND user_id = '%s'
                  AND left_at IS NULL
            )
            """.formatted(chat1, USER_A, chat1, USER_A),
            "chat 1 member A"
        );

        exec(
            """
            INSERT INTO chat_members (chat_member_id, chat_id, user_id, has_unread)
            SELECT UUID(), '%s', '%s', 1
            WHERE NOT EXISTS (
                SELECT 1 FROM chat_members
                WHERE chat_id = '%s'
                  AND user_id = '%s'
                  AND left_at IS NULL
            )
            """.formatted(chat1, USER_B, chat1, USER_B),
            "chat 1 member B"
        );

        // messages (без дублей)
        exec(
            """
            INSERT INTO messages (message_id, chat_id, sender_id, content, sent_at)
            SELECT UUID(), '%s', '%s', 'Hello there!', NOW() - INTERVAL 2 HOUR
            WHERE NOT EXISTS (
                SELECT 1 FROM messages
                WHERE chat_id = '%s'
                  AND sender_id = '%s'
                  AND content = 'Hello there!'
            )
            """.formatted(chat1, USER_B, chat1, USER_B),
            "chat 1 msg 1"
        );

        exec(
            """
            INSERT INTO messages (message_id, chat_id, sender_id, content, sent_at)
            SELECT UUID(), '%s', '%s', 'Hi! Good to hear from you.', NOW() - INTERVAL 90 MINUTE
            WHERE NOT EXISTS (
                SELECT 1 FROM messages
                WHERE chat_id = '%s'
                  AND sender_id = '%s'
                  AND content = 'Hi! Good to hear from you.'
            )
            """.formatted(chat1, USER_A, chat1, USER_A),
            "chat 1 msg 2"
        );

        exec(
            """
            INSERT INTO messages (message_id, chat_id, sender_id, content, sent_at)
            SELECT UUID(), '%s', '%s', 'Thanks! Good luck.', NOW() - INTERVAL 45 MINUTE
            WHERE NOT EXISTS (
                SELECT 1 FROM messages
                WHERE chat_id = '%s'
                  AND sender_id = '%s'
                  AND content = 'Thanks! Good luck.'
            )
            """.formatted(chat1, USER_B, chat1, USER_B),
            "chat 1 msg 3"
        );

        // ================== CHAT 2 ==================
        String chat2 = "cccc3333-0000-0000-0000-000000000002";

        exec(
            """
            INSERT INTO chats (chat_id, created_by)
            VALUES ('%s', '%s')
            ON DUPLICATE KEY UPDATE deleted_at = NULL
            """.formatted(chat2, USER_A),
            "chat 2"
        );

        exec(
            """
            INSERT INTO chat_members (chat_member_id, chat_id, user_id, has_unread)
            SELECT UUID(), '%s', '%s', 0
            WHERE NOT EXISTS (
                SELECT 1 FROM chat_members
                WHERE chat_id = '%s'
                  AND user_id = '%s'
                  AND left_at IS NULL
            )
            """.formatted(chat2, USER_A, chat2, USER_A),
            "chat 2 member A"
        );

        exec(
            """
            INSERT INTO chat_members (chat_member_id, chat_id, user_id, has_unread)
            SELECT UUID(), '%s', '%s', 1
            WHERE NOT EXISTS (
                SELECT 1 FROM chat_members
                WHERE chat_id = '%s'
                  AND user_id = '%s'
                  AND left_at IS NULL
            )
            """.formatted(chat2, USER_C, chat2, USER_C),
            "chat 2 member C"
        );

        exec(
            """
            INSERT INTO messages (message_id, chat_id, sender_id, content, sent_at)
            SELECT UUID(), '%s', '%s', 'Alright, I will call you back.', NOW() - INTERVAL 2 MINUTE
            WHERE NOT EXISTS (
                SELECT 1 FROM messages
                WHERE chat_id = '%s'
                  AND sender_id = '%s'
                  AND content = 'Alright, I will call you back.'
            )
            """.formatted(chat2, USER_C, chat2, USER_C),
            "chat 2 msg 1"
        );

        return true;
    }
}
