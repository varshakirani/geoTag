
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.iptc.IptcReader;



// TODO Auto-generated method stub


/**
 * Showcases the most popular ways of using the metadata-extractor library.
 * <p/>
 * For more information, see the project wiki: https://github.com/drewnoakes/metadata-extractor/wiki/GettingStarted
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class ReadMetaData {

	/**
	 * Executes the sample usage program.
	 *
	 * @param args command line parameters
	 */

	public static void main(String[] args)
	{

		URL u = getURL("https://www.drupal.org/files/IMG_4692.jpg");
		System.out.println(u.toString());
		InputStream in = getInputStream(u);	



		//File file = new File("/home/varsha/workspace/GeoTag/cycling.jpeg");

		// There are multiple ways to get a Metadata object for a file

		//
		// SCENARIO 1: UNKNOWN FILE TYPE
		//
		// This is the most generic approach.  It will transparently determine the file type and invoke the appropriate
		// readers.  In most cases, this is the most appropriate usage.  This will handle JPEG, TIFF, GIF, BMP and RAW
		// (CRW/CR2/NEF/RW2/ORF) files and extract whatever metadata is available and understood.
		//
		try {
			//Metadata metadata = ImageMetadataReader.readMetadata(file);
			Metadata metadata = ImageMetadataReader.readMetadata(in);
			print(metadata);
		} catch (ImageProcessingException e) {
			// handle exception
		} catch (IOException e) {
			// handle exception
		}

		//
		// SCENARIO 2: SPECIFIC FILE TYPE
		//
		// If you know the file to be a JPEG, you may invoke the JpegMetadataReader, rather than the generic reader
		// used in approach 1.  Similarly, if you knew the file to be a TIFF/RAW image you might use TiffMetadataReader,
		// PngMetadataReader for PNG files, BmpMetadataReader for BMP files, or GifMetadataReader for GIF files.
		//
		// Using the specific reader offers a very, very slight performance improvement.
		//
		try {
			Metadata metadata = JpegMetadataReader.readMetadata(in);

			//print(metadata);
		} catch (JpegProcessingException e) {
			// handle exception
		} catch (IOException e) {
			// handle exception
		}

		//
		// APPROACH 3: SPECIFIC METADATA TYPE
		//
		// If you only wish to read a subset of the supported metadata types, you can do this by
		// passing the set of readers to use.
		//
		// This currently only applies to JPEG file processing.
		//
		try {
			// We are only interested in handling
			Iterable<JpegSegmentMetadataReader> readers = Arrays.asList(new ExifReader(), new IptcReader());

			Metadata metadata = JpegMetadataReader.readMetadata(in, readers);

			//print(metadata);
		} catch (JpegProcessingException e) {
			// handle exception
		} catch (IOException e) {
			// handle exception
		}
	}

	private static URL getURL(String url){
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

	private static InputStream getInputStream(URL url){
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
	private static void print(Metadata metadata)
	{
		System.out.println("-------------------------------------");

		// Iterate over the data and print to System.out

		//
		// A Metadata object contains multiple Directory objects
		//
		for (Directory directory : metadata.getDirectories()) {

			//
			// Each Directory stores values in Tag objects
			//
			for (Tag tag : directory.getTags()) {

				if(tag.getTagName().contains("GPS")){
					System.out.println(tag);
				}

			}

			//
			// Each Directory may also contain error messages
			//
			if (directory.hasErrors()) {
				for (String error : directory.getErrors()) {
					System.err.println("ERROR: " + error);
				}
			}
		}
	}
}
