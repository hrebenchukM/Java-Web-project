package learning.itstep.javaweb222.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
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
import learning.itstep.javaweb222.data.dto.AccessToken;
import learning.itstep.javaweb222.data.dto.AuthCredential;
import learning.itstep.javaweb222.data.dto.Certificate;
import learning.itstep.javaweb222.data.dto.Education;
import learning.itstep.javaweb222.data.dto.Experience;
import learning.itstep.javaweb222.data.jwt.JwtToken;
import learning.itstep.javaweb222.models.user.UserProfileModel;
import learning.itstep.javaweb222.rest.RestMeta;
import learning.itstep.javaweb222.rest.RestResponse;
import learning.itstep.javaweb222.rest.RestStatus;
import learning.itstep.javaweb222.services.Signature.SignatureService;
import learning.itstep.javaweb222.services.form.FormParseService;
import learning.itstep.javaweb222.services.form.FormParseResult;
import learning.itstep.javaweb222.services.form.FormParseException;
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
                    Map.entry("experience-list", "GET /user/experience"),
                    Map.entry("experience-add", "POST /user/experience"),
                    Map.entry("education-list", "GET /user/education"),
                    Map.entry("education-add", "POST /user/education"),
                    Map.entry("certificate-list", "GET /user/certificates"),
                    Map.entry("certificate-add", "POST /user/certificates"),
                    Map.entry("skills-list", "GET /user/skills"),
                    Map.entry("skills-add", "POST /user/skills")


                ))

        );

        jwtToken = (JwtToken) req.getAttribute("JWT");

        String path = req.getPathInfo();

        // 🔴 AUTH ONLY for root /user
        if (path == null || path.isBlank()) {
            super.service(req, resp);
        }
        // 🔴 ALL OTHER endpoints REQUIRE JWT
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
        }
        else switch (path) {
            case "/profile":
                profile();
                break;

            case "/analytics":
                analytics();
                break;

            case "/experience":
                experienceGet();
                break;

            case "/education":
                educationGet();
                break;

            case "/certificates":
                certificatesGet();
                break;

            case "/skills":
                skillsGet();
                break;

            default:
                restResponse.setStatus(RestStatus.status404);
                restResponse.setData("Path not found: " + path);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getPathInfo();
        restResponse.getMeta().setDataType("string");

        if ("/experience".equals(path)) {
            experiencePost(req);
        }
        else if ("/education".equals(path)) {
            educationPost(req);
        }
        else if ("/certificates".equals(path)) {
            certificatesPost(req);
        }
        else if ("/skills".equals(path)) {
            skillsPost(req);
        }


        else {
            restResponse.setStatus(RestStatus.status405);
            restResponse.setData("POST not allowed for " + path);
        }
    }
    
    private void certificatesPost(HttpServletRequest req) {
        try {
            // 🔹 multipart/form-data
            FormParseResult res = formParseService.parse(req);

            Map<String, String> fields = res.getFields();
            FileItem file = res.getFiles().values().stream().findFirst().orElse(null);

            Certificate cert = new Certificate();

            cert.setName(fields.get("name"));
            cert.setAccreditationId(fields.get("accreditationId"));
            cert.setOrganizationUrl(fields.get("organizationUrl"));

            if (fields.get("issueDate") != null) {
                cert.setIssueDate(
                    java.sql.Date.valueOf(fields.get("issueDate"))
                );
            }

            if (fields.get("expiryDate") != null) {
                cert.setExpiryDate(
                    java.sql.Date.valueOf(fields.get("expiryDate"))
                );
            }

            // 🔹 PDF upload
            if (file != null) {
                cert.setDownloadRef(
                    storageService.save(file)
                );
            }

            String academyName = fields.get("academyName");

            dataAccessor.addCertificate(
                jwtToken.getPayload().getSub(),
                cert,
                academyName
            );

            restResponse.setData("Certificate added");
            restResponse.getMeta().setDataType("string");
        }
        catch (FormParseException ex) {
            restResponse.setStatus(RestStatus.status400);
            restResponse.setData(ex.getMessage());
        }
        catch (Exception ex) {
            restResponse.setStatus(RestStatus.status400);
            restResponse.setData(ex.getMessage());
        }
    }


    private void educationPost(HttpServletRequest req) {
        try {
            Education edu = gson.fromJson(req.getReader(), Education.class);

            dataAccessor.addEducation(
                jwtToken.getPayload().getSub(),
                edu
            );

            restResponse.setData("Education added");
            restResponse.getMeta().setDataType("string");
        }
        catch (Exception ex) {
            restResponse.setStatus(RestStatus.status400);
            restResponse.setData(ex.getMessage());
        }
    }



        private void experiencePost(HttpServletRequest req) {
         try {
             JsonObject body = gson.fromJson(req.getReader(), JsonObject.class);

             Experience exp = gson.fromJson(body, Experience.class);

            
             String companyName = body.get("companyName").getAsString();

             UUID companyId = dataAccessor.getOrCreateCompanyByName(
                 companyName,
                 jwtToken.getPayload().getSub()
             );

             exp.setCompanyId(companyId);

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
    private void skillsPost(HttpServletRequest req) {
        try {
            JsonObject body = gson.fromJson(req.getReader(), JsonObject.class);

            String name = body.get("name").getAsString();
            String level = body.has("level") ? body.get("level").getAsString() : null;
            boolean isMain = body.has("isMain") && body.get("isMain").getAsBoolean();

            dataAccessor.addSkill(
                jwtToken.getPayload().getSub(),
                name,
                level,
                isMain
            );

            restResponse.setData("Skill added");
            restResponse.getMeta().setDataType("string");
        }
        catch (Exception ex) {
            restResponse.setStatus(RestStatus.status400);
            restResponse.setData(ex.getMessage());
        }
    }



    // ======================== ENDPOINTS ========================

    private void profile() {
        try {
            String userId = jwtToken.getPayload().getSub();

            AuthCredential ua = dataAccessor.getUserAccess(
                userId,
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
                    .setProfileViews(
                        dataAccessor.getProfileViewsCount(userId)
                    )
                    .setPostViews(
                        dataAccessor.getPostViewsCount(userId)
                    )
            );
            restResponse.getMeta().setDataType("object");
        }
        catch (Exception ex) {
            restResponse.setStatus(RestStatus.status400);
            restResponse.setData(ex.getMessage());
        }
    }

    private void analytics() {
        try {
            String userId = jwtToken.getPayload().getSub();
            restResponse.setData(
                Map.of(
                    "profileViews",
                    dataAccessor.getProfileViewsCount(userId),
                    "postViews",
                    dataAccessor.getPostViewsCount(userId)
                )
            );
            restResponse.getMeta().setDataType("object");
        }
        catch (Exception ex) {
            restResponse.setStatus(RestStatus.status400);
            restResponse.setData(ex.getMessage());
        }
    }

    private void experienceGet() {
        restResponse.setData(
            dataAccessor.getUserExperienceBlocks(
                jwtToken.getPayload().getSub()
            )
        );
        restResponse.getMeta().setDataType("array");
    }

    private void educationGet() {
        restResponse.setData(
            dataAccessor.getUserEducations(
                jwtToken.getPayload().getSub()
            )
        );
        restResponse.getMeta().setDataType("array");
    }
    private void certificatesGet() {
        restResponse.setData(
            dataAccessor.getUserCertificates(
                jwtToken.getPayload().getSub()
            )
        );
        restResponse.getMeta().setDataType("array");
    }
    private void skillsGet() {
        restResponse.setData(
            dataAccessor.getUserSkills(
                jwtToken.getPayload().getSub()
            )
        );
        restResponse.getMeta().setDataType("array");
    }


    // ======================== AUTH ========================
    private void authenticate(HttpServletRequest req) throws ServletException, IOException {
        // Автентифікація за RFC 7617                         // Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
        String authHeader = req.getHeader("Authorization");   // Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
        if(authHeader == null || "".equals(authHeader)) {            
            this.restResponse.setStatus(RestStatus.status401);
            this.restResponse.setData("Missing 'Authorization' header");
            return;
        }
        String authScheme = "Basic ";
        if( ! authHeader.startsWith(authScheme) ) {
            this.restResponse.setStatus(RestStatus.status401);
            this.restResponse.setData("Invalid Authorization scheme. Must be " + authScheme);
            return;
        }
        String credentials = authHeader.substring( authScheme.length() );  // QWxhZGRpbjpvcGVuIHNlc2FtZQ==
        String userPass;
        try { 
            userPass = new String( 
                Base64.getDecoder().decode(credentials));   // Aladdin:open sesame
        }
        catch(IllegalArgumentException ex) {
            this.restResponse.setStatus(RestStatus.status401);
            this.restResponse.setData("Invalid credentials. Base64 decode error " + ex.getMessage());
            return;
        }
        String[] parts = userPass.split(":", 2);   // [0]Aladdin  [1]open sesame
        if(parts.length != 2) {
            this.restResponse.setStatus(RestStatus.status401);
            this.restResponse.setData("Invalid user-pass. Missing ':' ");
            return;
        }
        
        AuthCredential  ua = dataAccessor.getUserAccessByCredentials(parts[0], parts[1]);
        if(ua == null) {
            this.restResponse.setStatus(RestStatus.status401);
            this.restResponse.setData("Credentials rejected. Access denied");
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
        ));
        this.restResponse.setData( jwt.toString() );
    }

}
