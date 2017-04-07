package cz.vojtisek.smach.backend;

import com.googlecode.objectify.ObjectifyService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class OfyHelper implements ServletContextListener {
    public void contextInitialized(ServletContextEvent event) {
        ObjectifyService.register(ChargingSession.class);
    }

    public void contextDestroyed(ServletContextEvent event) {
        // App Engine does not currently invoke this method.
    }
}