package com.varsha.geoTag;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

@SuppressWarnings("deprecation")
public class ReadMetaData {

	public static JSONObject getGeoTag(String strURL){
		URL u = getURL(strURL);
		InputStream in = getInputStream(u);
		Metadata metadata;
		boolean hasGeoDetails = false;
		JSONObject ret = new JSONObject();
		try {
			metadata = ImageMetadataReader.readMetadata(in);
			//			print(metadata);
			/*
			 conversion of coordinates into decimal 
			 */
			String lon = null, lat = null;
			for (Directory directory : metadata.getDirectories()) {

				for (Tag tag : directory.getTags()) {

					if (tag.getTagName().contains("GPS")) {
					}
					if(tag.getTagName().equals("GPS Latitude")) {
						lat = tag.getDescription();
					}
					else if(tag.getTagName().equals("GPS Longitude")) {
						lon = tag.getDescription();
					}
				}

				if (directory.hasErrors()) {
					for (String error : directory.getErrors()) {
						System.err.println("ERROR: " + error);
					}
				}
			}
			Map<String,Double> corMap = new HashMap<String,Double>();
			double decLon = 0, decLat = 0;
			
			if(lon == null || lat == null){
				hasGeoDetails = false;
				return ret;
			}
			if(hasGeoDetails){
			decLon = cordinatesToDecimal(lon);
			decLat = cordinatesToDecimal(lat);

			corMap.put("Longitude", decLon);
			corMap.put("Latitude", decLat);

			ret = getLocationInfo(decLat,decLon);
			return ret;

			}
		} catch (ImageProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	public static void main(String[] args) {

		URL u = getURL("https://www.drupal.org/files/IMG_4692.jpg");
		InputStream in = getInputStream(u);
		Metadata metadata;
//		System.out.println(getGeoTag("http://4.bp.blogspot.com/-JOqxgp-ZWe0/U3BtyEQlEiI/AAAAAAAAOfg/Doq6Q2MwIKA/s1600/google-logo-874x288.png"));
//		System.out.println(getGeoTag("http://stackoverflow.com/questions/26691771/java-catch-exception-empty-string"));
//		System.out.println(getGeoTag("https://pbs.twimg.com/profile_images/522909800191901697/FHCGSQg0.png"));
//		System.out.println(getGeoTag("https://support.ggle.com/webmasters/answer/2409439?hl=en"));
		boolean hasGeoDetails = true;
		try {
			metadata = ImageMetadataReader.readMetadata(in);
			//			print(metadata);

			/*
			 conversion of coordinates into decimal 
			 */
			String lon = null, lat = null;
			for (Directory directory : metadata.getDirectories()) {

				for (Tag tag : directory.getTags()) {

					if (tag.getTagName().contains("GPS")) {
						//						System.out.println(tag);
					}

					if(tag.getTagName().equals("GPS Latitude")) {
						//						System.out.println(tag.getDescription());
						lat = tag.getDescription();
					}
					else if(tag.getTagName().equals("GPS Longitude")) {
						//						System.out.println(tag.getDescription());
						lon = tag.getDescription();
					}
				}

				if (directory.hasErrors()) {
					for (String error : directory.getErrors()) {
						System.err.println("ERROR: " + error);
					}
				}
			}

			Map<String,Double> corMap = new HashMap<String,Double>();
			double decLon = 0, decLat = 0;
			JSONObject ret = new JSONObject();
			if(lon == null || lat == null){
				hasGeoDetails = false;
				//				return ret;
			}
			if(hasGeoDetails){
				decLon = cordinatesToDecimal(lon);
				decLat = cordinatesToDecimal(lat);

				corMap.put("Longitude", decLon);
				corMap.put("Latitude", decLat);
			}
			//			System.out.println(corMap);

			ret = getLocationInfo(decLat,decLon);
			//			JSONObject location;
			//			String location_string;
			//			System.out.println(ret);
			try {
				//Get JSON Array called "results" and then get the 0th complete object as JSON        
				//				location = ret.getJSONArray("results").getJSONObject(0); 
				// Get the value of the attribute whose name is "formatted_string"
				//			    location_string = location.getString("formatted_address");
				//			    System.out.println("test formattted address:" + location_string);
			} catch (JSONException e1) {
				e1.printStackTrace();

			}
		} catch (ImageProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static double cordinatesToDecimal(String cord){
		double decCord = 0;
		String[] tokens = cord.split(" ");
		double d = Double.parseDouble(tokens[0].substring(0,tokens[0].length()-1));
		double m = Double.parseDouble(tokens[1].substring(0,tokens[1].length()-1));
		double s = Double.parseDouble(tokens[2].substring(0,tokens[2].length()-1));
		boolean isNeg = false;

		if(d<0){
			isNeg = true;
			d = -d;
		}
		decCord = d + m/60 + s/3600;
		if(isNeg){
			decCord = -decCord;
		}
		//		System.out.println(decCord);
		return decCord;
	}
	private static URL getURL(String url) {
		URL u;
		try {
			u = new URL(url);
			return u;
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;

	}
	public static JSONObject getLocationInfo( double lat, double lng) {

		HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng="+lat+","+lng+"&sensor=false");
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();
		try {
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(stringBuilder.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		client.getConnectionManager().shutdown();
		return jsonObject;
	}

	private static InputStream getInputStream(URL url) {
		InputStream is;
		try {
			is = url.openStream();
			return is;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	private static void print(Metadata metadata) {
		System.out.println("-------------------------------------");

		for (Directory directory : metadata.getDirectories()) {

			for (Tag tag : directory.getTags()) {

				if (tag.getTagName().contains("GPS")) {
					System.out.println(tag);
				}

			}

			if (directory.hasErrors()) {
				for (String error : directory.getErrors()) {
					System.err.println("ERROR: " + error);
				}
			}
		}
	}
}