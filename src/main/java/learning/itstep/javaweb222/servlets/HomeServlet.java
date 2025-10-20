package learning.itstep.javaweb222.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import learning.itstep.javaweb222.data.DataAccessor;
import learning.itstep.javaweb222.services.Signature.SignatureService;
import learning.itstep.javaweb222.services.kdf.KdfService;

// @WebServlet("")   // сервлет для головної сторінки -- 
@Singleton           // анотацію замінено з введенням ІоС
public class HomeServlet extends HttpServlet {
    private final KdfService kdfService;
    private final DataAccessor dataAccessor;
    private final SignatureService signatureService;

    @Inject
    public HomeServlet(KdfService kdfService, DataAccessor dataAccessor,SignatureService signatureService ) {
        this.kdfService = kdfService;
        this.dataAccessor = dataAccessor;
        this.signatureService = signatureService;
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("HomeServlet::doGet");  // вивід до out сервера (Apache)
        // атрибут, що буде у req протягом подальшої обробки (у т.ч. на JSP)
        req.setAttribute("HomeServlet", 
                "Hello from HomeServlet " 
                + kdfService.dk("123", "")
                        + "<br/>"
                + (dataAccessor.install() ? "Install OK" : "Install error" )
                        + "<br/>"
                // + (dataAccessor.seed() ? "Seed OK" : "Seed error" )
                + signatureService.getSignatureHex("123", "456") 
                + "<br/>JWT: " + req.getAttribute("JWT")
                + "<br/>JwtStatus: " + req.getAttribute("JwtStatus")
        );
        // задача: вивести значення атрибутів запиту а) "JWT" б) "JwtStatus"
        
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