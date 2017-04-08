package cz.vojtisek.smach.backend.servlet;

import com.googlecode.objectify.ObjectifyService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.vojtisek.smach.backend.ChargingSession;

public class StartChargingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();

        ChargingSession chargingSession = new ChargingSession();
        chargingSession.id = getChargingSessionId();
        chargingSession.active = true;
        ObjectifyService.ofy().save().entity(chargingSession).now();

        OpenSignalHelper openSignalHelper = new OpenSignalHelper();
        String response = openSignalHelper.pushMessage("&data[event]=charging_start&data[charging_session_id]=" + chargingSession.id);
        log("postToOpenSignal response = " + response);

        out.print(chargingSession.id);
    }

    private String getChargingSessionId() {
        return UUID.randomUUID().toString();
    }
}