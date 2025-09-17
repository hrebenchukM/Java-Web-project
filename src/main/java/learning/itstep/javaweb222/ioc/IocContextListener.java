package learning.itstep.javaweb222.ioc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 *
 * @author Lenovo
 */

public class IocContextListener extends GuiceServletContextListener {
 
    @Override
    protected Injector getInjector() {
        System.out.println("IocContextListener:getInjector");
        return Guice.createInjector(new ServicesConfig(),new ServletsConfig());
    }
}

/*
Context Listener - слухачі події утворення контексту - першого запуску
веб-застосунку сервером
Фільтр (middleware) - модулі, що передують розголуженню на сервлети,для задач IoC необхідно задати роботу Guice-Filter (web.xml)
*/