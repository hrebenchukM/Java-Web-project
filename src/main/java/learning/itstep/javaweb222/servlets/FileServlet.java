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
import java.util.Arrays;
import java.util.List;
import learning.itstep.javaweb222.services.storage.StorageService;

/**
 * Завантажити файл, що є у сховищі
 */
@Singleton
public class FileServlet extends HttpServlet {
    private final StorageService storageService;
    private final List<String> allowedExtensions = Arrays.asList(
            "jpeg", "png", "bmp", "webp", "gif");

    @Inject
    public FileServlet(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getPathInfo() ;   // id - filename.ext
        int dotIndex = id.lastIndexOf(".");
        if(dotIndex < 0 || id.endsWith("/") || id.contains("../")) {
            resp.setStatus(404);
            resp.getWriter().print("File " + id + " not found");
            return;
        }
        
        String ext = id.substring(dotIndex + 1);
        if(ext.equals("jpg")) {
            ext = "jpeg";
        }
        if( allowedExtensions.contains(ext) ) {
            resp.setContentType("image/" + ext);
        }
        else {
            resp.setStatus(415);
            resp.getWriter().print("Type '" + ext + "' not supported");
            return;
        }
        try(InputStream rdr = storageService.getStream(id)) {            
            // piping - передача з потоку читання до потоку запису
            byte[] buf = new byte[8192];
            int len;
            OutputStream w = resp.getOutputStream();
            while( (len = rdr.read(buf)) > 0 ) {
                w.write(buf, 0, len);
            }
        }
        catch(IOException ex) {
            resp.setStatus(404);
            resp.getWriter().print("File " + id + " not found. " + ex.getMessage());
        }
    }
    
    
}