/*
 *Description: Control Functions library
'Author :Sunanda Tirunagari & Ankit Kumar
 */

package utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.net.UrlChecker;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.windows.WindowsDriver;

public class Control
{
    private static XSSFWorkbook workbook;
    private static XSSFSheet Worksheet;
    private static XSSFCell cell;

    public static boolean OpenApplication(String browserName, String URL)
    {
        System.out.println("Driver Path : " + Constant.driverPath);
        try
        {

            if(Constant.driver!=null)
            {
                try
                {
                    System.out.println("Existing Browser Session found, will try to close");
                    Constant.driver.close();
                }
                catch(Exception e)
                {
                    System.out.println("[Ignorable] Exception while closing browser session : "+e.getMessage());
                }
            }


            if (browserName.equalsIgnoreCase("Chrome")) {
                System.setProperty("webdriver.chrome.driver", Constant.driverPath + "\\chromedriver.exe");
                ChromeOptions  options=new ChromeOptions ();
                HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
                chromePrefs.put("download.default_directory", Constant.downloadFolder);
                chromePrefs.put("safebrowsing.disable_download_protection", true);
                options.setExperimentalOption("prefs", chromePrefs);
                options.addArguments("--disable-notifications");
                options.addArguments("--ignore-certificate-errors");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                // options.addArguments("--headless");
                options.addArguments("--disable-browser-side-navigation");
                // options.addArguments("user-data-dir=" + Constant.validUserChromeDir);
                Constant.driver = new ChromeDriver(options);
            }
            Constant.driver.manage().timeouts().implicitlyWait(Constant.defaultBrowserTimeOut, TimeUnit.SECONDS);
            Constant.driver.manage().deleteAllCookies();
            Constant.driver.manage().window().setSize(new Dimension(1036, 780));
            Constant.driver.get(URL);
            return true;
        }
        catch(Exception e)
        {
            System.out.println("Exception in Launch Browser/Website: "+e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static void setDriverEntities()
    {
        Constant.wait = new WebDriverWait(Constant.driver, Constant.defaultWaitTimeout);
        Constant.jsExecutor =  (JavascriptExecutor) Constant.driver;
    }

    public static void setWaitTimeout(int timeout)
    {
        Constant.driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
        Constant.wait = new WebDriverWait(Constant.driver, timeout);
    }

    public static void takeScreenshot()
    {
        try
        {
            takeScreenshot(Constant.DefaultoptionalFlag);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void takeScreenshot(boolean optionalFlag)
    {
        //System.out.println("In takeScreenshot()");
        try
        {

            //Thread.sleep(5);
            DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
            Date date= new Date();
            String Date1 = dateFormat.format(date);
            //DateFormat dateFormat1 = new SimpleDateFormat("HHMMSS");
            DateFormat dateFormat1 = new SimpleDateFormat("HHmmssSSS");
            Date date2= new Date();
            String screenshotFilePath;
            String Date3 = dateFormat1.format(date2);

            if(Constant.locator==null)
                Constant.locator=""+System.currentTimeMillis();
            if(Constant.PageName==null)
                Constant.PageName=""+System.currentTimeMillis();

            Constant.locator=Constant.locator.replace(" ", "_");
            Constant.PageName=Constant.PageName.replace(" ", "_");

             screenshotFilePath=Constant.ScreenshotFolderName+File.separator+Constant.PageName+"_" + Constant.locator + "_"+Date1+"_"+Date3+".png";


             Constant.RecentScreenshot = Constant.PageName+"_" + Constant.locator + "_"+Date1+"_"+Date3+".png";
            try
            {
                TakesScreenshot scrShot = ((TakesScreenshot)Constant.driver);
                File srcFile = scrShot.getScreenshotAs(OutputType.FILE);
                File DestFile = new File(screenshotFilePath);
                FileHandler.copy(srcFile,DestFile);
                    if(optionalFlag) {
                        Generic.setScreenshothyperlink(screenshotFilePath);}
                    else {
                        Constant.DefaultoptionalFlag = true;

                    }
                 }catch (Exception e) {
                     e.printStackTrace();
                if(optionalFlag) {
                    Generic.setScreenshothyperlink(screenshotFilePath);}
                else {
                    Constant.DefaultoptionalFlag = true;

                }
            }

        }
        catch(Exception e)
        {

            System.out.println("Exception while taking screenshot : "+e.getMessage());
            e.printStackTrace();
        }
        //System.out.println("Out of takeScreenshot()");
    }

    public static void GeneratePDFReport()
    {
        String excel_path=Constant.ResultFilePath.substring(0,Constant.ResultFilePath.lastIndexOf("\\"))+"\\";
        System.out.println("Path to Result : "+excel_path);
        JavaReport jr=new JavaReport();
        jr.GenerateReport(Constant.path_to_python_scripts, Constant.ResultFilePath, excel_path, Constant.ResultFilePath, "Report_Test_Summary"+".pdf");
        //Constant.driver.quit();
    }

    public static String getExcelColumnValueFromTag(int RowNo, String stringToMatch)
    {
        String ExpectedDeduction="";
        try
        {
            ExpectedDeduction=Constant.Map2.get("TestCase"+ RowNo).get(stringToMatch);
            if((ExpectedDeduction == null))
            {
                ExpectedDeduction="";
            }

            try
            {
                ExpectedDeduction=ExpectedDeduction.trim();
            }
            catch(Exception e)
            {
                ExpectedDeduction="";
            }

        }
        catch(Exception e)
        {
          System.out.println("Exception : "+e.getMessage());
          e.printStackTrace();
        }

        return ExpectedDeduction;
    }
}//End of Class