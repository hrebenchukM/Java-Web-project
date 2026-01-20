package learning.itstep.javaweb222.servlets;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import learning.itstep.javaweb222.data.DataAccessor;
import learning.itstep.javaweb222.data.dto.Message;
import learning.itstep.javaweb222.data.jwt.JwtToken;
import learning.itstep.javaweb222.models.message.MessageFormModel;
import learning.itstep.javaweb222.websocket.WsRegistry;

@Singleton
public class MessageServlet extends HttpServlet {

    private final DataAccessor dataAccessor;
    private final Gson gson = new Gson();

    @Inject
    public MessageServlet(DataAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 🔐 JWT уже проверен фильтром
        JwtToken jwt = (JwtToken) req.getAttribute("JWT");
        if (jwt == null) {
            resp.setStatus(401);
            resp.getWriter().print("Unauthorized");
            return;
        }

        // 📥 читаем JSON
        MessageFormModel form =
                gson.fromJson(req.getReader(), MessageFormModel.class);

        if (form.getChatId() == null || form.getContent() == null) {
            resp.setStatus(400);
            resp.getWriter().print("chatId and content required");
            return;
        }

        // 🧱 создаём DTO
        Message message = new Message();

        message.setChatId(
            java.util.UUID.fromString(form.getChatId())
        );

        message.setSenderId(
            java.util.UUID.fromString(jwt.getPayload().getSub())
        );

        message.setContent(form.getContent());
        message.setIsDraft(form.getIsDraft());

        try {
            // 💾 сохраняем в БД
            dataAccessor.addMessage(message);

            // 🚀 PUSH через WebSocket (SignalR-аналог)
            WsRegistry.sendToChat(
                form.getChatId(),
                gson.toJson(Map.of(
                    "type", "message",
                    "chatId", form.getChatId(),
                    "message", message
                ))
            );

            resp.setContentType("application/json");
            resp.getWriter().print(
                gson.toJson(Map.of("status", "ok"))
            );
        }
        catch (Exception ex) {
            resp.setStatus(500);
            resp.getWriter().print(ex.getMessage());
        }
    }
}
