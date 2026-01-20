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
    
    private static boolean installed = false;

      @Override
      protected void doGet(HttpServletRequest req, HttpServletResponse resp)
              throws ServletException, IOException {

          String installResult = "";

         if (!installed) {
            boolean ok =
                dataAccessor.install() &&
                dataAccessor.seed();

            installResult = ok ? "Install + Seed OK" : "Install/Seed error";
            installed = true;
        }


          req.setAttribute("HomeServlet",
                  "Hello from HomeServlet<br/>" +
                  installResult
          );

          req.getRequestDispatcher("index.jsp").forward(req, resp);
      }


    
}
/*
Servlet - узагальнена назва Java-класів, призначених для роботи з мережею.
У т.ч. з Інтернет, де грають роль контролерів типу АРІ
*/