import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class ReadMetaData {

	public static void main(String[] args) {

		URL u = getURL("https://www.drupal.org/files/IMG_4692.jpg");
		System.out.println(u.toString());
		InputStream in = getInputStream(u);

		Metadata metadata;
		try {
			metadata = ImageMetadataReader.readMetadata(in);
			print(metadata);
			
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
						System.out.println(tag.getDescription());
						lat = tag.getDescription();
					}
					else if(tag.getTagName().equals("GPS Longitude")) {
						System.out.println(tag.getDescription());
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
			decLon = cordinatesToDecimal(lon);
			decLat = cordinatesToDecimal(lat);
			
			corMap.put("Longitude", decLon);
			corMap.put("Latitude", decLat);
			System.out.println(corMap);
			
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