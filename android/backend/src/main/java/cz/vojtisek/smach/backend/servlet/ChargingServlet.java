package cz.vojtisek.smach.backend.servlet;

import com.google.appengine.repackaged.com.google.gson.JsonObject;
import com.googlecode.objectify.ObjectifyService;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.vojtisek.smach.backend.ChargingSession;

public class ChargingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        ChargingSession chargingSession = ChargingSession.loadChargingSessionFromRequest(req);

        JsonObject json = new JsonObject();
        json.addProperty("watt_current", chargingSession.wattCurrent);
        json.addProperty("watt_total", chargingSession.wattTotal);
        json.addProperty("amp_current", chargingSession.ampCurrent);
        json.addProperty("amp_set", chargingSession.ampSet);

        ObjectifyService.ofy().save().entity(chargingSession).now();

        out.print(json.toString());
    }
}