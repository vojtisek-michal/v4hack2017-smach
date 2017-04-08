package cz.vojtisek.smach.backend;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class OpenSignalHelper {

    private static final String ONESIGNAL_URL = "https://onesignal.com/api/v1/notifications";

    private String apiKey;
    private String apiId;

    public OpenSignalHelper() throws IOException {
        Properties prop = new Properties();
        prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
        apiKey = prop.getProperty("ONESIGNAL_API_KEY");
        apiId = prop.getProperty("ONESIGNAL_APP_ID");
    }

    public String pushMessage(String extraData) throws IOException {

        URL obj = new URL(ONESIGNAL_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Host", "onesignal.com:443");
        con.setRequestProperty("Authorization", "Basic " + apiKey);

        String urlParameters = "app_id=" + apiId + "&isAndroid=true&android_background_data=true&included_segments[]=All" + extraData;
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}