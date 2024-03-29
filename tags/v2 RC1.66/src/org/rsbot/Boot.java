package org.rsbot;

import java.io.IOException;
import java.net.URLDecoder;

public class Boot {

	public static void main(final String[] args) throws IOException {
		String location = Boot.class.getProtectionDomain().getCodeSource()
		.getLocation().getPath();
		location = URLDecoder.decode(location, "UTF-8").replaceAll("\\\\", "/");
		final String os = System.getProperty("os.name").toLowerCase();
		final String flags = "-Xmx512m -Dsun.java2d.d3d=false";

		if (os.contains("windows")) {
			Runtime.getRuntime().exec(
					"javaw " + flags + " -classpath \"" + location
					+ "\" org.rsbot.Application");
		} else if (os.contains("mac")) {
			Runtime.getRuntime().exec(
					new String[] {
							"/bin/sh",
							"-c",
							"java " + flags + " -Xdock:name=\"Infinity\""
							+ " -Xdock:icon=resources/images/icon.png"
							+ " -classpath \"" + location
							+ "\" org.rsbot.Application" });
		} else {
			Runtime.getRuntime().exec(
					new String[] {
							"/bin/sh",
							"-c",
							"java " + flags + " -classpath \"" + location
							+ "\" org.rsbot.Application" });
		}
	}
}
