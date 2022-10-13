package eu.cdinvest.documenttoolkit;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PdfToZplConverter {

	String pdffile = null;
	String zplfile = null;

	PdfToZplConverterOptions options = new PdfToZplConverterOptions();

	private String printerParmFileName = "PDF";

	private int pdfHeightInMm = 0;
	private int pdfWidthInMm = 0;

	private int blackLimit = 380;
	private int total;
	private int widthBytes;
	private boolean compressHex = true;

	private static Map<Integer, String> mapCode = new HashMap<Integer, String>();
	{
		mapCode.put(1, "G");
		mapCode.put(2, "H");
		mapCode.put(3, "I");
		mapCode.put(4, "J");
		mapCode.put(5, "K");
		mapCode.put(6, "L");
		mapCode.put(7, "M");
		mapCode.put(8, "N");
		mapCode.put(9, "O");
		mapCode.put(10, "P");
		mapCode.put(11, "Q");
		mapCode.put(12, "R");
		mapCode.put(13, "S");
		mapCode.put(14, "T");
		mapCode.put(15, "U");
		mapCode.put(16, "V");
		mapCode.put(17, "W");
		mapCode.put(18, "X");
		mapCode.put(19, "Y");
		mapCode.put(20, "g");
		mapCode.put(40, "h");
		mapCode.put(60, "i");
		mapCode.put(80, "j");
		mapCode.put(100, "k");
		mapCode.put(120, "l");
		mapCode.put(140, "m");
		mapCode.put(160, "n");
		mapCode.put(180, "o");
		mapCode.put(200, "p");
		mapCode.put(220, "q");
		mapCode.put(240, "r");
		mapCode.put(260, "s");
		mapCode.put(280, "t");
		mapCode.put(300, "u");
		mapCode.put(320, "v");
		mapCode.put(340, "w");
		mapCode.put(360, "x");
		mapCode.put(380, "y");
		mapCode.put(400, "z");
	}

	// Getter and setters
	// ------------------
	public void setPdffile(String pdffile) {
		this.pdffile = pdffile;

		if (this.zplfile == null || this.zplfile.equals("")) {
			this.zplfile = pdffile.substring(0, pdffile.lastIndexOf(".")) + ".zpl";
		}
		;

	};

	public String getPdffile() {
		return this.pdffile;
	};

	public void setZplfile(String zplfile) {
		if (zplfile != null && !zplfile.equals(""))
			this.zplfile = zplfile;
	};

	public String getZplfile() {
		return this.zplfile;
	};

	public int getPdfHeightInMm() {
		return this.pdfHeightInMm;
	}

	public int getPdfWidthInMm() {
		return this.pdfWidthInMm;
	}

	public void convertPdfToZpl() throws IOException {

		try {

			String zpl = "";

			// Convert first site from pdf to bufferedImage
			// --------------------------------------------
			if (this.pdffile.equals(""))
				throw new IOException();

			PDDocument document = PDDocument.load(new File(this.pdffile));
			PDPage firstPage = document.getPage(0);

			switch (this.options.getPrintOrientation()) {
			case "R": // R -> 90°
				firstPage.setRotation(firstPage.getRotation() + 90);
				break;
			case "I": // I -> 180°
				firstPage.setRotation(firstPage.getRotation() + 180);
				break;
			case "B": // B -> 270°
				firstPage.setRotation(firstPage.getRotation() + 270);
				break;
			}

			if (this.options.getPrintOrientation().equals("R") || this.options.getPrintOrientation().equals("B")) {
				this.pdfHeightInMm = (int) (document.getPage(0).getMediaBox().getHeight() * 25.4 / this.options.getPrinterDpi());
				this.pdfWidthInMm = (int) (document.getPage(0).getMediaBox().getWidth() * 25.4 / this.options.getPrinterDpi());
			} else {
				this.pdfWidthInMm = (int) (document.getPage(0).getMediaBox().getHeight() * 25.4 / this.options.getPrinterDpi());
				this.pdfHeightInMm = (int) (document.getPage(0).getMediaBox().getWidth() * 25.4 / this.options.getPrinterDpi());
			}

			PDFRenderer pdfRenderer = new PDFRenderer(document);
			BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 203, ImageType.RGB);

			// Convert bufferedImage to zpl
			// -----------------------------
			zpl = convertFromPngToZpl(bufferedImage);

			// Close document and bufferedImage
			// --------------------------------
			document.close();
			bufferedImage.flush();

			// Write zpl to file
			// -----------------
			BufferedWriter bw = new BufferedWriter(new FileWriter(this.zplfile));
			bw.write(zpl);
			bw.close();

		} catch (Exception e) {
			throw (e);
		}

	}

	/**
	 * convert from png to zpl...
	 * 
	 * @param image
	 * @return
	 * @throws IOException
	 */
	private String convertFromPngToZpl(BufferedImage image) throws IOException {
		String cuerpo = createBody(image);
		if (compressHex)
			cuerpo = encodeHexAscii(cuerpo);
		return addZplHeadDoc() + cuerpo + addZplLabelFormatDoc() + addZplFootDoc();
	}

	/**
	 * convert BufferedImage to binary
	 * 
	 * @param orginalImage
	 * @return
	 * @throws IOException
	 */
	private String createBody(BufferedImage orginalImage) throws IOException {
		StringBuffer sb = new StringBuffer();
		Graphics2D graphics = orginalImage.createGraphics();
		graphics.drawImage(orginalImage, 0, 0, null);
		int height = orginalImage.getHeight();
		int width = orginalImage.getWidth();
		int rgb, red, green, blue, index = 0;
		char auxBinaryChar[] = { '0', '0', '0', '0', '0', '0', '0', '0' };
		widthBytes = width / 8;
		if (width % 8 > 0) {
			widthBytes = (((int) (width / 8)) + 1);
		} else {
			widthBytes = width / 8;
		}
		this.total = widthBytes * height;
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				rgb = orginalImage.getRGB(w, h);
				red = (rgb >> 16) & 0x000000FF;
				green = (rgb >> 8) & 0x000000FF;
				blue = (rgb) & 0x000000FF;
				char auxChar = '1';
				int totalColor = red + green + blue;
				if (totalColor > blackLimit) {
					auxChar = '0';
				}
				auxBinaryChar[index] = auxChar;
				index++;
				if (index == 8 || w == (width - 1)) {
					sb.append(fourByteBinary(new String(auxBinaryChar)));
					auxBinaryChar = new char[] { '0', '0', '0', '0', '0', '0', '0', '0' };
					index = 0;
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * four byte binary
	 * 
	 * @param binaryStr
	 * @return
	 */
	private String fourByteBinary(String binaryStr) {
		int decimal = Integer.parseInt(binaryStr, 2);
		if (decimal > 15) {
			return Integer.toString(decimal, 16).toUpperCase();
		} else {
			return "0" + Integer.toString(decimal, 16).toUpperCase();
		}
	}

	/**
	 * encode binary to hex...
	 * 
	 * @param code
	 * @return
	 */
	private String encodeHexAscii(String code) {
		int maxlinea = widthBytes * 2;
		StringBuffer sbCode = new StringBuffer();
		StringBuffer sbLinea = new StringBuffer();
		String previousLine = null;
		int counter = 1;
		char aux = code.charAt(0);
		boolean firstChar = false;
		for (int i = 1; i < code.length(); i++) {
			if (firstChar) {
				aux = code.charAt(i);
				firstChar = false;
				continue;
			}
			if (code.charAt(i) == '\n') {
				if (counter >= maxlinea && aux == '0') {
					sbLinea.append(",");
				} else if (counter >= maxlinea && aux == 'F') {
					sbLinea.append("!");
				} else if (counter > 20) {
					int multi20 = (counter / 20) * 20;
					int resto20 = (counter % 20);
					sbLinea.append(mapCode.get(multi20));
					if (resto20 != 0) {
						sbLinea.append(mapCode.get(resto20) + aux);
					} else {
						sbLinea.append(aux);
					}
				} else {
					sbLinea.append(mapCode.get(counter) + aux);
					if (mapCode.get(counter) == null) {
					}
				}
				counter = 1;
				firstChar = true;
				if (sbLinea.toString().equals(previousLine)) {
					sbCode.append(":");
				} else {
					sbCode.append(sbLinea.toString());
				}
				previousLine = sbLinea.toString();
				sbLinea.setLength(0);
				continue;
			}
			if (aux == code.charAt(i)) {
				counter++;
			} else {
				if (counter > 20) {
					int multi20 = (counter / 20) * 20;
					int resto20 = (counter % 20);
					sbLinea.append(mapCode.get(multi20));
					if (resto20 != 0) {
						sbLinea.append(mapCode.get(resto20) + aux);
					} else {
						sbLinea.append(aux);
					}
				} else {
					sbLinea.append(mapCode.get(counter) + aux);
				}
				counter = 1;
				aux = code.charAt(i);
			}
		}
		return sbCode.toString();
	}

	/**
	 * add Zpl head doc.. // ~DG path, totalBytes, rowBytes, data // Uploads an
	 * embedded image, storing it at the specified file path. // Parameters: //
	 * // path: The file path to save the image to. The ^XG command can later be
	 * used to load the image from this path. The default value is
	 * R:UNKNOWN.GRF. // totalBytes: The total number of bytes in the image.
	 * Because each pixel in the image uses 1 bit, this value should be the
	 * total number of pixels in the image, divided by 8 (since there are 8 bits
	 * per byte). There is no default value. // rowBytes: The number of bytes
	 * per pixel row in the image. Because each pixel in the image uses 1 bit,
	 * this value should be the pixel width of the image, divided by 8 (since
	 * there are 8 bits per byte). The default value is 1, which is almost
	 * always incorrect. // data: The image data, in hexadecimal format. There
	 * is no default value. // // Example (common usage):
	 * ~DGR:IMAGE.GRF,999000,999,ABCDEF01234... // Example (full usage):
	 * ~DGR:IMAGE.GRF,999000,999,ABCDEF01234...
	 * 
	 * @return
	 */
	private String addZplHeadDoc() {
		String str = "~DGR:" + printerParmFileName + ".GRF," + total + "," + widthBytes + ", ";
		return str;
	}

	/**
	 * add Zpl label format doc...
	 * 
	 * @return
	 */
	private String addZplLabelFormatDoc() {

		String str = "" + "^XA" + // # Start Label Format
				"^MM" + this.options.getPrintMode() + // % print_mode
				// "^PO" + this.options.getPrintOrientation() + // %
				// print_orientation
				"^MN" + this.options.getMediaTracking() + // % media_tracking
				"^FO0,0" + // # Field Origin to 0,0
				"^XGR:" + printerParmFileName + ".GRF,1,1" + // % self.filename,
																// # Draw image
				"^FS" + // # Field Separator
				"^PQ" + this.options.getQuantityOfLabels() + // # Print Quantity
				"," + this.options.getPauseAndCutEveryNLabels() + // # Pause and
																	// cut every
																	// N labels
				",0," + this.options.getOverridePause() + // 'Y' if
															// override_pause
															// else 'N' # Don't
															// pause between
															// cuts
				"^XZ" + // # End Label Format
				"^XA^IDR:" + printerParmFileName + ".GRF"; // % self.filename #
															// Delete image from
															// printer

		return str;
	}

	/**
	 * add ZPL foot doc
	 * 
	 * @return
	 */
	private String addZplFootDoc() {
		String str = "^FS" + "^XZ";
		return str;
	}

}
