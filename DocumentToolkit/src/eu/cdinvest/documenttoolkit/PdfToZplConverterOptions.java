package eu.cdinvest.documenttoolkit;

public class PdfToZplConverterOptions {

	// Media Tracking
	// --------------
	// media being used and a value must be entered or the command is ignored
	// N = continuous media
	// Y = non-continuous media web sensing *
	// W = non-continuous media web sensing *
	// M = non-continuous media mark sensing
	String mediaTracking = "W";

	// Print Mode
	// ----------
	// T = Tear-off is default Value
	// P = Peel-off (not available on S-300)
	// R = Rewind (depends on printer model)
	// A = Applicator (depends on printer model)
	// C = Cutter (depends on printer model)
	// D = Delayed cutter
	String printMode = "T";

	// Override Pause
	// --------------
	// prepeel select, Y = true and N = false
	String overridePause = "N";

	int quantityOfLabels = 1;

	// Print Orientation
	// -----------------
	// N -> 0°, R -> 90°, I -> 180°, B -> 270°
	String printOrientation = "N";

	int pauseAndCutEveryNLabels = 0;
	
	// printerDpi, to optimize the printResult, default is 203
	int printerDpi = 203;	
	
	
	// Getters and setters
	// -------------------
	public String getMediaTracking() {
		return this.mediaTracking;
	}

	public void setMediaTracking(String mediaTracking) {
		if (mediaTracking != null && !mediaTracking.trim().isEmpty())
			this.mediaTracking = mediaTracking;
	}

	public String getPrintMode() {
		return this.printMode;
	}

	public void setPrintMode(String printMode) {
		if (printMode != null && !printMode.trim().isEmpty())
			this.printMode = printMode;
	}

	public String getOverridePause() {
		return this.overridePause;
	}

	public void setOverridePause(String overridePause) {
		if (overridePause != null && !overridePause.trim().isEmpty())
			this.overridePause = overridePause;
	}

	public int getQuantityOfLabels() {
		return this.quantityOfLabels;
	}

	public void setQuantityOfLabels(int quantityOfLabels) {
		if (quantityOfLabels > 0)
			this.quantityOfLabels = quantityOfLabels;
	}

	public String getPrintOrientation() {
		return this.printOrientation;
	}

	public void setPrintOrientation(String printOrientation) {
		if (printOrientation != null && !printOrientation.trim().isEmpty())
			this.printOrientation = printOrientation;

	}

	public int getPauseAndCutEveryNLabels() {
		return this.pauseAndCutEveryNLabels;
	}

	public void setPauseAndCutEveryNLabels(int pauseAndCutEveryNLabels) {
		if (pauseAndCutEveryNLabels > 0)
			this.pauseAndCutEveryNLabels = pauseAndCutEveryNLabels;
	}
	
	public int getPrinterDpi() {
		return printerDpi;
	}

	public void setPrinterDpi(int printerDpi) {
		if (printerDpi > 0)
		this.printerDpi = printerDpi;
	}

}
