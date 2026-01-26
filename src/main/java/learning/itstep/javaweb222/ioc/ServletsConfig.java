package learning.itstep.javaweb222.ioc;

import com.google.inject.servlet.ServletModule;
import learning.itstep.javaweb222.filters.*;
import learning.itstep.javaweb222.servlets.*;
import learning.itstep.javaweb222.servlets.stream.ChatStreamServlet;

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
        serve("/notifications"   ).with(NotificationsServlet.class);
        serve("/notifications/*" ).with(NotificationsServlet.class);
        serve("/network"   ).with(NetworkServlet.class);
        serve("/network/*" ).with(NetworkServlet.class);
        serve("/contacts").with(ContactsServlet.class);
        serve("/contacts/*").with(ContactsServlet.class);
        serve("/groups").with(GroupsServlet.class);
        serve("/groups/*").with(GroupsServlet.class);
        serve("/pages").with(PagesServlet.class);
        serve("/pages/*").with(PagesServlet.class);
        serve("/events").with(EventsServlet.class);
        serve("/events/*").with(EventsServlet.class);
        serve("/portfolio").with(PortfolioServlet.class);
        serve("/portfolio/*").with(PortfolioServlet.class);
        serve("/chat/stream").with(ChatStreamServlet.class);
        serve("/chats").with(ChatsServlet.class);
        serve("/chats/*").with(ChatsServlet.class);
        serve("/messages").with(MessageServlet.class);
        serve("/messages/*").with(MessageServlet.class);
}
    
}
/*
Конфігурація сервлетів та фільтрів (middleware)
Замінює собою стандартну маршрутизацію (анотації @WebServlet, @WebFilter)
!! На всіх класах сервлетів та фільтрів зняти @Web-анотації, додати
!! анотацію @Singleton
*/