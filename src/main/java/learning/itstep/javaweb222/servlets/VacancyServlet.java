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
import learning.itstep.javaweb222.data.dto.Vacancy;
import learning.itstep.javaweb222.data.jwt.JwtToken;
import learning.itstep.javaweb222.rest.RestMeta;
import learning.itstep.javaweb222.rest.RestResponse;
import learning.itstep.javaweb222.rest.RestStatus;
import learning.itstep.javaweb222.services.form.FormParseException;
import learning.itstep.javaweb222.services.form.FormParseResult;
import learning.itstep.javaweb222.services.form.FormParseService;

@Singleton
public class VacancyServlet extends HttpServlet {

    private final DataAccessor dataAccessor;
    private final FormParseService formParseService;
    private final Gson gson = new GsonBuilder().serializeNulls().create();

    private RestResponse restResponse;
    private JwtToken jwtToken;

    @Inject
    public VacancyServlet(
            DataAccessor dataAccessor,
            FormParseService formParseService
    ) {
        this.dataAccessor = dataAccessor;
        this.formParseService = formParseService;
    }

    // ======================== SERVICE ========================
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        this.restResponse = new RestResponse();
        restResponse.setMeta(
                new RestMeta()
                        .setServiceName("Jobs API 'Vacancies'")
                        .setCacheSeconds(300)
                        .setManipulations(new String[]{"GET", "POST"})
                        .setLinks(Map.ofEntries(
                                Map.entry("list", "GET /vacancy"),
                                Map.entry("best", "GET /vacancy/best"),
                                Map.entry("create", "POST /vacancy")
                        ))
        );

        jwtToken = (JwtToken) req.getAttribute("JWT");

        super.service(req, resp);

        resp.setContentType("application/json; charset=utf-8");
        resp.getWriter().print(gson.toJson(restResponse));
    }

    // ======================== GET ========================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getPathInfo();

        // GET /vacancy
        if (path == null || path.isBlank() || "/".equals(path)) {
            List<Vacancy> vacancies = dataAccessor.getVacancies();
            restResponse.setData(vacancies);
            restResponse.getMeta().setDataType("json.array");
            return;
        }

        // GET /vacancy/best
        if ("/best".equals(path)) {
            int limit = 5;
            String limitParam = req.getParameter("limit");

            if (limitParam != null) {
                try {
                    limit = Integer.parseInt(limitParam);
                    if (limit < 1) throw new NumberFormatException();
                }
                catch (NumberFormatException ex) {
                    restResponse.setStatus(RestStatus.status400);
                    restResponse.setData(
                            "Parameter 'limit' must be positive integer"
                    );
                    return;
                }
            }

            restResponse.setData(
                    dataAccessor.getBestVacancies(limit)
            );
            restResponse.getMeta().setDataType("json.array");
            return;
        }

        restResponse.setStatus(RestStatus.status404);
        restResponse.setData("Path not found: " + path);
    }

    // ======================== POST ========================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (jwtToken == null) {
            restResponse.setStatus(RestStatus.status401);
            return;
        }

        String path = req.getPathInfo();
        if (path == null || path.isBlank() || "/".equals(path)) {
            postVacancy(req);
        }
        else {
            restResponse.setStatus(RestStatus.status404);
            restResponse.setData("Slug " + path + " not found");
        }
    }

    // ======================== CREATE ========================
    private void postVacancy(HttpServletRequest req) {

        try {
            FormParseResult res = formParseService.parse(req);
            Map<String, String> fields = res.getFields();

            String title = fields.get("title");
            String company = fields.get("company");

            if (title == null || title.isBlank()
                    || company == null || company.isBlank()) {
                throw new FormParseException(
                        "Fields 'title' and 'company' are required"
                );
            }

            Vacancy vacancy = new Vacancy()
                    .setTitle(title)
                    .setJobType(fields.get("jobType"))
                    .setSchedule(fields.get("workplaceType"))
                    .setLocation(fields.get("location"))
                    .setDescription(fields.get("description"));

            dataAccessor.addVacancy(
                    jwtToken.getPayload().getSub(),
                    company,
                    vacancy
            );

            restResponse.setData("Ok");
            restResponse.getMeta().setDataType("string");
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
