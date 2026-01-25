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

import learning.itstep.javaweb222.data.DataAccessor;
import learning.itstep.javaweb222.data.dto.User;
import learning.itstep.javaweb222.rest.*;

@Singleton
public class PortfolioServlet extends HttpServlet {

    private final DataAccessor dataAccessor;
    private final Gson gson = new GsonBuilder().serializeNulls().create();

    private RestResponse restResponse;

    @Inject
    public PortfolioServlet(DataAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
    }

    // ======================== SERVICE ========================
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        restResponse = new RestResponse();
        restResponse.setMeta(
            new RestMeta()
                .setServiceName("Portfolio API")
                .setCacheSeconds(300)
                .setManipulations(new String[]{"GET"})
                .setLinks(Map.of(
                    "portfolio", "GET /portfolio/{userId}"
                ))
        );

        super.service(req, resp);

        resp.setContentType("application/json; charset=utf-8");
        resp.getWriter().print(gson.toJson(restResponse));
    }

    // ======================== GET ========================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getPathInfo(); // /{userId}

        if (path == null || path.equals("/")) {
            restResponse.setStatus(RestStatus.status400);
            restResponse.setData("UserId required: /portfolio/{userId}");
            return;
        }

        String userId = path.substring(1);

        // ===== validate UUID =====
        try {
            UUID.fromString(userId);
        }
        catch (Exception ex) {
            restResponse.setStatus(RestStatus.status400);
            restResponse.setData("Invalid userId UUID");
            return;
        }

        // ===== user =====
        User user = dataAccessor.getUserById(userId);
        if (user == null) {
            restResponse.setStatus(RestStatus.status404);
            restResponse.setData("User not found");
            return;
        }

        // ===== aggregate portfolio (ONLY via DataAccessor) =====
        restResponse.setData(Map.of(
            "user", user,
            "experience", dataAccessor.getPortfolioExperienceBlocks(userId),
            "education", dataAccessor.getPortfolioEducations(userId),
            "certificates", dataAccessor.getPortfolioCertificates(userId),
            "skills", dataAccessor.getPortfolioSkills(userId),
            "languages", dataAccessor.getPortfolioLanguages(userId),
            "recommendations", dataAccessor.getPortfolioRecommendations(userId)
        ));

        restResponse.getMeta().setDataType("object");
    }
}
