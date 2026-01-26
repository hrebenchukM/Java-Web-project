package learning.itstep.javaweb222.servlets.stream;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import learning.itstep.javaweb222.data.DataAccessor;
import learning.itstep.javaweb222.data.jwt.JwtToken;

@Singleton
public class ChatStreamServlet extends HttpServlet {

    /*
     ChatStreamServlet — SSE endpoint для realtime-подій чату.
     
     АНАЛОГ SignalR Hub у C#.
     
     ЙОГО РОЛЬ:
     - тримати відкриті HTTP-зʼєднання з клієнтами
     - групувати клієнтів по chatId
     - розсилати події усім учасникам чату
     
     ВАЖЛИВО:
     - ChatStreamServlet НЕ пише у БД
     - НЕ має бізнес-логіки
     - він лише "труба" для подій
    */

    /*
     chatId -> список відкритих HTTP-відповідей (SSE)
     
     Це аналог:
     SignalR Groups / Connections
    */
    private final DataAccessor dataAccessor;

    @Inject
    public ChatStreamServlet(DataAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
    }
    private final Map<String, List<HttpServletResponse>> streams =
            new ConcurrentHashMap<>();
    private final Map<String, Long> onlineUsers = new ConcurrentHashMap<>();
    private final Map<HttpServletResponse, String> connections =
            new ConcurrentHashMap<>();

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        JwtToken jwt = (JwtToken) req.getAttribute("JWT");
        if (jwt == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String userId = jwt.getPayload().getSub();
        onlineUsers.put(userId, System.currentTimeMillis());
        // 🔗 СВЯЗЫВАЕМ response ↔ userId
        connections.put(resp, userId);
        String chatId = req.getParameter("chatId");
        if (chatId == null || chatId.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        /*
         🔒 ПЕРЕВІРКА УЧАСТІ В ЧАТІ
         */
        if (!dataAccessor.isChatMember(
                chatId,
                jwt.getPayload().getSub()
        )) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // SSE headers
        resp.setContentType("text/event-stream");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setHeader("Connection", "keep-alive");

        streams
            .computeIfAbsent(chatId, k -> new CopyOnWriteArrayList<>())
            .add(resp);
    }


    // =================== PUSH EVENT ===================

    /*
     Метод broadcast — серце realtime.
     
     Його викликають REST-сервлети (наприклад MessageServlet),
     коли у системі зʼявляється нова подія.
     
     Це прямий аналог:
     SignalR: Clients.Group(chatId).SendAsync(...)
    */
    public void broadcast(String chatId, String json) {
        List<HttpServletResponse> list = streams.get(chatId);
        if (list == null) return;

        for (HttpServletResponse r : list) {
            try {
                /*
                 Формат SSE:
                 data: <json>
                 
                 Подвійний перенос рядка — кінець події.
                */
                r.getWriter().write("data: " + json + "\n\n");
                r.getWriter().flush();
            }
            catch (Exception ex) {
                list.remove(r);

                String userId = connections.remove(r);
                if (userId != null) {
                    onlineUsers.remove(userId);
                    dataAccessor.updateLastSeen(userId);
                }
            }

        }
    }
}
