package learning.itstep.javaweb222.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import learning.itstep.javaweb222.data.DataAccessor;
import learning.itstep.javaweb222.services.Signature.SignatureService;
import learning.itstep.javaweb222.services.config.ConfigService;
import learning.itstep.javaweb222.services.kdf.KdfService;
import learning.itstep.javaweb222.services.timestamp.UnixTimestampService;

//@WebServlet("")   // сервлет для головної сторінки
@Singleton
public class HomeServlet extends HttpServlet {
private final KdfService kdfService;
private final UnixTimestampService unixTimestampService;
private final DataAccessor dateAccessor;
private final SignatureService signatureService;

@Inject
public HomeServlet(KdfService kdfService ,UnixTimestampService unixTimestampService,DataAccessor dateAccessor,SignatureService signatureService)
{
      this.kdfService=kdfService;
      this.unixTimestampService=unixTimestampService;
      this.dateAccessor = dateAccessor;
      this.signatureService =signatureService;
}
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("HomeServlet::doGet");  // вивід до out сервера (Apache)
        // атрибут, що буде у req протягом подальшої обробки (у т.ч. на JSP)
        

      req.setAttribute("HomeServlet",
              "Hello from HomeServlet "
              + kdfService.dk("123", "") 
              +"<br/>UUID:"
              + dateAccessor.getDbIdentity()
              +"<br/>"
           //   + (dateAccessor.install()?"Install ok":"Install error")
               +"<br/>"
            //  + (dateAccessor.seed()?"Seed ok":"Seed error")
            //  +"<br/>DB Time: "
             // + dateAccessor.getDbTime()
              + signatureService.getSignatureHex("123", "456")
              +"<br/>JWT:"
                      +req.getAttribute("JWT")
              + "<br/>JwtStatus: "
                      +req.getAttribute("JwtStatus")
              
      );
      
      req.setAttribute("Timestamp", unixTimestampService.getTimestamp());

        // return View()
        req.getRequestDispatcher("index.jsp").forward(req, resp);
        
        // return Json()
        // resp.setHeader("Content-Type", "application/json");
        // resp.getWriter().print("\"This is JSON string\"");
    }
    
}
/*
Servlet - узагальнена назва Java-класів, призначених для роботи з мережею.
У т.ч. з Інтернет, де грають роль контролерів типу АРІ
*/