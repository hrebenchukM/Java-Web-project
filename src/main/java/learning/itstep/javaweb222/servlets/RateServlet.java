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
public class RateServlet extends HttpServlet {
    private final DataAccessor dataAccessor;
    private final Gson gson;
    private RestResponse restResponse;
    private JwtToken jwtToken;

    @Inject
    public RateServlet(DataAccessor dataAccessor, Gson gson) {
        this.dataAccessor = dataAccessor;
        this.gson = gson;
    }


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.restResponse = new RestResponse();
        restResponse.setMeta(
                new RestMeta()
                .setServiceName("Shop feedbacks API 'Rate'")
                .setCacheSeconds(1000)
                .setManipulations(new String[] {"GET", "POST"})
                .setLinks(Map.ofEntries(
                    Map.entry("create-feedback", "POST /rate?item-id={id}")
                ) )
        );
        jwtToken = (JwtToken) req.getAttribute("JWT");
        if(jwtToken == null) {
            this.restResponse.setStatus(RestStatus.status401);
        }
        else {
            super.service(req, resp); 
        } 
        
        resp.setContentType("application/json; charset=utf-8");
        resp.getWriter().print(
                gson.toJson(restResponse)
        );
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
   
    }
    
    
}
