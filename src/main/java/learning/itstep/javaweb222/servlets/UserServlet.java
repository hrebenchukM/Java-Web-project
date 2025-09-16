package learning.itstep.javaweb222.servlets;
 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
 
@WebServlet("/user")
public class UserServlet extends HttpServlet {
 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
     System.out.println("UserServlet::doGet");  // вивід до out сервера (Apache)
       // атрибут, що буде у req протягом подальшої обробки (у т.ч. на JSP)
        req.setAttribute("UserServlet", "Hello from UserServlet");
        
        // return View()
        req.getRequestDispatcher("user.jsp").forward(req, resp);
     
    }
}