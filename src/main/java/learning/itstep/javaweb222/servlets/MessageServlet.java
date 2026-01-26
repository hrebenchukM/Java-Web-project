package learning.itstep.javaweb222.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import learning.itstep.javaweb222.data.DataAccessor;
import learning.itstep.javaweb222.data.dto.Message;
import learning.itstep.javaweb222.data.jwt.JwtToken;
import learning.itstep.javaweb222.models.message.MessageFormModel;
import learning.itstep.javaweb222.rest.*;
import learning.itstep.javaweb222.servlets.stream.ChatStreamServlet;

@Singleton
public class MessageServlet extends HttpServlet {

    private final DataAccessor dataAccessor;
    private final ChatStreamServlet chatStream;

    private final Gson gson =
            new GsonBuilder().serializeNulls().create();

    private RestResponse restResponse;
    private JwtToken jwtToken;

    @Inject
    public MessageServlet(
            DataAccessor dataAccessor,
            ChatStreamServlet chatStream
    ) {
        this.dataAccessor = dataAccessor;
        this.chatStream = chatStream;
    }

    // ======================== SERVICE ========================
    @Override
    protected void service(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {

        restResponse = new RestResponse();
        restResponse.setMeta(
            new RestMeta()
                .setServiceName("API 'Messages'")
                .setCacheSeconds(0)
                .setManipulations(new String[]{"GET", "POST"})
                .setLinks(
                    Map.of(
                        "get",  "GET /messages?chatId={id}",
                        "post", "POST /messages"
                    )
                )
        );

        jwtToken = (JwtToken) req.getAttribute("JWT");

        String method = req.getMethod();
        String path = req.getPathInfo();
        boolean isRoot = path == null || path.isBlank() || "/".equals(path);

        // ===== GET /messages (требует JWT)
        if ("GET".equals(method) && isRoot) {
            if (jwtToken == null) {
                restResponse.setStatus(RestStatus.status401);
                restResponse.setData("JWT required");
            }
            else {
                super.service(req, resp);
            }
        }
        // ===== POST /messages (требует JWT)
        else if ("POST".equals(method) && isRoot) {
            if (jwtToken == null) {
                restResponse.setStatus(RestStatus.status401);
                restResponse.setData("JWT required");
            }
            else {
                super.service(req, resp);
            }
        }
        // ===== всё остальное запрещено
        else {
            restResponse.setStatus(RestStatus.status404);
            restResponse.setData("Path not found: " + path);
        }

        resp.setContentType("application/json; charset=utf-8");
        resp.getWriter().print(gson.toJson(restResponse));
    }

    // ======================== GET ========================
    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp
    ) {

        String chatId = req.getParameter("chatId");
        if (chatId == null || chatId.isBlank()) {
            restResponse.setStatus(RestStatus.status400);
            restResponse.setData("Parameter 'chatId' required");
            return;
        }

        // 🔒 проверка участия в чате
        if (!dataAccessor.isChatMember(
                chatId,
                jwtToken.getPayload().getSub()
        )) {
            restResponse.setStatus(RestStatus.status403);
            restResponse.setData("You are not a member of this chat");
            return;
        }

        try {
            int totalItems =
                dataAccessor.getChatMessages(chatId).size();

            RestPagination pagination =
                RestPagination.fromRequest(req, totalItems);

            int offset =
                (pagination.getCurrentPage() - 1)
                * pagination.getPerPage();

            List<Message> messages =
                dataAccessor.getChatMessages(
                    chatId,
                    offset,
                    pagination.getPerPage()
                );

            pagination.setFirstPageHref(
                "/messages?chatId=" + chatId
                + "&page=1&perpage=" + pagination.getPerPage()
            );

            restResponse.getMeta().setPagination(pagination);
            restResponse.getMeta().setDataType("array");
            restResponse.setStatus(RestStatus.status200);
            restResponse.setData(messages);
        }
        catch (Exception ex) {
            restResponse.setStatus(RestStatus.status500);
            restResponse.setData(ex.getMessage());
        }
    }

    // ======================== POST ========================
    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        MessageFormModel form =
                gson.fromJson(req.getReader(), MessageFormModel.class);

        if (form == null
                || form.getChatId() == null
                || form.getContent() == null
                || form.getContent().isBlank()) {

            restResponse.setStatus(RestStatus.status400);
            restResponse.setData("chatId and content required");
            return;
        }

        // 🔒 проверка участия в чате
        if (!dataAccessor.isChatMember(
                form.getChatId(),
                jwtToken.getPayload().getSub()
        )) {
            restResponse.setStatus(RestStatus.status403);
            restResponse.setData("You are not a member of this chat");
            return;
        }

        // ===== 🔐 ВАЛИДАЦИЯ JWT SUBJECT =====
        String subject = jwtToken.getPayload() != null
                ? jwtToken.getPayload().getSub()
                : null;

        if (subject == null || subject.isBlank()) {
            restResponse.setStatus(RestStatus.status401);
            restResponse.setData("Invalid JWT subject");
            return;
        }

        UUID senderId;
        try {
            senderId = UUID.fromString(subject);
        }
        catch (Exception ex) {
            restResponse.setStatus(RestStatus.status401);
            restResponse.setData("Invalid JWT subject format");
            return;
        }

        // ===== ТВОЯ БИЗНЕС-ЛОГИКА (БЕЗ ИЗМЕНЕНИЙ) =====
        Message message = new Message()
                .setChatId(UUID.fromString(form.getChatId()))
                .setSenderId(senderId)
                .setContent(form.getContent())
                .setIsDraft(form.getIsDraft());

        try {
            UUID id = dataAccessor.addMessage(message);
            message.setId(id);

            // 🔴 realtime
            chatStream.broadcast(
                    message.getChatId().toString(),
                    gson.toJson(message)
            );

            restResponse.setStatus(RestStatus.status200);
            restResponse.setData(
                Map.of("status", "ok")
            );
        }
        catch (Exception ex) {
            restResponse.setStatus(RestStatus.status500);
            restResponse.setData(ex.getMessage());
        }
    }
}
