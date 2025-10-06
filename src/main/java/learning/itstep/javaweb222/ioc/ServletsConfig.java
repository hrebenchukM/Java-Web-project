
package learning.itstep.javaweb222.ioc;

import com.google.inject.servlet.ServletModule;
import learning.itstep.javaweb222.filters.*;
import learning.itstep.javaweb222.servlets.HomeServlet;
import learning.itstep.javaweb222.servlets.UserServlet;

/**
 *
 * @author Lenovo
 */
public class ServletsConfig extends ServletModule {
    @Override
    protected void configureServlets()
    {
        //налаштування фільтрів
        filter("/*").through(CorsFilter.class);
        
         filter("/*").through(AuthFilter.class);
        
         
        //налаштування сервлетів
        serve("/").with(HomeServlet.class);
        serve("/user").with(UserServlet.class);
        
    }
}

/*
Конфігурація сервлетів та фільтрів (middleware)
Замінює собою стандартну маршрутизацію (анотації @WebServlet,@WebFilter)
!! На всіх класах сервлетів та фільтрів зняти @Web-анотації,додати анотацію @Singleton
*/