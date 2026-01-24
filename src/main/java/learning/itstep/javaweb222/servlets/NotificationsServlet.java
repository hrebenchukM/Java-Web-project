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
import learning.itstep.javaweb222.data.DataAccessor;
import learning.itstep.javaweb222.data.dto.Notification;
import learning.itstep.javaweb222.data.jwt.JwtToken;
import learning.itstep.javaweb222.rest.RestMeta;
import learning.itstep.javaweb222.rest.RestResponse;
import learning.itstep.javaweb222.rest.RestStatus;

@Singleton
public class NotificationsServlet extends HttpServlet {

    private final DataAccessor dataAccessor;
    private final Gson gson = new GsonBuilder().serializeNulls().create();
    private RestResponse restResponse;
    private JwtToken jwtToken;

    @Inject
    public NotificationsServlet(DataAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        this.restResponse = new RestResponse();
        restResponse.setMeta(
            new RestMeta()
                .setServiceName("LinkedIn API 'Notifications'")
                .setCacheSeconds(0)
                .setManipulations(new String[]{"GET","PATCH","PUT","DELETE"})
                .setLinks(Map.ofEntries(
                    Map.entry("all", "GET /notifications"),
                    Map.entry("filter", "GET /notifications?filter={type}"),
                    Map.entry("read-one", "PATCH /notifications?notification-id={id}"),
                    Map.entry("read-all", "PUT /notifications"),
                    Map.entry("delete", "DELETE /notifications?notification-id={id}")
                ))
        );

        jwtToken = (JwtToken) req.getAttribute("JWT");
        if (jwtToken == null) {
            this.restResponse.setStatus(RestStatus.status401);
        }
        else {
            super.service(req, resp);
        }

        resp.setContentType("application/json; charset=utf-8");
        resp.getWriter().print(
                gson.toJson(restResponse)
        );
    }

    // ================== GET ==================

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String userId = jwtToken.getPayload().getSub();
        String filter = req.getParameter("filter");

        try {
            List<Notification> data;

            if (filter == null || filter.isBlank() || "all".equals(filter)) {
                data = dataAccessor.getUserNotifications(userId);
            }
            else switch (filter) {
                case "vacancies":
                    data = dataAccessor.getVacancyNotifications(userId);
                    break;
                case "publications":
                    data = dataAccessor.getPublicationNotifications(userId);
                    break;
                case "mentions":
                    data = dataAccessor.getMentionNotifications(userId);
                    break;
                default:
                    this.restResponse.setStatus(RestStatus.status400);
                    this.restResponse.getMeta().setDataType("string");
                    this.restResponse.setData("Unknown filter: " + filter);
                    return;
            }

            this.restResponse.getMeta().setDataType("json.array");
            this.restResponse.setData(data);
            this.restResponse.getMeta().getParams().put(
                "unread",
                String.valueOf(
                    dataAccessor.getUnreadNotificationsCount(userId)
                )
            );

        }
        catch (Exception ex) {
            this.restResponse.getMeta().setDataType("string");
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData(ex.getMessage());
        }
    }

    // ================== PATCH ==================
    // mark one as read

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String id = req.getParameter("notification-id");
        if (id == null || id.isBlank()) {
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData("Missing query parameter: notification-id");
            return;
        }

        try {
            dataAccessor.markNotificationAsRead(id);
            this.restResponse.getMeta().setDataType("null");
        }
        catch (Exception ex) {
            this.restResponse.getMeta().setDataType("string");
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData(ex.getMessage());
        }
    }

    // ================== PUT ==================
    // mark all as read

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            dataAccessor.markAllNotificationsAsRead(
                    jwtToken.getPayload().getSub());
            this.restResponse.getMeta().setDataType("null");
        }
        catch (Exception ex) {
            this.restResponse.getMeta().setDataType("string");
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData(ex.getMessage());
        }
    }

    // ================== DELETE ==================

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String id = req.getParameter("notification-id");
        if (id == null || id.isBlank()) {
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData("Missing query parameter: notification-id");
            return;
        }

        try {
            dataAccessor.deleteNotification(id);
            this.restResponse.getMeta().setDataType("null");
        }
        catch (Exception ex) {
            this.restResponse.getMeta().setDataType("string");
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData(ex.getMessage());
        }
    }
}
