/*
 *Description: Control Functions library
 'Author :Sunanda Tirunagari and Ankit Kumar
*/

package utility;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.SessionId;
import io.appium.java_client.windows.WindowsDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;

public class Constant
{
    public static final String TestDataFilePath = new File("DataSheet.xlsx").getAbsolutePath();
    public static final String downloadFolder = new File("DownloadedFiles\\").getAbsolutePath();
    public static final String path_to_python_scripts="C:\\Python27_Excel_PDF\\";
    public static final String driverPath = "C:\\Projects\\drivers";
    public static final String baseURL = "https://paysysuat.globe.com.ph/";
    public static String username ="";
    public static int invalidAccounts = 0;

    public static final String sheetName="ActualData";
    public static final int defaultBrowserTimeOut = 60;
    public static String uploadDate = "";
    public static String rejectRawDateFormat = "";
    public static String uploadTime = "";
    public static int defaultWaitTimeout = 30;
    public static int uploadTimeout = 0;
    public static String dateOverride = ""; //YYYY-MM-DD
    /*
     * **********************************************************************************************
     * System Parameters - Please don't touch
     * **********************************************************************************************
     */
    public static boolean atleastOneFailure=false;
    public static boolean isAMandatoryStep=false;
    public static int lastTestCaseNumber=-1;
    public static String terminateOnFailure="";
    public static int Col_input=0;
    public static int SeqID = 1;
    public static int StepIndex = 0;
    public static int TestStepIndex = 0;
    public static int StepStatus = 0;
    public static int TestCaseIndex = 0;
    public static int TestCaseNumber = 0;
    public static int PassedCases = 0;
    public static int FailedCases = 0;
    public static HashMap<String, HashMap<String, String>> Map = new HashMap<String,HashMap<String,String>>();
    public static HashMap<String, HashMap<String, String>> Map2 = new HashMap<String,HashMap<String,String>>();
    public static String UserStoryName = null;
    public static String DataFilePath = null;
    public static String ScreenshotFolderName = null;
    public static String strScenarioDesc=null;
    public static String strExpectedResult=null;
    public static String strActualResult=null;
    public static String PageName=null;
    public static String locator=null;
    public static String ResultFilePath = null;
    public static String RecentScreenshot=null;
    public static WebDriver driver = null;
    public static WebDriverWait wait = null;
    public static JavascriptExecutor jsExecutor = null;
    public static WebDriver driver_web = null;
    public static WebElement webelement;
    public static boolean DefaultoptionalFlag = true;
    public static boolean skipIndicator=false;
    public static int skipToRowNumber=-999;
    public static int RowNo=0;

    public static final String email = "joel.brioso@globe.com.ph";
    public static final String password = "T2VJb28wNTE0IQ0K";
    public static final String googleKey = "5ugrfdzlnti2vroapdvl6c2eud2w6p4v";
}
