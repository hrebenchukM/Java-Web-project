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
import learning.itstep.javaweb222.data.dto.Post;
import learning.itstep.javaweb222.rest.RestMeta;
import learning.itstep.javaweb222.rest.RestResponse;
import learning.itstep.javaweb222.rest.RestStatus;

@Singleton
public class PostServlet extends HttpServlet {

    private final DataAccessor dataAccessor;
    private final Gson gson = new GsonBuilder().serializeNulls().create();

    private RestResponse restResponse;

    @Inject
    public PostServlet(DataAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
    }

    @Override
    protected void service(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {

        this.restResponse = new RestResponse();
        restResponse.setMeta(
                new RestMeta()
                        .setServiceName("API 'Post'")
                        .setCacheSeconds(30)
                        .setManipulations(new String[]{"GET"})
                        .setLinks(Map.ofEntries(
                                Map.entry("feed", "GET /post"),
                                Map.entry("post", "GET /post/{id}")
                        ))
        );

        super.service(req, resp);

        resp.setContentType("application/json; charset=utf-8");
        resp.getWriter().print(gson.toJson(restResponse));
    }

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {

        String path = req.getPathInfo();

        // ===== FEED =====
        if (path == null || "/".equals(path)) {
            int page = parseInt(req.getParameter("page"), 1);
            int perPage = parseInt(req.getParameter("perPage"), 10);

            restResponse.setData(
                    dataAccessor.getFeed(page, perPage)
            );
            return;
        }

        // ===== SINGLE POST =====
        String postId = path.substring(1);
        Post post = dataAccessor.getPostById(postId);

        if (post == null) {
            restResponse.setStatus(RestStatus.status404);
            restResponse.setData("Post not found: " + postId);
            restResponse.getMeta().setDataType("string");
        }
        else {
            restResponse.setData(post);
            restResponse.getMeta().setDataType("object");
        }
    }

    private int parseInt(String val, int def) {
        try {
            return Integer.parseInt(val);
        }
        catch (Exception ex) {
            return def;
        }
    }
}
