package learning.itstep.javaweb222.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import learning.itstep.javaweb222.data.DataAccessor;
import learning.itstep.javaweb222.data.dto.Rate;
import learning.itstep.javaweb222.data.jwt.JwtToken;
import learning.itstep.javaweb222.models.rate.RateFormModel;
import learning.itstep.javaweb222.rest.RestMeta;
import learning.itstep.javaweb222.rest.RestPagination;
import learning.itstep.javaweb222.rest.RestResponse;
import learning.itstep.javaweb222.rest.RestStatus;

@Singleton
public class RateServlet extends HttpServlet {
    private final Logger logger;
    private final DataAccessor dataAccessor;
    private final Gson gson;
    private RestResponse restResponse;
    private JwtToken jwtToken;
    
    @Inject
    public RateServlet(DataAccessor dataAccessor, Gson gson, Logger logger) {
        this.dataAccessor = dataAccessor;
        this.gson = gson;
        this.logger = logger;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.restResponse = new RestResponse();
        restResponse.setMeta(
                new RestMeta()
                .setServiceName("Shop feedbacks API 'Rate':"+req.getMethod())
                .setCacheSeconds(1000)
                .setManipulations(new String[] {"GET", "POST"})
                .setLinks( Map.ofEntries(
               Map.entry("create-feedback", "POST /rate?item-id={id}"),
               // Приклад відхилення від одноманітного інтерфейсу
               // У POST передається ?item-id={id}, а у GET /{id}
               Map.entry("get-feedbacks", "GET /rate/{id}")
       ) )

        );
        jwtToken = (JwtToken) req.getAttribute("JWT");
        if(jwtToken == null && !req.getMethod().equals("GET")) {
            this.restResponse.setStatus( RestStatus.status401 );
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
        /* Одержати JSON з тіла запиту,
           провести валідацію, передати управління на dataAccessor
            body: JSON.stringify({
                ciId,
                productId,
                rate,
                comment
            })
           */
        try( InputStream rs = req.getInputStream();
             InputStreamReader rsr = new InputStreamReader(rs)
        ) {
            // JsonElement reqBody = gson.fromJson(rsr, JsonElement.class);
            // this.restResponse.setData(reqBody);
            RateFormModel rateFormModel = gson.fromJson(rsr, RateFormModel.class);
            rateFormModel.setUserId( jwtToken.getPayload().getSub() );
            dataAccessor.addRate( rateFormModel );
            this.restResponse.getMeta().setDataType("null");
        }
        catch( Exception ex ) {
            logger.log(Level.WARNING, "RateServlet::doPost {0}", ex.getMessage() );
            this.restResponse.setStatus( RestStatus.status400 );
            this.restResponse.setData( ex.getMessage() );
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo().substring(1);
        
        int totalItems = dataAccessor.getRatesCountById(path);

        try {
            RestPagination restPagination = RestPagination.fromRequest(
                    req,
                    totalItems,
                    2
            );
            this.restResponse.getMeta().setPagination(restPagination);
            if(totalItems == 0) {
                this.restResponse.setData(new Rate[0]);
            }
            else {
                this.restResponse.setData( dataAccessor.getRates(path,restPagination) );
            }

        }
        catch(Exception ex) {
            this.restResponse.setStatus( RestStatus.status400 );
            this.restResponse.setData( ex.getMessage() );
        }


    }
    
    
}
