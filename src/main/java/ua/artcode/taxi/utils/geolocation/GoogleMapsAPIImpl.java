package ua.artcode.taxi.utils.geolocation;

import com.google.gson.*;
import com.maxmind.geoip.LookupService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ua.artcode.taxi.exception.InputDataWrongException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

public class GoogleMapsAPIImpl implements GoogleMapsAPI {

    public static final String GET_ADDRESS_FORMATTED_QUERY_TEMPLATE =
            "https://maps.googleapis.com/maps/api/geocode/json?address=%s,%s,%s&key=%s";

    public static final String GET_ADDRESS_UNFORMATTED_QUERY_TEMPLATE =
            "https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s";

    public static final String GET_DISTANCE_QUERY_TEMPLATE =
            "https://maps.googleapis.com/maps/api/directions/json?origin=place_id:%s&destination=place_id:%s&key=%s";

    public static final String GET_LOCATION_QUERY_TEMPLATE =
            "https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s";

    public static final String GOOGLE_API_KEY =
            "AIzaSyD-KmQUcMJUpRzjthK1CvNtmYw3mLf9vzs";

    public static final String DATA_IP_FILE =
            "/home/jessy/IdeaProjects/_DataForProjects/GeoLiteCity.dat";

    public static final String URL_FOR_SEARCH_CURRENT_IP =
            "http://checkip.amazonaws.com";



    @Override
    public Location findLocation(String unformatted) throws InputDataWrongException {
        final String preparedQuery = String.format(GET_ADDRESS_UNFORMATTED_QUERY_TEMPLATE,
                prepareQueryReplaceSpaces(unformatted), GOOGLE_API_KEY);

        return getLocationFromURL(preparedQuery);
    }

    private String prepareQueryReplaceSpaces(String unformatted) {
        return unformatted.replaceAll(" ", "+");
    }

    @Override//todo finish all task
    public Location findLocation(String country, String city, String street, String houseNum)
                                                                            throws InputDataWrongException {
        final String preparedQuery = String.format(GET_ADDRESS_FORMATTED_QUERY_TEMPLATE,
                prepareQueryReplaceSpaces((houseNum + " " + street)),
                prepareQueryReplaceSpaces(city),
                country,
                GOOGLE_API_KEY);

        return getLocationFromURL(preparedQuery);
    }

    @Override
    public Location findPlaceID(Location location) throws InputDataWrongException {
        final String preparedQuery = String.format(GET_LOCATION_QUERY_TEMPLATE,
                location.getLat(), location.getLon(), GOOGLE_API_KEY);

        return getLocationFromURL(preparedQuery);
    }

    private Location getLocationFromURL(String preparedQuery) throws InputDataWrongException {
        try {
            String jsonResponse = sendGetRequest(preparedQuery);
            return convert(jsonResponse);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            throw new InputDataWrongException("Wrong calculation in Google API");
        }
    }

    private Location convert(String jsonResponse) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject root = (JSONObject) jsonParser.parse(jsonResponse);

        JSONArray result = (JSONArray) root.get("results");
        JSONObject firstFound = (JSONObject) result.get(0);


        JSONObject geometry = (JSONObject) firstFound.get("geometry");
        String placeId = firstFound.get("place_id").toString();

        JSONObject location = (JSONObject) geometry.get("location");

        String addressFormattedName = firstFound.get("formatted_address").toString();
        String lat = location.get("lat").toString();
        String lng = location.get("lng").toString();

        return new Location(addressFormattedName,
                Double.parseDouble(lat), Double.parseDouble(lng), placeId);
    }

    private String sendGetRequest(String preparedQuery) throws IOException, InputDataWrongException {
        URL obj = new URL(preparedQuery);
        String res = "";
        try {
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            // optional default is GET
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            res = getStringContent(con.getInputStream());

        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new InputDataWrongException("Wrong calculation in Google API");
        }

        return res;
    }

    private String getStringContent(InputStream is) throws IOException {
        Scanner sc = new Scanner(is);
        StringBuilder sb = new StringBuilder();
        while (sc.hasNextLine()) {
            sb.append(sc.nextLine()).append("\n");
        }

        return sb.toString();
    }

    @Override
    public double getDistance(Location pointA, Location pointB) throws InputDataWrongException {
        final String formattedQuery = String.format(GET_DISTANCE_QUERY_TEMPLATE,
                pointA.getPlaceId(), pointB.getPlaceId(), GOOGLE_API_KEY);

        try {
            //todo use gsoup library for better mapping
            String body = sendGetRequest(formattedQuery);

            JsonParser jsonParser = new JsonParser();
            JsonElement element = jsonParser.parse(body);

            JsonObject asJsonObject = element.getAsJsonObject();
            JsonArray routes = asJsonObject.getAsJsonArray("routes");
            JsonObject asJsonObject1 = routes.get(0).getAsJsonObject();
            JsonArray legs = asJsonObject1.getAsJsonArray("legs");
            JsonObject asJsonObject2 = legs.get(0).getAsJsonObject();
            JsonPrimitive jsonObject = asJsonObject2.getAsJsonObject("distance").getAsJsonPrimitive("value");
            //System.out.println(jsonObject.toString());

            return Double.parseDouble(jsonObject.getAsString());
            //System.out.println(body);
        } catch (IOException e) {
            e.printStackTrace();
            throw new InputDataWrongException("Wrong calculation in Google API");
        }
    }

    public Location getCurrentLocation() throws IOException, InputDataWrongException {

        URL whatismyip = new URL(URL_FOR_SEARCH_CURRENT_IP);
        BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));

        String ip = in.readLine();

        LookupService cl = new LookupService(DATA_IP_FILE,
                LookupService.GEOIP_MEMORY_CACHE | LookupService.GEOIP_CHECK_CACHE);

        com.maxmind.geoip.Location geoLocation = cl.getLocation(ip);

        Float lat = geoLocation.latitude;
        Float lng = geoLocation.longitude;

        return findPlaceID(new Location(lat, lng));
    }
}