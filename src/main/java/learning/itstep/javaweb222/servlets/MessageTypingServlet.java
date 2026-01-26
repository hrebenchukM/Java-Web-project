
package learning.itstep.javaweb222.servlets;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import learning.itstep.javaweb222.data.DataAccessor;
import learning.itstep.javaweb222.data.jwt.JwtToken;
import learning.itstep.javaweb222.servlets.stream.ChatStreamServlet;

@Singleton
public class MessageTypingServlet extends HttpServlet {

    private final DataAccessor dataAccessor;
    private final ChatStreamServlet chatStream;
    private final Gson gson = new Gson();

    @Inject
    public MessageTypingServlet(
            DataAccessor dataAccessor,
            ChatStreamServlet chatStream
    ) {
        this.dataAccessor = dataAccessor;
        this.chatStream = chatStream;
    }

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        JwtToken jwt = (JwtToken) req.getAttribute("JWT");
        if (jwt == null) {
            resp.setStatus(401);
            return;
        }

        String chatId = req.getParameter("chatId");
        if (chatId == null || chatId.isBlank()) {
            resp.setStatus(400);
            return;
        }

        if (!dataAccessor.isChatMember(chatId, jwt.getPayload().getSub())) {
            resp.setStatus(403);
            return;
        }

        chatStream.broadcast(
            chatId,
            gson.toJson(
                java.util.Map.of(
                    "type", "typing",
                    "chatId", chatId,
                    "userId", jwt.getPayload().getSub()
                )
            )
        );

        resp.setStatus(204); // no content
    }
}
