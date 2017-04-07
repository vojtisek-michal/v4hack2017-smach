package cz.vojtisek.smach.backend;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StopChargingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        String chargingSessionId = req.getParameter("charging_session_id");

        if (chargingSessionId == null || chargingSessionId.isEmpty()) {

            resp.sendError(403, "No charging_session_id provided.");
        } else {

            out.println(String.format(Locale.ENGLISH,
                    "You gave me charging_session_id %s to start charging.", chargingSessionId));
        }
    }
}
