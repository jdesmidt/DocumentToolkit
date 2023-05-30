package eu.cdinvest.documenttoolkit;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class DocumentToolkit {

	public static void main(String[] args) throws Exception {

		int port = 10900;

		try {

			if (args.length > 0) {
				port = Integer.parseInt(args[0]);
			}

			HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

			server.createContext("/", new Router());

			server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(5));
			server.start();
			
			System.out.println("DocumentToolkit is listening on port " + port );

		} catch (Exception e) {
			System.out.println("Failed to start server. Port may be in use.");
			return;
		}
	}

	static class Router implements HttpHandler {

		@Override
		public void handle(HttpExchange t) throws IOException {

			JSON jsonIn = null;
			JSON jsonOut = new JSON();
			
			PdfToZplConverter pz = new PdfToZplConverter();

			// Read http request
			// -----------------
			jsonIn = JSONInOut.getJsonIn(t);
			

			// Process report
			// --------------
			try {
				String uri = t.getRequestURI().toString().toLowerCase();
				
				System.out.println(uri);
				
				switch (uri) {
				case "/jasper/compilereport":
					jsonOut = JasperReports.compileReport(jsonIn);
					break;

				case "/jasper/generatepdf":
					jsonOut = JasperReports.generateReport(jsonIn);
					break;
					
					
				case "/pdftozpl":
					pz.setPdffile(jsonIn.getString("pdffile"));
					pz.setZplfile(jsonIn.getString("zplfile"));
					
 					pz.options.setMediaTracking(jsonIn.getString("mediaTracking"));
 					pz.options.setPrintMode(jsonIn.getString("printMode"));
 					pz.options.setOverridePause(jsonIn.getString("overridePause"));
 					pz.options.setPauseAndCutEveryNLabels(jsonIn.getInt("pauseAndCutEveryNLabels"));
 					pz.options.setPrintOrientation(jsonIn.getString("printOrientation"));
 					pz.options.setQuantityOfLabels(jsonIn.getInt("quantityOfLabels"));
 					pz.options.setPrinterDpi(jsonIn.getInt("printerDpi"));
 					pz.convertPdfToZpl();
 					
 					jsonOut.setBoolean("success", true);
 					jsonOut.setString("outfile", pz.getZplfile());
 					jsonOut.setInt("heightMM", pz.getPdfHeightInMm());
 					jsonOut.setInt("widthMM", pz.getPdfWidthInMm());
 					
 					break;
					
				}
			} catch (Exception e) {
				System.out.println(e);
				JSONInOut.sendErrorResponse(t, e);
				return;
			}

			// Send http response
			// ------------------
			JSONInOut.sendJsonOut(t, jsonOut);
			t.sendResponseHeaders(200, -1);

		}
	};

}
