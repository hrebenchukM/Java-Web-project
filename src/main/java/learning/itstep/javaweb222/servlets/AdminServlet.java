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
import learning.itstep.javaweb222.data.jwt.JwtToken;
import learning.itstep.javaweb222.rest.RestMeta;
import learning.itstep.javaweb222.rest.RestResponse;
import learning.itstep.javaweb222.rest.RestStatus;

@Singleton
public class AdminServlet extends HttpServlet {

    private final DataAccessor dataAccessor;
    private final Gson gson = new Gson();

    private RestResponse restResponse;
    private JwtToken jwtToken;

    @Inject
    public AdminServlet(DataAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
    }

    @Override
    protected void service(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {

        this.restResponse = new RestResponse();
        this.restResponse.setMeta(
                new RestMeta()
                        .setServiceName("Admin API")
                        .setCacheSeconds(0)
                        .setManipulations(new String[]{"GET", "POST", "DELETE"})
                        .setLinks(Map.ofEntries(
                                Map.entry("notifications", "GET /admin/notifications"),
                                Map.entry("mark-read", "POST /admin/notifications/read"),
                                Map.entry("delete", "DELETE /admin/notification/{id}")
                        ))
        );

        this.jwtToken = (JwtToken) req.getAttribute("JWT");
        if (jwtToken == null) {
            restResponse.setStatus(RestStatus.status401);
        }
        else {
            super.service(req, resp);
        }

        resp.setContentType("application/json; charset=utf-8");
        resp.getWriter().print(gson.toJson(restResponse));
    }

    // ===================== GET =====================

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {

        String path = req.getPathInfo();

        if (path == null || "/notifications".equals(path)) {
            getNotifications();
        }
        else {
            restResponse.setStatus(RestStatus.status404);
            restResponse.setData("Path not found: " + path);
        }
    }

    private void getNotifications() {
        String userId = jwtToken.getPayload().getSub();
        restResponse.setData(
                dataAccessor.getUserNotifications(userId)
        );
    }

    // ===================== POST =====================

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {

        String path = req.getPathInfo();

        if ("/notifications/read".equals(path)) {
            markAllRead();
        }
        else {
            restResponse.setStatus(RestStatus.status404);
            restResponse.setData("Path not found: " + path);
        }
    }

    private void markAllRead() {
        String userId = jwtToken.getPayload().getSub();
        dataAccessor.markAllNotificationsAsRead(userId);
        restResponse.setData("Ok");
    }

    // ===================== DELETE =====================

    @Override
    protected void doDelete(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {

        String path = req.getPathInfo();
        if (path != null && path.startsWith("/notification/")) {
            String id = path.substring("/notification/".length());
            dataAccessor.deleteNotification(id);
            restResponse.setData("Ok");
        }
        else {
            restResponse.setStatus(RestStatus.status404);
            restResponse.setData("Path not found: " + path);
        }
    }
}
