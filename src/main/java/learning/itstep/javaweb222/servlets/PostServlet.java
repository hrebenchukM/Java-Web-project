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
import java.util.UUID;
import static java.lang.Math.ceil;
import org.apache.commons.fileupload2.core.FileItem;
import learning.itstep.javaweb222.data.DataAccessor;
import learning.itstep.javaweb222.data.dto.Post;
import learning.itstep.javaweb222.data.jwt.JwtToken;
import learning.itstep.javaweb222.rest.*;
import learning.itstep.javaweb222.services.form.FormParseException;
import learning.itstep.javaweb222.services.form.FormParseResult;
import learning.itstep.javaweb222.services.form.FormParseService;
import learning.itstep.javaweb222.services.storage.StorageService;

@Singleton
public class PostServlet extends HttpServlet {

    private final DataAccessor dataAccessor;
    private final FormParseService formParseService;
    private final Gson gson = new GsonBuilder().serializeNulls().create();
    private final StorageService storageService;

    private RestResponse restResponse;
    private JwtToken jwtToken;

    @Inject
    public PostServlet(
            DataAccessor dataAccessor,
            FormParseService formParseService,
            StorageService storageService
    ) {
        this.dataAccessor = dataAccessor;
        this.formParseService = formParseService;
        this.storageService = storageService;
    }

    // ======================== SERVICE ========================
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        restResponse = new RestResponse();
        restResponse.setMeta(
            new RestMeta()
                .setServiceName("API 'Post'")
                .setCacheSeconds(30)
                .setManipulations(new String[]{"GET", "POST"})
                .setLinks(Map.ofEntries(
                    Map.entry("feed", "/post"),
                    Map.entry("post", "/post/{id}"),
                    Map.entry("create", "POST /post")
                ))
        );

        jwtToken = (JwtToken) req.getAttribute("JWT");

        String path = req.getPathInfo();
        boolean isRoot = path == null || path.isBlank() || "/".equals(path);

        // GET /post — публичный
        if ("GET".equals(req.getMethod()) && isRoot) {
            super.service(req, resp);
        }
        // POST /post — только с JWT
        else if ("POST".equals(req.getMethod()) && isRoot) {
            if (jwtToken == null) {
                restResponse.setStatus(RestStatus.status401);
                restResponse.setData("JWT required");
            } else {
                super.service(req, resp);
            }
        }
        // остальное — требует JWT
        else if (jwtToken == null) {
            restResponse.setStatus(RestStatus.status401);
            restResponse.setData("JWT required");
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

        // ===== FEED =====
        if (path == null || path.isBlank() || "/".equals(path)) {

            int perPage = 10;
            String perPageParam = req.getParameter("perPage");
            if (perPageParam != null) {
                try {
                    perPage = Integer.parseInt(perPageParam);
                    if (perPage < 1) throw new NumberFormatException();
                }
                catch (NumberFormatException ex) {
                    restResponse.setStatus(RestStatus.status400);
                    restResponse.setData("Parameter 'perPage' must be positive");
                    return;
                }
            }

            int totalItems = dataAccessor.getFeedCount();
            int lastPage = (int) ceil((double) totalItems / perPage);

            int page = 1;
            String pageParam = req.getParameter("page");
            if (pageParam != null) {
                try {
                    page = Integer.parseInt(pageParam);
                    if (page < 1 || page > lastPage) throw new NumberFormatException();
                }
                catch (NumberFormatException ex) {
                    restResponse.setStatus(RestStatus.status400);
                    restResponse.setData(
                        "Parameter 'page' must be between 1 and " + lastPage
                    );
                    return;
                }
            }

            restResponse.getMeta().setPagination(
                new RestPagination()
                    .setCurrentPage(page)
                    .setPerPage(perPage)
                    .setLastPage(lastPage)
                    .setTotalItems(totalItems)
                    .setFirstPageHref("/post?page=1&perPage=" + perPage)
            );

            restResponse.setData(
                dataAccessor.getFeed(page, perPage)
            );
            restResponse.getMeta().setDataType("array");
            return;
        }

        // ===== SINGLE POST =====
        String postId = path.substring(1);
        Post post = dataAccessor.getPostById(postId);

        if (post == null) {
            restResponse.setStatus(RestStatus.status404);
            restResponse.setData("Post not found: " + postId);
        }
        else {
            restResponse.setData(post);
            restResponse.getMeta().setDataType("object");
        }
    }

    // ======================== POST ========================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getPathInfo();

        if (path == null || path.isBlank() || "/".equals(path)) {
            createPost(req);
        }
        else {
            restResponse.setStatus(RestStatus.status405);
            restResponse.setData("POST not allowed for " + path);
        }
    }

    // ======================== CREATE POST ========================
  private void createPost(HttpServletRequest req) {

    try {
        FormParseResult res = formParseService.parse(req);
        Map<String, String> fields = res.getFields();
        var files = res.getFiles().values(); // Collection<FileItem>

        String content = fields.get("content");
        if (content == null || content.isBlank()) {
            throw new FormParseException("Missing 'content'");
        }

        Post post = new Post()
            .setUserId(UUID.fromString(jwtToken.getPayload().getSub()))
            .setContent(content)
            .setVisibility(
                fields.getOrDefault("visibility", "public")
            );

        // 1️⃣ создаём пост
        Post created = dataAccessor.addPost(post);

        // 2️⃣ если есть файл — сохраняем
        if (!files.isEmpty()) {
            FileItem file = files.stream().findFirst().get();

            // сохраняем файл
            String fileName = storageService.save(file);

            // связываем media ↔ post
            dataAccessor.attachMediaToPost(
                created.getId(),
                fileName,
                "image"
            );
        }

        restResponse.setData(created);
        restResponse.getMeta().setDataType("object");
    }
    catch (FormParseException ex) {
        restResponse.setStatus(RestStatus.status400);
        restResponse.setData(ex.getMessage());
    }
    catch (Exception ex) {
        restResponse.setStatus(RestStatus.status500);
        restResponse.setData(ex.getMessage());
    }
}

}
