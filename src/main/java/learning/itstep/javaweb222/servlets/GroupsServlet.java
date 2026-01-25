
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
import java.util.Map;
import learning.itstep.javaweb222.data.DataAccessor;
import learning.itstep.javaweb222.data.jwt.JwtToken;
import learning.itstep.javaweb222.models.group.GroupModel;
import learning.itstep.javaweb222.rest.RestMeta;
import learning.itstep.javaweb222.rest.RestResponse;
import learning.itstep.javaweb222.rest.RestStatus;

@Singleton
public class GroupsServlet extends HttpServlet {

    private final DataAccessor dataAccessor;
    private final Gson gson = new GsonBuilder().serializeNulls().create();
    private RestResponse restResponse;
    private JwtToken jwtToken;

    @Inject
    public GroupsServlet(DataAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        restResponse = new RestResponse();
        restResponse.setMeta(
            new RestMeta()
                .setServiceName("LinkedIn API 'Groups'")
                .setManipulations(new String[]{"GET"})
                .setLinks(Map.ofEntries(
                    Map.entry("my", "GET /groups/my"),
                    Map.entry("group", "GET /groups/{id}"),
                    Map.entry("members", "GET /groups/{id}/members"),
                    Map.entry("posts", "GET /groups/{id}/posts")
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
    
    
    
    
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) {

    String path = req.getPathInfo();

    if (path == null || path.equals("/")) {
        restResponse.setStatus(RestStatus.status400);
        restResponse.setData("Missing path parameter");
        restResponse.getMeta().setDataType("string");
        return;
    }
    // ---------- /groups/my ----------
    if ("/my".equals(path)) {
        restResponse.setData(
            dataAccessor.getMyGroups(jwtToken.getPayload().getSub())
        );
        restResponse.getMeta().setDataType("array");
        return;
    }

    // ---------- /groups/{id}/members ----------
    if (path.endsWith("/members")) {

        String groupId = path.substring(1, path.length() - "/members".length());

        restResponse.setData(
            dataAccessor.getGroupMembers(groupId)
        );
        restResponse.getMeta().setDataType("array");
        return;
    }
    if (path.endsWith("/posts")) {
        String groupId = path.substring(1, path.length() - "/posts".length());
        restResponse.setData(dataAccessor.getGroupPosts(groupId));
        restResponse.getMeta().setDataType("array");
        return;
    }

    // ---------- /groups/{id} ----------
    String id = path.substring(1);

    GroupModel group = dataAccessor.getGroupById(id);
    if (group == null) {
        restResponse.setStatus(RestStatus.status404);
        restResponse.setData("Group not found: " + id);
        restResponse.getMeta().setDataType("string");
        return;
    }

    restResponse.setData(group);
    restResponse.getMeta().setDataType("object");
}

}
