package src.geolocation;

import com.google.gson.Gson;
import src.utils.HttpUtility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * GeoLocation class
 * - Used to get current location and retrieve info
 */
public class Geolocation {

    private Location location;

    public Geolocation() throws IOException{
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                whatismyip.openStream()));

        String ip = in.readLine(); //you get the IP as a String
        System.out.println(ip);
        String requestURL = "http://api.ipstack.com/"+ip+"?access_key=0877312de88a31b8453dea864015f4dd";
        try {
            HttpUtility.sendGetRequest(requestURL);
            String response = HttpUtility.readSingleLineRespone();
            //System.out.println(response);
            Gson gson = new Gson();
            this.location = gson.fromJson(response, Location.class);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        HttpUtility.disconnect();
    }

    /**
     * Gets current city
     * @return Current city
     */
    public String getCity() { return this.location.city; }
    /**
     * Gets current country
     * @return Current country
     */
    public String getCountry() {return this.location.country_name;}
    /**
     * Gets current region
     * @return Current region
     */
    public String getRegion() {return this.location.region_name;}



}
