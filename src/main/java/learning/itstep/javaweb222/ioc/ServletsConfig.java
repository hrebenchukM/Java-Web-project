package learning.itstep.javaweb222.ioc;

import com.google.inject.servlet.ServletModule;
import learning.itstep.javaweb222.filters.*;
import learning.itstep.javaweb222.servlets.*;

public class ServletsConfig extends ServletModule {

    @Override
    protected void configureServlets() {
        // Налаштування фільтрів
        filter("/*").through(CorsFilter.class);
        filter("/*").through(CharsetFilter.class);
        filter("/*").through(AuthFilter.class);
        
        // Налаштування сервлетів
        serve("/"       ).with(HomeServlet.class   );
        serve("/admin/*").with(AdminServlet.class  );
        serve("/file/*" ).with(FileServlet.class   );
        serve("/post" ).with(PostServlet.class );
        serve("/post/*" ).with(PostServlet.class );
        serve("/user"   ).with(UserServlet.class   );
        serve("/user/*" ).with(UserServlet.class   );
        serve("/vacancy"   ).with(VacancyServlet.class   );
        serve("/vacancy/*" ).with(VacancyServlet.class   );
    }
    
}
/*
Конфігурація сервлетів та фільтрів (middleware)
Замінює собою стандартну маршрутизацію (анотації @WebServlet, @WebFilter)
!! На всіх класах сервлетів та фільтрів зняти @Web-анотації, додати
!! анотацію @Singleton
*/