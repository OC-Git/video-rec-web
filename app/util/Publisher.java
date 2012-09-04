package util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import play.Logger;

import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.media.MediaFileSource;
import com.google.gdata.data.media.mediarss.MediaCategory;
import com.google.gdata.data.media.mediarss.MediaDescription;
import com.google.gdata.data.media.mediarss.MediaKeywords;
import com.google.gdata.data.media.mediarss.MediaTitle;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.data.youtube.YouTubeMediaGroup;
import com.google.gdata.data.youtube.YouTubeNamespace;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.InvalidEntryException;
import com.google.gdata.util.ServiceException;

public class Publisher {

	public static String publish(File file, String title, String category,
			String description, String accessToken) {
		if (accessToken == null)
			return null;
		Logger.info("Publishing to Youtube: " + file.getAbsolutePath());
		String clientID = "lean-video-recording";
		String developer_key = "AI39si642bz59kjhdF7sZOLDlCL2BjQT8_c9mtdBLRhvv8CB4KP8TApMj7Q94AOYhIBS5jfLENomZ0fuOywEKzrBk3Aqw2bdDQ";
		YouTubeService service = new YouTubeService(clientID, developer_key);
		service.setAuthSubToken(accessToken);

		VideoEntry newEntry = new VideoEntry();

		YouTubeMediaGroup mg = newEntry.getOrCreateMediaGroup();
		mg.setTitle(new MediaTitle());
		mg.getTitle().setPlainTextContent(title);
		mg.addCategory(new MediaCategory(YouTubeNamespace.CATEGORY_SCHEME,
				"People"));
		mg.setKeywords(new MediaKeywords());
		mg.getKeywords().addKeyword(category);
		mg.setDescription(new MediaDescription());
		mg.getDescription().setHtmlContent(description);
		mg.setPrivate(false);
		mg.addCategory(new MediaCategory(YouTubeNamespace.DEVELOPER_TAG_SCHEME,
				category));

		// newEntry.setGeoCoordinates(new GeoRssWhere(37.0, -122.0));
		// alternatively, one could specify just a descriptive string
		// newEntry.setLocation("Mountain View, CA");

		MediaFileSource ms = new MediaFileSource(file, "video/quicktime");
		newEntry.setMediaSource(ms);

		String uploadUrl = "http://uploads.gdata.youtube.com/feeds/api/users/default/uploads";
		try {
			VideoEntry createdEntry = service.insert(new URL(uploadUrl),
					newEntry);
			// tag:youtube.com,2008:video:9yCGrfG9vRk
			String id = createdEntry.getId();
			return id.substring(id.lastIndexOf(':') + 1);
		} catch (ServiceException se) {
			Logger.error(
					"ServiceException: " + se.getExtendedHelp() + " "
							+ se.getInternalReason() + " " + se.getDebugInfo(),
					se);
			return null;
		} catch (Exception e) {
			Logger.error("Youtube publish", e);
			return null;
		}
	}
}
