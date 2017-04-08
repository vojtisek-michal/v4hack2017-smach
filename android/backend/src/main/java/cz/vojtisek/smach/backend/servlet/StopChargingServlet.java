package cz.vojtisek.smach.backend.servlet;

import com.googlecode.objectify.ObjectifyService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.vojtisek.smach.backend.ChargingSession;

public class StopChargingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        ChargingSession chargingSession = ChargingSession.loadChargingSessionFromRequest(req);

        chargingSession.active = false;
        ObjectifyService.ofy().save().entity(chargingSession).now();

        OpenSignalHelper openSignalHelper = new OpenSignalHelper();
        String response = openSignalHelper.pushMessage("&data[event]=charging_end&data[charging_session_id]=" + chargingSession.id);
        log("postToOpenSignal response = " + response);

        out.println(String.format(Locale.ENGLISH,
                "You gave me charging_session_id %s to stop charging.", chargingSession.id));
    }
}