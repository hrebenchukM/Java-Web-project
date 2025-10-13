
package learning.itstep.javaweb222.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import learning.itstep.javaweb222.services.storage.StorageService;

@Singleton
public class FileServlet extends HttpServlet {
    private final StorageService storageService;

    @Inject
    public FileServlet(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
     String id = req.getPathInfo() ;
    
     try(InputStream rdr = storageService.getStream(id)){
     resp.setContentType("image/jpeg");
     //piping
     byte[] buf = new byte[8192];
     int len;
     OutputStream w = resp.getOutputStream();
       while((len = rdr.read(buf))>0)
       {
         w.write(buf,0,len);
       }
     }
     catch(IOException ex){
          resp.getWriter().print(ex.getMessage());
     }
    }
    
}
