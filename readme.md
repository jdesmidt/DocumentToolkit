# Document Toolkit
This http server handles requests for :
- Compiling Jasper Reports (jrxml -> jasper)
- Creating Jasper Reports based on json file(s)
- Converting PDF to ZPL files

## JasperReports
Supported version : JasperReports 6.16

 
### Compiling Jasper Reports (jrxml -> jasper)

Compiling a report can be done by sending a request to **/jasper/compilereport**.


The body of the requests is a json containing the following keys:
- jrxml (required) : location of the jrxml file
- jasper (optional) : path to the file to be created, if not provided, the name will be the same as the jrxml


            GET http://localhost:10900/jasper/compilereport

            Request body :
            {
                "jrxml": "D:\\temp\\FAKT_B2B.jrxml"
            }

            Response : 
            {
                "success": true,
                "jrxml": "D:\\temp\\FAKT_B2B.jrxml",
                "jasper": "D:\\temp\\FAKT_B2B.jasper"
            }

### Creating Jasper Reports based on data adapter provided in report (fixed json file)  

Creating a pdf from a report can be done by sending a request to **/jasper/generatepdf**.

When the data adapter specified in the report will be used, the body of the request contains the following keys :
- jasper (required) : location of the report file
- outfile (optional) : path of the pdf file to be created, if not provided a file will be created containing a timestamp.

            GET http://localhost:10900/jasper/generatepdf

            Request body :
            {
                "jasper": "D:\\temp\\FAKT_B2B.jasper"
            }

            Response : 
            {
                "success": true,
                "jasper": "D:\\temp\\FAKT_B2B.jasper",
                "outfile": "D:\\temp\\FAKT_B2B_20221013_0843.pdf"
            }

### Creating Jasper Reports based on json data source (variable json file)

Creating a pdf from a report can be done by sending a request to **/jasper/generatepdf**.

When adding a **main datasource** in the request body, the data adapter will be overriden.
Note : the data adapter and the json file specified in the data adapter must still exist.

This datasource needs two parameters : the location of the json file and the name of the root key (array) used in the report.

To override the data adapter with a data source, the body of the request must contain the following keys :
- jasper (required) : location of the report file
- outfile (optional) : path of the pdf file to be created, if not provided a file will be created containing a timestamp.
- datasources (required) object containing :
    - id (required) : "MAIN" (fixed value)
    - json (required) : location of the json file
    - rootProperty (required) : name of the array in the json

            GET http://localhost:10900/jasper/generatepdf

            Request body :
            {
                "jasper": "D:\\temp\\FAKT_B2B.jasper",
                "datasources": [
                    {
                        "id": "MAIN",
                        "json": "D:\\temp\\FAKT_B2B_20221001_080000.json",
                        "rootProperty": "factuur"
                    }
                ]                
            }

            Response : 
            {
                "success": true,
                "jasper": "D:\\temp\\FAKT_B2B.jasper",
                "outfile": "D:\\temp\\FAKT_B2B_20221013_0843.pdf"
            }

### Creating Jasper Reports based on json data source (variable json file) with **subreports**

Creating a pdf from a report can be done by sending a request to **/jasper/generatepdf**.

When a **main datasource** is added, the data adapter can be overriden with a json datasource (see above).

When adding extra **named datasources**, extra parameters will be used when creating the report.
These parameters are of type JsonDataSource. 

These parameters needs to be created in the main report as well. The parameter name must be specified as "id" in the request body.

In JasperStudio:
* Navigate to Parameters in the main report and add for each datasource a parameter of type "net.sf.jasperreports.engine.data.JsonDataSource".
* Click on the subreport component and navigate to Properties. On Data Source Expression add the parameter name : $P(*parameter name*).
* Note: when using data source expressions, generating an example in JasperStudio will fail.

