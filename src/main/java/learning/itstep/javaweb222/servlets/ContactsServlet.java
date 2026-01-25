
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
import learning.itstep.javaweb222.rest.RestMeta;
import learning.itstep.javaweb222.rest.RestResponse;
import learning.itstep.javaweb222.rest.RestStatus;

@Singleton
public class ContactsServlet extends HttpServlet {

    private final DataAccessor dataAccessor;
    private final Gson gson = new GsonBuilder().serializeNulls().create();

    private RestResponse restResponse;
    private JwtToken jwtToken;

    @Inject
    public ContactsServlet(DataAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        restResponse = new RestResponse();
        restResponse.setMeta(
            new RestMeta()
                .setServiceName("LinkedIn API 'Contacts'")
                .setManipulations(new String[]{"GET"})
                .setLinks(Map.ofEntries(
                    Map.entry("contacts", "GET /contacts"),
                    Map.entry("following", "GET /contacts/following")
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

        if (path == null || path.isBlank() || "/".equals(path)) {
            restResponse.setData(
                dataAccessor.getContacts(jwtToken.getPayload().getSub())
            );
            restResponse.getMeta().setDataType("array");
            return;
        }

        if ("/following".equals(path)) {
            restResponse.setData(
                dataAccessor.getFollowing(jwtToken.getPayload().getSub())
            );
            restResponse.getMeta().setDataType("array");
            return;
        }

        restResponse.setStatus(RestStatus.status404);
        restResponse.setData("Path not found: " + path);
    }
}
