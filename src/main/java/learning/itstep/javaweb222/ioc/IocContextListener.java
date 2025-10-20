package learning.itstep.javaweb222.ioc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class IocContextListener extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        System.out.println("IocContextListener::getInjector");
        return Guice.createInjector(
                new ServicesConfig(),
                new ServletsConfig()
        );
    }
    
}
/*
Context Listener - слухачі події утворення контексту - першого запуску
веб-застосунку сервером
Фільтри (middleware) - модулі, що передують розгалуженню на сервлети,
для задач ІоС необхідно задати роботу Guice Filter (web.xml)
*/