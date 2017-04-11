package cz.vojtisek.smach.backend;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import javax.servlet.http.HttpServletRequest;

@Entity
public class ChargingSession {
    @Id
    public String id;
    public int wattCurrent;
    @Index
    public int wattTotal;
    public int ampCurrent;
    public int ampSet;
    @Index
    public boolean active;

    public static ChargingSession loadChargingSessionFromRequest(HttpServletRequest req)
            throws IllegalArgumentException {

        String chargingSessionId = req.getParameter("charging_session_id");

        if (chargingSessionId == null || chargingSessionId.isEmpty()) {

            throw new IllegalArgumentException("No charging_session_id provided.");
        } else {

            ChargingSession chargingSession = ObjectifyService.ofy().load().type(ChargingSession.class)
                    .id(chargingSessionId).now();
            if (chargingSession == null) {

                throw new IllegalArgumentException("No ChargingSession found for given charging_session_id.");
            } else {
                return chargingSession;
            }
        }
    }
}