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
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import learning.itstep.javaweb222.data.DataAccessor;
import learning.itstep.javaweb222.data.dto.*;
import learning.itstep.javaweb222.data.jwt.JwtToken;
import learning.itstep.javaweb222.models.user.UserProfileModel;
import learning.itstep.javaweb222.rest.*;
import learning.itstep.javaweb222.services.Signature.SignatureService;
import learning.itstep.javaweb222.services.form.FormParseException;
import learning.itstep.javaweb222.services.form.FormParseResult;
import learning.itstep.javaweb222.services.form.FormParseService;
import learning.itstep.javaweb222.services.storage.StorageService;
import org.apache.commons.fileupload2.core.FileItem;

@Singleton
public class UserServlet extends HttpServlet {

    private final DataAccessor dataAccessor;
    private final SignatureService signatureService;
    private final FormParseService formParseService;
    private final StorageService storageService;

    private final Gson gson = new GsonBuilder().serializeNulls().create();

    private RestResponse restResponse;
    private JwtToken jwtToken;

    @Inject
    public UserServlet(
            DataAccessor dataAccessor,
            SignatureService signatureService,
            FormParseService formParseService,
            StorageService storageService
    ) {
        this.dataAccessor = dataAccessor;
        this.signatureService = signatureService;
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
                .setServiceName("Shop API 'User'")
                .setCacheSeconds(1000)
                .setManipulations(new String[]{"GET", "POST"})
                .setLinks(Map.ofEntries(
                    Map.entry("authenticate", "GET /user"),
                    Map.entry("profile", "GET /user/profile"),
                    Map.entry("analytics", "GET /user/analytics"),
                    Map.entry("experience", "/user/experience"),
                    Map.entry("education", "/user/education"),
                    Map.entry("certificates", "/user/certificates"),
                    Map.entry("skills", "/user/skills")
                ))
        );

        jwtToken = (JwtToken) req.getAttribute("JWT");

        String path = req.getPathInfo();

        // root /user — auth
        if (path == null || path.isBlank()) {
            super.service(req, resp);
        }
        // everything else — JWT required
        else if (jwtToken == null) {
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

        if (path == null || path.isBlank()) {
            authenticate(req);
            return;
        }
          // 👇 НОВОЕ
        if (path.matches("^/[0-9a-fA-F\\-]{36}$")) {
            getUserById(path.substring(1));
            return;
        }


        switch (path) {
            case "/profile": profile(); break;
            case "/analytics": analytics(); break;
            case "/experience": experienceGet(); break;
            case "/education": educationGet(); break;
            case "/certificates": certificatesGet(); break;
            case "/skills": skillsGet(); break;
            default:
                restResponse.setStatus(RestStatus.status404);
                restResponse.setData("Path not found: " + path);
        }
    }

    // ======================== POST ========================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getPathInfo();
        restResponse.getMeta().setDataType("string");

      
        switch (path) {
            case "/experience": experiencePost(req); break;
            case "/education": educationPost(req); break;
            case "/certificates": certificatesPost(req); break;
            case "/skills": skillsPost(req); break;
            default:
                restResponse.setStatus(RestStatus.status405);
                restResponse.setData("POST not allowed for " + path);
        }
    }

    private void getUserById(String userId) {
        try {
            UUID uid = UUID.fromString(userId);

            User user = dataAccessor.getUserById(uid);

            if (user == null) {
                restResponse.setStatus(RestStatus.status404);
                restResponse.setData("User not found");
                return;
            }

            restResponse.setData(user);
            restResponse.getMeta().setDataType("object");
        }
        catch (Exception ex) {
            restResponse.setStatus(RestStatus.status400);
            restResponse.setData("Invalid user id");
        }
    }

    // ======================== EXPERIENCE ========================
   private void experiencePost(HttpServletRequest req) {
    try {
        FormParseResult res = formParseService.parse(req);
        Map<String, String> f = res.getFields();

        String companyName = f.get("companyName");
        if (companyName == null || companyName.isBlank()) {
            throw new FormParseException("companyName required");
        }

        UUID companyId = dataAccessor.getOrCreateCompanyByName(
            companyName,
            jwtToken.getPayload().getSub()
        );

        Experience exp = new Experience()
            .setCompanyId(companyId)
            .setPosition(f.get("position"))
            .setEmploymentType(f.get("employmentType"))
            .setWorkLocationType(f.get("workLocationType"))
            .setLocation(f.get("location"))
            .setDescription(f.get("description"))
            .setStartDate(
                f.get("startDate") != null
                    ? java.sql.Date.valueOf(f.get("startDate"))
                    : null
            )
            .setEndDate(
                f.get("endDate") != null
                    ? java.sql.Date.valueOf(f.get("endDate"))
                    : null
            );


        dataAccessor.addExperience(
            jwtToken.getPayload().getSub(),
            exp
        );

        restResponse.setData("Experience added");
        restResponse.getMeta().setDataType("string");
    }
    catch (Exception ex) {
        restResponse.setStatus(RestStatus.status400);
        restResponse.setData(ex.getMessage());
    }
}

    // ======================== EDUCATION ========================
