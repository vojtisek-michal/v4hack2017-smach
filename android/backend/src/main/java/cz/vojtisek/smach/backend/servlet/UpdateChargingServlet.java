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

public class UpdateChargingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        ChargingSession chargingSession = ChargingSession.loadChargingSessionFromRequest(req);

        chargingSession.wattCurrent = Integer.valueOf(req.getParameter("watt_current"));
        chargingSession.wattTotal = Integer.valueOf(req.getParameter("watt_total"));
        chargingSession.ampCurrent = Integer.valueOf(req.getParameter("amp_current"));
        chargingSession.ampSet = Integer.valueOf(req.getParameter("amp_set"));
        chargingSession.active = true;
        ObjectifyService.ofy().save().entity(chargingSession).now();

        out.println(String.format(Locale.ENGLISH,
                "You gave me charging_session_id %s to update charging.", chargingSession.id));
    }
}