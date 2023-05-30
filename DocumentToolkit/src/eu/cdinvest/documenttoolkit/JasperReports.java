package eu.cdinvest.documenttoolkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.AbstractXmlDataSource;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.data.JsonDataSource;

public class JasperReports {

	// Compile jrxml -> jasper
	// -----------------------
	public static JSON compileReport(JSON jsonIn) throws JRException, FileNotFoundException {

		JSON jsonOut = new JSON();

		// jrxml file name - required parameter
		String jrxml = jsonIn.getString("jrxml");
		if (!(new File(jrxml).exists())) {
			throw new FileNotFoundException();
		}

		// jasper file name - optional parameter
		String jasper = jsonIn.getString("jasper");
		if (jasper == null)
			jasper = jrxml.substring(0, jrxml.lastIndexOf(".")) + ".jasper";

		// process compilation
		try {
			JasperCompileManager.compileReportToFile(jrxml, jasper);
			jsonOut.setBoolean("success", true);
			jsonOut.setString("jrxml", jrxml);
			jsonOut.setString("jasper", jasper);
		} catch (JRException e) {
			throw (e);
		}

		return jsonOut;

	}

	// Generate pdf
	// ------------
	public static JSON generateReport(JSON jsonIn) throws JRException, FileNotFoundException {

		JSON jsonOut = new JSON();
		JsonDataSource jsonMainDataSource = null;
		AbstractXmlDataSource<JRXmlDataSource> xmlMainDataSource = null;

		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperPrint jasperPrint = null;

		// main report name / jasper file name - required parameter
		String jasper = jsonIn.getString("jasper");
		if (!(new File(jasper).exists())) {
			throw new FileNotFoundException();
		}

		// outfile / pdf file name - optional parameter
		String outfile = jsonIn.getString("outfile");
		if (outfile == null)
			outfile = jasper.substring(0, jasper.lastIndexOf(".")) + '_' + ZonedDateTime.now( ZoneOffset.of("+02:00") ).format( DateTimeFormatter.ofPattern( "yyyyMMdd_HHmmss" ) ) + ".pdf";

		// datasources for main and subreports - optional parameter
		if (jsonIn.getJSONObject().has("datasources")) {
			JSONArray datasources = jsonIn.getJSONObject().getJSONArray("datasources");

			String jsonMain = null;
			String xmlMain = null;
			String rootPropertyMain = null;

			for (int i = 0; i < datasources.length(); i++) {
				JSON ds = new JSON(datasources.getJSONObject(i));

				if (ds.getString("id").equals("MAIN")) {
					jsonMain = ds.getString("json");
					xmlMain = ds.getString("xml");
					rootPropertyMain = ds.getString("rootProperty");
				}
			}

			if (jsonMain != null && rootPropertyMain != null) {
				jsonMainDataSource = new JsonDataSource(new File(jsonMain), rootPropertyMain);

				for (int i = 0; i < datasources.length(); i++) {
					JSON ds = new JSON(datasources.getJSONObject(i));
					String idSubReport = ds.getString("id");
					String jsonSubReport = null;
					String rootPropertySubReport = null;

					if (!idSubReport.equals("MAIN") && idSubReport != null) {
						jsonSubReport = ds.getString("json");
						if (jsonSubReport == null)
							jsonSubReport = jsonMain;
						rootPropertySubReport = ds.getString("rootProperty");
					}

					if (idSubReport != null && jsonSubReport != null && rootPropertySubReport != null) {
						JsonDataSource jsonSubDataSource = new JsonDataSource(new File(jsonSubReport),
								rootPropertySubReport);
						parameters.put(ds.getString("id"), jsonSubDataSource);
					}
				}
			}
			
			if (xmlMain != null && rootPropertyMain != null) {
				xmlMainDataSource = new JRXmlDataSource(new File(xmlMain), rootPropertyMain);

				for (int i = 0; i < datasources.length(); i++) {
					JSON ds = new JSON(datasources.getJSONObject(i));
					String idSubReport = ds.getString("id");
					String xmlSubReport = null;
					String rootPropertySubReport = null;

					if (!idSubReport.equals("MAIN") && idSubReport != null) {
						xmlSubReport = ds.getString("xml");
						if (xmlSubReport == null)
							xmlSubReport = xmlMain;
						rootPropertySubReport = ds.getString("rootProperty");
					}

					if (idSubReport != null && xmlSubReport != null && rootPropertySubReport != null) {
						AbstractXmlDataSource<JRXmlDataSource> xmlSubDataSource = new JRXmlDataSource(new File(xmlSubReport),
								rootPropertySubReport);
						parameters.put(ds.getString("id"), xmlSubDataSource);
					}
				}
			}
		}

		// Process creation of report
		try {
			if (jsonMainDataSource != null) {
				jasperPrint = JasperFillManager.fillReport(jasper, parameters, jsonMainDataSource);
			} 
			else if (xmlMainDataSource != null) {
				jasperPrint = JasperFillManager.fillReport(jasper, parameters, xmlMainDataSource);
			} 
			else {
				jasperPrint = JasperFillManager.fillReport(jasper, parameters);
			}

			JasperExportManager.exportReportToPdfFile(jasperPrint, outfile);
			
			jsonOut.setBoolean("success", true);
			jsonOut.setString("jasper", jasper);
			jsonOut.setString("outfile", outfile);

		} catch (JRException e) {
			throw (e);
		}

		return jsonOut;
	}

}
