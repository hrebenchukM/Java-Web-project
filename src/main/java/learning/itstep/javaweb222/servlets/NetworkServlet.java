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
import learning.itstep.javaweb222.data.dto.UserActivity;
import learning.itstep.javaweb222.data.jwt.JwtToken;
import learning.itstep.javaweb222.rest.*;

@Singleton
public class NetworkServlet extends HttpServlet {

    private final DataAccessor dataAccessor;
    private final Gson gson = new GsonBuilder().serializeNulls().create();

    private RestResponse restResponse;
    private JwtToken jwtToken;

    @Inject
    public NetworkServlet(DataAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
    }

    // ======================== SERVICE ========================
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        restResponse = new RestResponse();
        restResponse.setMeta(
            new RestMeta()
                .setServiceName("Shop API 'Network'")
                .setCacheSeconds(0)
                .setManipulations(new String[]{"GET", "POST"})
                .setLinks(Map.ofEntries(
                    Map.entry("suggestions", "GET /network/suggestions"),
                    Map.entry("activity", "GET /network/activity"),
                    Map.entry("my-activity", "GET /network/activity/my"),
                    Map.entry("request", "POST /network/request"),
                    Map.entry("accept", "POST /network/accept"),
                    Map.entry("remove", "POST /network/remove")
                ))
        );

        jwtToken = (JwtToken) req.getAttribute("JWT");

        if (jwtToken == null) {
            restResponse.setStatus(RestStatus.status401);
        }
        else {
            super.service(req, resp);
        }

        resp.setContentType("application/json; charset=utf-8");
        resp.getWriter().print(gson.toJson(restResponse));
    }

    // ======================== GET ========================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getPathInfo();

        if ("/suggestions".equals(path)) {
            suggestions();
        }
        else if ("/activity".equals(path) || "/activity/".equals(path)) {
            getNetworkActivity(req);
        }
        else if ("/activity/my".equals(path)) {
            getMyActivity(req);
        }
        else {
            restResponse.setStatus(RestStatus.status404);
            restResponse.setData("Path not found: " + path);
        }
    }

    // ======================== POST ========================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getPathInfo();

        switch (path) {
            case "/request": request(req); break;
            case "/accept": accept(req); break;
            case "/remove": remove(req); break;
            default:
                restResponse.setStatus(RestStatus.status405);
                restResponse.setData("POST not allowed for " + path);
        }
    }

    // ======================== HANDLERS ========================

    private void suggestions() {
        restResponse.setData(
            dataAccessor.getSuggestedUsers(
                jwtToken.getPayload().getSub()
            )
        );
        restResponse.getMeta().setDataType("array");
    }

    private void request(HttpServletRequest req) {
        String targetId = req.getParameter("userId");
        if (targetId == null || targetId.isBlank()) {
            restResponse.setStatus(RestStatus.status400);
            restResponse.setData("userId required");
            return;
        }

        boolean ok = dataAccessor.sendConnectionRequest(
            jwtToken.getPayload().getSub(),
            targetId
        );

        restResponse.setData(ok ? "Request sent" : "Request failed");
        if (!ok) restResponse.setStatus(RestStatus.status400);
    }

    private void accept(HttpServletRequest req) {
        String requesterId = req.getParameter("userId");
        if (requesterId == null || requesterId.isBlank()) {
            restResponse.setStatus(RestStatus.status400);
            restResponse.setData("userId required");
            return;
        }

        boolean ok = dataAccessor.acceptConnection(
            requesterId,
            jwtToken.getPayload().getSub()
        );

        restResponse.setData(ok ? "Connection accepted" : "Accept failed");
        if (!ok) restResponse.setStatus(RestStatus.status400);
    }

    private void remove(HttpServletRequest req) {
        String otherId = req.getParameter("userId");
        if (otherId == null || otherId.isBlank()) {
            restResponse.setStatus(RestStatus.status400);
            restResponse.setData("userId required");
            return;
        }

        boolean ok = dataAccessor.removeConnection(
            jwtToken.getPayload().getSub(),
            otherId
        );

        restResponse.setData(ok ? "Connection removed" : "Remove failed");
        if (!ok) restResponse.setStatus(RestStatus.status400);
    }

    // ======================== ACTIVITY ========================

    private void getNetworkActivity(HttpServletRequest req) {

        String userId = jwtToken.getPayload().getSub();

        int perpage = parseInt(req.getParameter("perpage"), 10);
        int page = parseInt(req.getParameter("page"), 1);
        int offset = (page - 1) * perpage;

        List<UserActivity> list =
                dataAccessor.getNetworkActivity(userId, perpage, offset);

        restResponse.getMeta().setDataType("array");
        restResponse.setData(list);
    }

    private void getMyActivity(HttpServletRequest req) {

        String userId = jwtToken.getPayload().getSub();

        int perpage = parseInt(req.getParameter("perpage"), 10);
        int page = parseInt(req.getParameter("page"), 1);
        int offset = (page - 1) * perpage;

        List<UserActivity> list =
                dataAccessor.getMyActivity(userId, perpage, offset);

        restResponse.getMeta().setDataType("array");
        restResponse.setData(list);
    }

    // ======================== UTILS ========================

    private int parseInt(String value, int def) {
        if (value == null) return def;
        try {
            int v = Integer.parseInt(value);
            return v > 0 ? v : def;
        }
        catch (Exception ex) {
            return def;
        }
    }
}
