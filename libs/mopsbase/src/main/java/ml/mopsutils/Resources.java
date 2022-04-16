package ml.mopsutils;

import org.bukkit.plugin.Plugin;

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
	public InputStream getPluginsResourceAsStream(Plugin plg, String str) {
		return plg.getResource(str);
	}
	public InputStream getPluginsJSONResourceAsStream(Plugin plg, String str) {
		return plg.getResource("/json/" + str);
	}
}
