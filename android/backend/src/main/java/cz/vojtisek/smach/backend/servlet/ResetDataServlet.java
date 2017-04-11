package cz.vojtisek.smach.backend.servlet;

import com.google.appengine.repackaged.com.google.api.client.http.HttpStatusCodes;
import com.googlecode.objectify.cmd.Query;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.vojtisek.smach.backend.ChargingSession;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class ResetDataServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        if (!req.getParameter("pass").equals("smach")) {
            resp.sendError(HttpStatusCodes.STATUS_CODE_FORBIDDEN, "You shall not pass!");
        }
        if (Integer.parseInt(req.getParameter("kwh")) == 0) {
            resp.sendError(HttpStatusCodes.STATUS_CODE_FORBIDDEN, "You shall not pass!");
        }
        int level = Integer.parseInt(req.getParameter("kwh"));

        //ofy().delete().entities(ofy());
        Query<ChargingSession> sessions = ofy().load().type(ChargingSession.class);
        List<ChargingSession> toDelete = new ArrayList<>();
        for (ChargingSession session: sessions) {
            if (session.wattTotal >= level * 1000) {
                toDelete.add(session);
            }
        }
        ofy().delete().entities(toDelete).now();

        out.print("DONE");
    }
}