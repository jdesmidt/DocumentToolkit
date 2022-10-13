package eu.cdinvest.documenttoolkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;

public class JSONInOut {
	

	public static JSON getJsonIn(HttpExchange t) {
		
		// Get body from request
		// ---------------------
		InputStream body = t.getRequestBody();

		// Convert body to string
		// ----------------------
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;

		try {
			if (body != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(body));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("{}");
			}
		} catch (IOException e) {
			stringBuilder.append("{}");
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ex) {
				}
			}
		};

		// Convert string to JSON
		// ----------------------
		JSON jsonIn = new JSON(stringBuilder.toString());
		
		return jsonIn;

	};

	public static void sendJsonOut(HttpExchange t, JSON jsonOut) {
		try {
			String response = jsonOut.toString();
			t.getResponseHeaders().set("Content-Type", "application/json");
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} catch (IOException e) {}
		
	};

	public static void sendErrorResponse(HttpExchange t, Exception e) {

		try {
			JSON jsonOut = new JSON();
			jsonOut.setBoolean("success", false);
			jsonOut.setString("error", e.toString());

			String response = jsonOut.toString();

			t.getResponseHeaders().set("Content-Type", "application/json");
			t.sendResponseHeaders(500, response.length());

			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} catch (IOException ex) {
			System.out.println("Error while sending error");
		}

	};

}
