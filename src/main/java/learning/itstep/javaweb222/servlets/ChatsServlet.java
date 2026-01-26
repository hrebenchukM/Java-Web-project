package learning.itstep.javaweb222.servlets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
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
public class ChatsServlet extends HttpServlet {

    private final DataAccessor dataAccessor;
    private final Gson gson = new GsonBuilder().serializeNulls().create();

    private RestResponse restResponse;
    private JwtToken jwtToken;

    @Inject
    public ChatsServlet(DataAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        restResponse = new RestResponse();
        restResponse.setMeta(
            new RestMeta()
                .setServiceName("API 'Chats'")
                .setManipulations(new String[]{"GET"})
                .setLinks(
                    Map.of("chats", "GET /chats")
                )
        );

        jwtToken = (JwtToken) req.getAttribute("JWT");

        if (jwtToken == null) {
            restResponse.setStatus(RestStatus.status401);
        }
        else {
            restResponse.setData(
                dataAccessor.getUserChatList(
                    jwtToken.getPayload().getSub()
                )
            );
            restResponse.getMeta().setDataType("array");
        }

        resp.setContentType("application/json; charset=utf-8");
        resp.getWriter().print(gson.toJson(restResponse));
    }
}
