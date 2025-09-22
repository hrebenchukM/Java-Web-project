package learning.itstep.javaweb222.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import learning.itstep.javaweb222.data.DateAccessor;
import learning.itstep.javaweb222.services.config.ConfigService;
import learning.itstep.javaweb222.services.kdf.KdfService;
import learning.itstep.javaweb222.services.timestamp.UnixTimestampService;

//@WebServlet("")   // сервлет для головної сторінки
@Singleton
public class HomeServlet extends HttpServlet {
private final KdfService kdfService;
private final UnixTimestampService unixTimestampService;
private final DateAccessor dateAccessor;
@Inject
public HomeServlet(KdfService kdfService ,UnixTimestampService unixTimestampService,DateAccessor dateAccessor)
{
      this.kdfService=kdfService;
      this.unixTimestampService=unixTimestampService;
      this.dateAccessor = dateAccessor;
}
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("HomeServlet::doGet");  // вивід до out сервера (Apache)
        // атрибут, що буде у req протягом подальшої обробки (у т.ч. на JSP)
      req.setAttribute("HomeServlet",
              "Hello from HomeServlet "
              + kdfService.dk("123", "") 
              +"<br/>"
              + dateAccessor.getDbIdentity()
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