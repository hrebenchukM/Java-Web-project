package learning.itstep.javaweb222.filters;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class CorsFilter implements Filter {
    private final Logger logger;
    private FilterConfig filterConfig;

    @Inject
    public CorsFilter(Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

  @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        resp.setHeader(
            "Access-Control-Allow-Headers",
            "Authorization,Content-Type"
        );

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }


    @Override
    public void destroy() {
        this.filterConfig = null;
    }
    
}
/*
Фільтри (сервлетні фільтри) - реалізація концепції Middleware - 
ланцюга послідовного запуску програмних модулів за схемою 
"передай далі"
Включаються фільтри одним з трьох варіантів:
а) у файлі web.xml (див. зразок з GuiceFilter)
б) анотацією @WebFilter("/*")
в) системою IoC (див. ServletsConfig)
*/