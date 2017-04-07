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

public class AmpServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ChargingSession chargingSession = ChargingSession.loadChargingSessionFromRequest(req);
        PrintWriter out = resp.getWriter();
        out.println(chargingSession.ampSet);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ChargingSession chargingSession = ChargingSession.loadChargingSessionFromRequest(req);
        chargingSession.ampSet = Integer.valueOf(req.getParameter("amp"));
        ObjectifyService.ofy().save().entity(chargingSession).now();

        PrintWriter out = resp.getWriter();
        out.println(String.format(Locale.ENGLISH,
                "You gave me charging_session_id %s to set amp_current to %d.", chargingSession.id, chargingSession.ampSet));
    }
}