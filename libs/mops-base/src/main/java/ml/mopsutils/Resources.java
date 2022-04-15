package ml.mopsutils;

import java.io.InputStream;
import java.net.URL;

public class Resources {
	public URL getResource(Object obj, String str) {
		return obj.getClass().getResource(str);
	}
	public URL getJSONResource(Object obj, String str) {
		return obj.getClass().getResource("/json/" + str);
	}
	public InputStream getResourceAsStream(Object obj, String str) {
		return obj.getClass().getResourceAsStream(str);
	}
	public InputStream getJSONResourceAsStream(Object obj, String str) {
		return obj.getClass().getResourceAsStream("/json/" + str);
	}
}