To use datasources for main and subreports, the body of the request must contain the following information :
- jasper (required) : location of the report file
- outfile (optional) : path of the pdf file to be created, if not provided a file will be created containing a timestamp.
- datasources (required) object containing :
    - id (required) : "MAIN" (fixed value for main data source) or "*parameter name*" (for sub data source).
    - json (required/optional) : location of the json file (optional for sub data sources using the same json file of MAIN)
    - rootProperty (required) : name of the array in the json

            GET http://localhost:10900/jasper/generatepdf

            Request body :
            {
                "jasper": "D:\\temp\\FAKT_B2B.jasper",
                "datasources": [
                    {
                        "id": "MAIN",
                        "json": "D:\\temp\\FAKT_B2B_20221001_080000.json",
                        "rootProperty": "factuur"
                    },
                    {
                        "id": "DS_BESTELLINGEN",
                        "rootProperty": "factuur.bestellingendetail"
                    },
                    {
                        "id": "DS_DETAILBESTELLINGEN",
                        "rootProperty": "factuur.bestellingendetail"
                    },
                    {
                        "id": "DS_DOUANEINFO",
                        "rootProperty": "factuur.douaneinfo"
                    }
                ]                
            }

            Response : 
            {
                "success": true,
                "jasper": "D:\\temp\\FAKT_B2B.jasper",
                "outfile": "D:\\temp\\FAKT_B2B_20221013_0843.pdf"
            }

### Creating Jasper Reports based on xml data source (variable xml file)

Creating a pdf from a report can be done by sending a request to **/jasper/generatepdf**.

When adding a **main datasource** in the request body, the data adapter will be overriden.
Note : the data adapter and the xml file specified in the data adapter must still exist.

This datasource needs two parameters : the location of the xml file and the root node to be used in de XPath expression.

To override the data adapter with a data source, the body of the request must contain the following keys :
- jasper (required) : location of the report file
- outfile (optional) : path of the pdf file to be created, if not provided a file will be created containing a timestamp.
- datasources (required) object containing :
    - id (required) : "MAIN" (fixed value)
    - xml (required) : location of the xml file
    - rootProperty (required) : root node for XPath expression

            GET http://localhost:10900/jasper/generatepdf

            Request body :
            {
                "jasper": "D:\\temp\\FAKT_B2B.jasper",
                "datasources": [
                    {
                        "id": "MAIN",
                        "xml": "G:\\My Drive\\CD-Invest\\Klanten\\Optimco\\jasper\\test\\O-017806886.xml",
                        "rootProperty": "/Body/Document"
                    }
                ]                
            }

            Response : 
            {
                "success": true,
                "outfile": "G:\\My Drive\\CD-Invest\\Klanten\\Optimco\\jasper\\test\\BriefSchade_20230530_103254.pdf",
                "jasper": "G:\\My Drive\\CD-Invest\\Klanten\\Optimco\\jasper\\test\\BriefSchade.jasper"
            }

## ZPL Conversion 

Converting a PDF file to ZPL file can be done by sending a request to **/pdftozpl**.

With this service a pdf file can be converted to Zebra language (ZPL).

The request body can contain the following keys :
* pdffile (required) : path to file to converted
* zplfile (optional) : path to zpl file to be created, when not specified a file will be created based on pdf name
* mediaTracking (optional) : media being used and a value must be entered or the command is ignored 
    * N = continuous media
	* Y = non-continuous media web sensing 
	* W = non-continuous media web sensing  (default)
	* M = non-continuous media mark sensing
* printMode (optional) : print mode
	* T = Tear-off is default Value (default)
	* P = Peel-off (not available on S-300)
	* R = Rewind (depends on printer model)
	* A = Applicator (depends on printer model)
	* C = Cutter (depends on printer model)
	* D = Delayed cutter
* overridePause (optional) : 
    * Y = Yes (default)
    * N = No
* pauseAndCutEveryNLabels (optional) : default 0
* printOrientation (optional) : 
    * N = 0° (default)
    * R = 90°
    * I = 180°
    * B = 270°
* quantityOfLabels (optional) : default 0
* printerDpi (optional) : default 203


            GET http://localhost:10900/pdftozpl

            Request body :
            {
                "pdffile": "D:\\temp\\LABEL_12345.pdf",
                "mediaTracking" : "W",
                "printMode" : "T",
                "overridePause" : "Y",
                "pauseAndCutEveryNLabels", 0,
                "printOrientation" : "N",
                "quantityOfLabels" : 0,
                "printerDpi": 203
            }

            Response : 
            {
                "success": true,
                "outfile": "D:\\temp\\LABEL_12345.zpl",
                "heightMM": 400,
                "widthMM", 600,
            }


## References
* [Creating JasperReports with subreports using datasource](http://junaedhalim.blogspot.com/2009/12/creating-jasperreport-with-subreports.html)

## Credits
* Patrick Kremer - Kabelwerk Eupen AG