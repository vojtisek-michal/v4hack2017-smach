package cz.vojtisek.smach.backend.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.vojtisek.smach.backend.ChargingSession;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class StatsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        List<ChargingSession> list = ofy().load().type(ChargingSession.class).list();
        int totalWatt = 0;
        for (ChargingSession session: list) {
            totalWatt += session.wattTotal;
        }
        out.print(totalWatt);
    }
}