package org.json;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * Makes reading/writing JSON from/to file easy
 * 
 * @author Waterwolf
 */
public class JSONFileHandler {
	public static JSONObject readFile(final File f) throws IOException, JSONException {
		if (!f.exists())
			throw new FileNotFoundException("File not found");

		final BufferedReader br = new BufferedReader(new FileReader(f));
		String line;

		String wholeInput = "";

		while ((line = br.readLine()) != null)
			wholeInput += line;

		br.close();

		return new JSONObject(wholeInput);
	}

	public static void saveFile(final File f, final JSONObject obj) throws IOException,
	JSONException {
		saveFile(f, obj, true);
	}

	public static void saveFile(final File f, final JSONObject obj, final boolean indent)
	throws IOException, JSONException {
		saveFile(f, obj, (indent ? 3 : 0));
	}

	public static void saveFile(final File f, final JSONObject obj, final int indentFactor)
	throws IOException, JSONException {
		if (f.getParentFile() != null && !f.getParentFile().exists()) // check
			// if
			// folder
			// exists
			f.getParentFile().mkdirs(); // if not, make folder

		if (!f.exists()) // check if file exists
			f.createNewFile(); // if not, create file

		final BufferedWriter bw = new BufferedWriter(new FileWriter(f)); // open
		// bufferedwriter

		if (indentFactor > 0)
			bw.write(obj.toString(indentFactor)); // write JSONObject to file
		// with indentfactor "3"
		else
			bw.write(obj.toString());

		bw.close(); // close bufferedwriter
	}
}