private void educationPost(HttpServletRequest req) {
    try {
        FormParseResult res = formParseService.parse(req);
        Map<String, String> f = res.getFields();

        Education edu = new Education()
            .setInstitution(f.get("institution"))
            .setDegree(f.get("degree"))
            .setFieldOfStudy(f.get("fieldOfStudy"))
            .setStartDate(
                f.get("startDate") != null
                    ? java.sql.Date.valueOf(f.get("startDate"))
                    : null
            )
            .setEndDate(
                f.get("endDate") != null
                    ? java.sql.Date.valueOf(f.get("endDate"))
                    : null
            )
            .setSource(f.get("source"));

        dataAccessor.addEducation(
            jwtToken.getPayload().getSub(),
            edu
        );

        restResponse.setData("Education added");
    }
    catch (Exception ex) {
        restResponse.setStatus(RestStatus.status400);
        restResponse.setData(ex.getMessage());
    }
}

    // ======================== CERTIFICATES ========================
    private void certificatesPost(HttpServletRequest req) {
        try {
            FormParseResult res = formParseService.parse(req);
            Map<String, String> f = res.getFields();
            FileItem file = res.getFiles().values().stream().findFirst().orElse(null);

            Certificate cert = new Certificate()
                .setName(f.get("name"))
                .setAccreditationId(f.get("accreditationId"))
                .setOrganizationUrl(f.get("organizationUrl"));

            if (file != null) {
                cert.setDownloadRef(storageService.save(file));
            }

            dataAccessor.addCertificate(
                jwtToken.getPayload().getSub(),
                cert,
                f.get("academyName")
            );

            restResponse.setData("Certificate added");
        }
        catch (Exception ex) {
            restResponse.setStatus(RestStatus.status400);
            restResponse.setData(ex.getMessage());
        }
    }

    // ======================== SKILLS ========================
    private void skillsPost(HttpServletRequest req) {
        try {
            FormParseResult res = formParseService.parse(req);
            Map<String, String> f = res.getFields();

            dataAccessor.addSkill(
                jwtToken.getPayload().getSub(),
                f.get("name"),
                f.get("level"),
                Boolean.parseBoolean(f.getOrDefault("isMain", "false"))
            );

            restResponse.setData("Skill added");
        }
        catch (Exception ex) {
            restResponse.setStatus(RestStatus.status400);
            restResponse.setData(ex.getMessage());
        }
    }

    // ======================== GET HELPERS ========================
    private void profile() {
        AuthCredential ua = dataAccessor.getUserAccess(
            jwtToken.getPayload().getSub(),
            jwtToken.getPayload().getAud()
        );

        if (ua == null || ua.getUser() == null) {
            restResponse.setStatus(RestStatus.status404);
            restResponse.setData("User not found");
            return;
        }

        restResponse.setData(
            new UserProfileModel()
                .setLogin(ua.getLogin())
                .setRole(ua.getRoleId())
                .setUser(ua.getUser())
        );
        restResponse.getMeta().setDataType("object");
    }

    private void analytics() {
        String uid = jwtToken.getPayload().getSub();
        restResponse.setData(Map.of(
            "profileViews", dataAccessor.getProfileViewsCount(uid),
            "postViews", dataAccessor.getPostViewsCount(uid)
        ));
        restResponse.getMeta().setDataType("object");
    }

    private void experienceGet() {
        restResponse.setData(
            dataAccessor.getUserExperienceBlocks(jwtToken.getPayload().getSub())
        );
        restResponse.getMeta().setDataType("array");
    }

    private void educationGet() {
        restResponse.setData(
            dataAccessor.getUserEducations(jwtToken.getPayload().getSub())
        );
        restResponse.getMeta().setDataType("array");
    }

    private void certificatesGet() {
        restResponse.setData(
            dataAccessor.getUserCertificates(jwtToken.getPayload().getSub())
        );
        restResponse.getMeta().setDataType("array");
    }

    private void skillsGet() {
        restResponse.setData(
            dataAccessor.getUserSkills(jwtToken.getPayload().getSub())
        );
        restResponse.getMeta().setDataType("array");
    }

    // ======================== AUTH ========================
    private void authenticate(HttpServletRequest req) {
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Basic ")) {
            restResponse.setStatus(RestStatus.status401);
            restResponse.setData("Invalid Authorization header");
            return;
        }

        String[] parts = new String(Base64.getDecoder()
                .decode(auth.substring(6))).split(":", 2);

        if (parts.length != 2) {
            restResponse.setStatus(RestStatus.status401);
            restResponse.setData("Invalid credentials format");
            return;
        }

        AuthCredential ua =
            dataAccessor.getUserAccessByCredentials(parts[0], parts[1]);

        if (ua == null) {
            restResponse.setStatus(RestStatus.status401);
            restResponse.setData("Access denied");
            return;
        }

        AccessToken at = dataAccessor.createAccessToken(
            ua,
            req.getHeader("User-Agent"),
            req.getRemoteAddr()
        );

        JwtToken jwt = JwtToken.fromAccessToken(at);
        jwt.setSignature(
            Base64.getEncoder().encodeToString(
                signatureService.getSignatureBytes(jwt.getBody(), "secret")
            )
        );

        restResponse.setData(jwt.toString());
    }
}
