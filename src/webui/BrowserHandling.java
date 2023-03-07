package webui;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import io.appium.java_client.internal.Config;
import utility.*;

public class BrowserHandling
{
    public static boolean checkCurrentUser()
    {
        Control.setWaitTimeout(30);
        String xpath = "//div[text()='" + Constant.email + "']";
        boolean exists = Constant.driver.findElements(By.xpath(xpath)).size() != 0;
        Control.setWaitTimeout(Constant.defaultBrowserTimeOut);
        if (exists)
        {
            Constant.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();
        }
        return exists;
    }

    public static void loginFromEmail() throws Exception
    {
        String xpath = "//input[@type='email']";
        WebElement elem = Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        elem.sendKeys(Constant.email, Keys.ENTER);

        xpath = "//input[@type='password']";
        elem = Constant.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        elem.sendKeys(TestDataHandling.getPassword(), Keys.ENTER);

        xpath = "//input[@type='tel']";
        elem = Constant.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        String otp = TOTPGenerator.getTwoFactorCode();
        System.out.println("[INFO] OTP is " + otp);
        elem.sendKeys(otp, Keys.ENTER);
    }

    public static void loginToGoogle() throws Exception
    {
        String xpath = "//button[@class='ant-btn google-button']";
        Control.setWaitTimeout(30);
        boolean exists = Constant.driver.findElements(By.xpath(xpath)).size() != 0;
        Control.setWaitTimeout(Constant.defaultBrowserTimeOut);
        if (exists)
        {
            WebElement elem = Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            elem.click();
            Constant.wait.until(ExpectedConditions.numberOfWindowsToBe(2));
            Set<String> s1 = Constant.driver.getWindowHandles();
            Iterator<String> i1 = s1.iterator();
            String parentWindow = Constant.driver.getWindowHandle();
            while(i1.hasNext())
            {
                String next_tab = i1.next();
                if (!parentWindow.equalsIgnoreCase(next_tab))
                {
                    System.out.println("[INFO] No Account Logged in. Will try to Login");
                    Constant.driver.switchTo().window(next_tab);

                    if (checkCurrentUser())
                    {
                        System.out.println("[INFO] Logged with present email.");
                    }
                    else
                    {
                        loginFromEmail();
                    }
                }
            }
            Constant.driver.switchTo().window(parentWindow);
        }
    }

    public static void checkIfUserIsLogged()
    {
        String xpath = "//span[contains(text(), 'already logged in another device.')]";
        Control.setWaitTimeout(30);
        boolean exists = Constant.driver.findElements(By.xpath(xpath)).size() != 0;
        Control.setWaitTimeout(Constant.defaultBrowserTimeOut);
        if (exists)
        {
            System.out.println("[INFO] Login in another device. Forcing Login");
            xpath = "//div[@class='ant-modal-body']//span[text()='OK']//parent::button";
            WebElement elem = Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            elem.click();
        }
    }

    public static boolean LaunchWebNavigateToUrl()
    {
        try
        {
            Control.OpenApplication("Chrome", Constant.baseURL + "login");
            System.out.println("screen size: " + Constant.driver.manage().window().getSize());
            Control.setDriverEntities();
            Thread.sleep(1500);
            loginToGoogle();
            Thread.sleep(1500);
            checkIfUserIsLogged();
            String xpath = "//img[@alt='Pay2Globe']";
            Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            return true;
        }
        catch(Exception e)
        {
            System.out.println("Exception while launching UNI Web: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static boolean launchWebWithRetries() {
        int ctr=-1;
        while(++ctr<3)
        {
            if(LaunchWebNavigateToUrl())
                return true;
        }
        return false;
    }

    public static boolean initializeWebDrivers()
    {
        if(launchWebWithRetries())
        {
            Generic.WriteTestData("Launch Uni in Web-Browser", "Uni", "Uni","Should be able to launch Uni", "Able to launch Uni", "Pass");
        }
        else
        {
            System.out.println("Unable to launch Uni in Web-Browser, even after retries");
            Generic.WriteTestData("Unable to launch Uni in Web-Browser, even after retries", "Uni", "Uni","Should be able to launch Uni", "Unable to launch Uni", "Fail");
            return false;
        }

        return true;
    }

    public static void clickElementByXpath(String xpath)
    {
        WebElement elem = Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        elem.click();
    }

    public static void selectFileType(String fileType)
    {
        WebElement elem = null;
        String xpath = "//div[@data-testid = 'file-type-dropdown']";
        try
        {
            elem=Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            elem.click();
            xpath = "//div[contains(@class, 'dropdown')]//li[text() = '" + fileType + "']";
            elem=Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            Constant.jsExecutor.executeScript("arguments[0].click()", elem);
            Generic.WriteTestData("Select on '" + fileType + "' in the Upload Dropdown", "", "",
                                  fileType + " can be selected",
                                  fileType + " was select", "Pass");

        }
        catch (Exception e)
        {
            System.out.println("[ERR] " + e.getMessage());
            Generic.WriteTestData("Select on '" + fileType + "' in the Upload Dropdown", "", "",
                                  fileType + " can be selected",
                                  fileType + " was not select", "Fail");
        }
    }

    public static void selectChannelType(String channel)
    {
        WebElement elem = null;
        String xpath = "//div[@data-testid = 'channel-dropdown']";
        try
        {
            elem=Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            elem.click();
            xpath = "//div[contains(@class, 'dropdown')]//li[text() = '" + channel + "']";
            elem=Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            Constant.jsExecutor.executeScript("arguments[0].click()", elem);
            Generic.WriteTestData("Select on '" + channel + "' in the Upload Dropdown", "", "",
                                  channel + " can be selected",
                                  channel + " was select", "Pass");

        }
        catch (Exception e)
        {
            System.out.println("[ERR] " + e.getMessage());
            Generic.WriteTestData("Select on '" + channel + "' in the Upload Dropdown", "", "",
                                  channel + " can be selected",
                                  channel + " was not select", "Fail");
        }
    }

    public static void setUploadDate() throws Exception
    {
        Date date;
        date = new Date();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat tf = new SimpleDateFormat("HH:mm:ss");
        DateFormat rejectRawFormat = new SimpleDateFormat("MMddyyyy");
        df.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        Constant.uploadDate = df.format(date);
        Constant.rejectRawDateFormat = rejectRawFormat.format(date);
    }

    public static void upload(String filePath)
    {
        String xpath = "//input[@type = 'file']";
        WebElement elem=Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        String absPath = new File("DataFiles\\" + filePath).getAbsolutePath();
        System.out.println("[INFO] Uploading::" + absPath);
        elem.sendKeys(absPath);
        xpath = "//span[text()='OK']//parent::button";
        elem=Constant.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        elem.click();
    }

    public static void uploadFile(String filePath)
    {
        WebElement elem = null;
        String xpath = "//input[@type = 'file']";
        try
        {
            elem=Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            Constant.jsExecutor.executeScript("arguments[0].style=''", elem);
            upload(filePath);

            xpath = "//p[normalize-space() = '1 of 1 uploads complete']";
            Control.setWaitTimeout(Constant.uploadTimeout);
            elem = Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            Control.setWaitTimeout(Constant.defaultBrowserTimeOut);
            Generic.WriteTestData("Uploading file '" + filePath + "' to UNI", "", "",
                                  filePath + " should be uploaded",
                                  filePath + " was uploaded", "Pass");
            setUploadDate();
            Generic.WriteTestData("Uploading file '" + filePath + "' to UNI", "", "",
                                  filePath + " should be uploaded",
                                  filePath + " was not uploaded", "Pass");
        }
        catch (Exception e)
        {
            System.out.println("[ERR]" + e.getMessage());
            Generic.WriteTestData("Uploading file '" + filePath + "' to UNI", "", "",
                                  filePath + " should be uploaded",
                                  filePath + " was not uploaded", "Fail");
        }
    }

    public static void uploadBulkFile()
    {
        WebElement elem = null;
        String xpath = "//input[@type = 'file']";

        try
        {
            elem=Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            Constant.jsExecutor.executeScript("arguments[0].style=''", elem);

            String absPath = new File("DataFiles\\").getAbsolutePath();
            File folder = new File(absPath);

            File[] listOfFiles = folder.listFiles();
            String filesToUpload = "";
            int filesUploaded = 0;
            for (int i = 0; i < listOfFiles.length; i++)
            {
                String filename = listOfFiles[i].getAbsolutePath();
                if (!filename.contains("GTM_Template") && filename.endsWith(".xlsx"))
                {
                    filesToUpload = filesToUpload + filename;
                    if (i != listOfFiles.length - 1)
                    {
                        filesToUpload = filesToUpload + "\n";
                    }
                    filesUploaded++;
                }
            }
            elem.sendKeys(filesToUpload.strip());
            System.out.println("[INFO] Uploading files " + filesToUpload);
            xpath = "//*[contains(text(), 'upload " + Integer.toString(filesUploaded) + " file(s).')]";
            Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            setUploadDate();

            xpath = "//span[text()='OK']//parent::button";
            elem=Constant.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
            elem.click();

            xpath = "//*[text() = 'Processing...']";
            int counter = 0;
            int processingFiles = Constant.driver.findElements(By.xpath(xpath)).size();
            while (processingFiles != 0 && counter != 60)
            {
                System.out.println("[INFO] Still Processing " + Integer.toString(processingFiles) + " file(s)");
                Thread.sleep(30000);
                Control.setWaitTimeout(5);
                processingFiles = Constant.driver.findElements(By.xpath(xpath)).size();
                Control.setWaitTimeout(Constant.defaultWaitTimeout);
                counter++;
            }

            if (processingFiles != 0)
            {
                System.out.println("[ERR] " + Integer.toString(processingFiles)+ " file(s) did not process");
                Generic.WriteTestData("Uploading bulk files to UNI", "", "",
                                      Integer.toString(filesUploaded) + " bulk files should be uploaded",
                                      Integer.toString(processingFiles) + " bulk files was not uploaded", "Fail");
            }
        }
        catch (Exception e)
        {
            System.out.println("[ERR] " + e.getMessage());
            Generic.WriteTestData("Uploading bulk files to UNI", "", "",
                                  "Bulk files should be uploaded",
                                  "Error Occured: " + e.getMessage(), "Fail");
        }
    }

    public static void checkText(String text)
    {
        String xpath = "//*[text() ='" + text + "']";
        try
        {
            Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            Generic.WriteTestData("'" + text + "' should be Visible", "", "",
                                  text + " should be Visible",
                                  text + " is visible", "Pass");
        }
        catch (Exception e)
        {
            System.out.println("[ERR] Text not Found: " + text);
            Generic.WriteTestData("Click on '" + text + "' in the Sidebar", "", "",
                                  text + " should be Visible",
                                  text + " is not visible", "Fail");
        }
    }

    public static void goToUrl(String url)
    {
        String xpath = "//header[contains(@class, 'uni-header')]";
        try
        {
            Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            Constant.driver.get(Constant.baseURL + url);
            Generic.WriteTestData("Redirecting to " + url, "", "",
                                  "Should be redirected to " + url,
                                  "Successfully redirected to " + url, "Pass");
            xpath = "//button[@data-testid='auth-action-btn']";
            WebElement elem = Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            elem.click();
            Control.takeScreenshot();
        }
        catch(Exception e)
        {
            Generic.WriteTestData("Redirecting to " + url, "", "",
                                  "Should be redirected to " + url,
                                  "Failed to redirect to", "Fail");

        }
    }

    public static void checkUrl(String url)
    {
        String currentUrl = Constant.driver.getCurrentUrl();
        if (currentUrl.contains(url))
        {
            Generic.WriteTestData("URL should be valid", "", "",
                                  "URL should contain " + url,
                                  "URL is Valid", "Pass");
        }
        else
        {
            Generic.WriteTestData("URL should be valid", "", "",
                                "URL should contain " + url,
                                "URL is invalid. Url is " + currentUrl, "Fail");
        }
    }

    public static void validateGTM(String type)
    {
        WebElement elem = null;
        String xpath = "//div[@data-testid = 'file-selection-dropdown']";
        try
        {
            elem=Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            elem.click();

            xpath = "//div[contains(@class, 'dropdown')]//li[text() = '" + type + "']";
            elem=Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            Constant.jsExecutor.executeScript("arguments[0].click()", elem);
            setDate();

            xpath = "//span[text() = 'Apply']//parent::button";
            elem = Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            elem.click();

            if(type.equals("AR"))
            {
                validateApprovedGTM();
            }
            else if(type.equals("Reject"))
            {
                validateRejectGTM();
            }
            else
            {
                validateRawGTM();
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage() );
            e.printStackTrace();
        }
    }

    public static void processGTMFile()
    {
        String csvFile = "GTM_Template.csv";
        List<String[]> input = TestDataHandling.ReadCsv(csvFile);
        TestDataHandling.categorizeGTMFiles(input);
        // TestDataHandling.getApprovedGTMAccounts();
    }

    public static void validateApprovedGTM()
    {
        WebElement elem = null;

        Map<String, String> approvedAccounts = TestDataHandling.getApprovedGTMAccounts();
        for (Map.Entry<String, String> entry : approvedAccounts.entrySet())
        {
            try
            {
                String tmpDate = Constant.uploadDate.replace("-", "");
                String xpath = "//tr//td[contains(text(), 'ar.Payment') and contains(text(), '" + tmpDate + "') " +
                               "and contains(text(), '" + entry.getKey() + "')]" +
                               "//following-sibling::td[contains (text(), '" + Constant.username + "')]";
                Actions actions = new Actions(Constant.driver);
                elem=Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
                actions.doubleClick(elem).perform();

                xpath = "//div[contains(text(), 'ar.Payment') and contains(text(), '" + tmpDate + "') " +
                               "and contains(text(), '" + entry.getKey() + "')]";
                WebElement modal = Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));

                xpath = "//span[contains(normalize-space(), '" + entry.getValue() + "')]";
                Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));

                xpath = "//span[text() = 'Close']//parent::button";
                clickElementByXpath(xpath);
                Constant.wait.until(ExpectedConditions.invisibilityOf(modal));
                Thread.sleep(1500);
            }
            catch (Exception e)
            {
                System.out.println("[ERR] Valid Account not found in Approved GTM Files: " + entry.getValue());
                Generic.WriteTestData("Validate Approved Accounts in GTM", "", "",
                                      "Approved GTM account should be seen in AR File",
                                      "Account not found: " + entry.getValue(), "Fail");
            }
        }
        Generic.WriteTestData("Validate Approved Accounts in GTM", "", "",
                              "Approved GTM account should be seen in AR File",
                              "All Accounts are found", "Pass");
    }

    public static void validateRawGTM()
    {
        WebElement elem = null;
        List<String[]> rawAccounts = TestDataHandling.getRawGTMAccounts();

        String xpath = "//td[contains(text(), '" + Constant.username + "') and contains(text()," +
                       "'" + Constant.rejectRawDateFormat + "') " +
                       "and contains(text(), 'GTM_Template')]";

        Actions actions = new Actions(Constant.driver);
        elem=Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        actions.doubleClick(elem).perform();


        elem=Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        for (String[] accounts : rawAccounts)
        {
            try
            {
                xpath = "//tr[";
                for (int i = 0; i < accounts.length; i++)
                {
                    xpath = xpath + "contains(normalize-space(),'" + accounts[i] + "')";
                    if (i != accounts.length - 1)
                    {
                        xpath = xpath + " and ";
                    }
                }
                xpath = xpath + "]";
                Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            }
            catch (Exception e)
            {
                System.out.println("[ERR] Account not found in Raw GTM Files: " + String.join(",", accounts));
                Generic.WriteTestData("Validate Raw Accounts in GTM", "", "",
                                      "Raw GTM account should be seen in AR File",
                                      "Account not found: " + String.join(",", accounts), "Fail");
            }
        }
        Generic.WriteTestData("Validate Raw Accounts in GTM", "", "",
                              "Raw GTM account should be seen in AR File",
                              "All " + Integer.toString(rawAccounts.size()) + " accounts are found", "Pass");
        xpath = "//span[text() = 'Close']//parent::button";
        clickElementByXpath(xpath);
    }

    public static void validateRejectGTM()
    {
        WebElement elem = null;

        List<String[]> rejectedAccounts = TestDataHandling.getRejectedGTMAccounts();
        String xpath = "//td[contains(text(), '" + Constant.username + "') and " +
                       "contains(text(), '" + Constant.rejectRawDateFormat + "') " +
                       "and contains(text(), 'Reject.csv')]";

        Actions actions = new Actions(Constant.driver);
        elem=Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        actions.doubleClick(elem).perform();

        for (String[] accounts : rejectedAccounts)
        {
            try
            {
                xpath = "//tr[";
                for (int i = 0; i < accounts.length; i++)
                {
                    xpath = xpath + "contains(normalize-space(),'" + accounts[i] + "')";
                    if (i != accounts.length - 1)
                    {
                        xpath = xpath + " and ";
                    }
                }
                xpath = xpath + "]";
                Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            }
            catch (Exception e)
            {
                System.out.println("[ERR] Rejected Account not found in Rejected GTM Files: " + String.join(",", accounts));
                Generic.WriteTestData("Validate Rejected Accounts in GTM", "", "",
                                      "Rejected GTM account should be seen in AR File",
                                      "Account not found: " + String.join(",", accounts), "Fail");
            }
        }
        Generic.WriteTestData("Validate Rejected Accounts in GTM", "", "",
                              "Rejected GTM account should be seen in AR File",
                              "All " + Integer.toString(rejectedAccounts.size()) + " accounts are found", "Pass");
        xpath = "//span[text() = 'Close']//parent::button";
        clickElementByXpath(xpath);
    }

    public static void clickDropdown(String dropdownXpath, String optionXpath) throws Exception
    {
        WebElement elem=Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(dropdownXpath)));
        elem.click();

        elem=Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(optionXpath)));
        Constant.jsExecutor.executeScript("arguments[0].click()", elem);
        Thread.sleep(1500);
    }


    public static void setDate() throws Exception
    {
        String xpath = "//span[contains(@class, 'calendar-picker')]";
        WebElement elem = Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        elem.click();
        String startDatexpath = "//input[@class='ant-calendar-input ' and @placeholder='Start date']";
        WebElement startDateElem = Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(startDatexpath)));
        if(startDateElem.getAttribute("value").isEmpty())
        {
            startDateElem.sendKeys(Constant.uploadDate, Keys.ENTER);

            xpath = "//input[@placeholder='End date']";
            elem = Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            startDateElem.sendKeys(Constant.uploadDate, Keys.ENTER);
            String date = Constant.uploadDate.split("-")[2];
            if (date.startsWith("0"))
            {
                date = date.replace("0", "");
            }
            xpath = "//tr[contains(@class, 'active-week')]//*[@class = 'ant-calendar-date' and text()='" + date +"']";
            elem = Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            elem.click();

            xpath = "//span[text() = 'Apply']//parent::button";
            elem = Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            elem.click();

            xpath = "//div[@data-testid='pagination-selection']";
            elem = Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            elem.click();

            xpath = "//li[text()='100']";
            elem = Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            elem.click();
        }
        else
        {
            xpath = "//span[text() = 'Apply']//parent::button";
            elem = Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            elem.click();
        }
    }

    public static void setDateMonth(String calendarXpath, String dateXpath) throws Exception
    {
        WebElement elem = Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(calendarXpath)));
        elem.click();
        String calendarModalXpath = "//div[contains(@class, 'ant-calendar-picker-container')]";
        Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(calendarModalXpath)));

        elem = Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(dateXpath)));
        elem.click();
        Thread.sleep(1500);
    }

    public static void checkTable(String checking) throws Exception
    {
        try {
            String xpath = "";
            if (checking.equals("EMPTY"))
            {
                xpath = "//p[@class='ant-empty-description' and text() = 'No Data']";
                Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            }
            else if (checking.equals("NOTEMPTY"))
            {
                xpath = "//p[@class='ant-empty-description' and text() = 'No Data']";
                Control.setWaitTimeout(5);
                Constant.wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
                Control.setWaitTimeout(Constant.defaultWaitTimeout);
            }
        }
        catch (Exception e)
        {
            System.out.println("[ERR] " + e.getMessage());
        }
    }

    public static void waitForTableToProcess() throws Exception
    {
        Thread.sleep(1500);
        String xpath = "//div[contains(@class, 'ant-spin-spinning')]";
        Control.setWaitTimeout(5);
        Constant.wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
        Control.setWaitTimeout(Constant.defaultWaitTimeout);
    }

    public static void clickApplyButton() throws Exception
    {
        String xpath = "//span[text() = 'Apply']//parent::button";
        WebElement elem = Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        elem.click();
    }

    public static void validateBatchFile(String[] inputs)
    {
        String type = inputs[0];
        String company = inputs[1];
        String channel = inputs[2];
        String startMonth = inputs[3];
        String endMonth = inputs[4];
        String checking = inputs[5];
        String dropdownXpath, optionXpath = "";

        try
        {
            dropdownXpath = "//div[@data-testid='file-selection-dropdown']";
            optionXpath = "//div[contains(@class, 'dropdown')]//li[text() = '" + type + "']";
            clickDropdown(dropdownXpath, optionXpath);

            dropdownXpath = "//div[@data-testid='company-dropdown']";
            optionXpath = "//div[contains(@class, 'dropdown')]//li[text() = '" + company + "']";
            clickDropdown(dropdownXpath, optionXpath);

            dropdownXpath = "//div[@data-testid='channel-dropdown']";
            optionXpath = "//div[contains(@class, 'dropdown')]//li[text() = '" + channel + "']";
            clickDropdown(dropdownXpath, optionXpath);

            dropdownXpath = "//input[@data-testid='start-datepicker']";
            optionXpath = "//td[@title='" + startMonth + "']";
            setDateMonth(dropdownXpath, optionXpath);

            dropdownXpath = "//input[@data-testid='end-datepicker']";
            optionXpath = "//td[@title='" + endMonth + "']";
            setDateMonth(dropdownXpath, optionXpath);

            clickApplyButton();
            waitForTableToProcess();
            checkTable(checking);
        }
        catch (Exception e)
        {
            System.out.println("[ERR] " + e.getMessage());
        }
    }

    public static boolean checkZipFile() throws Exception
    {
        File dir = new File(Constant.downloadFolder);

        FileFilter fileFilter = new WildcardFileFilter("*.zip");
        int retries = 0;
        while(retries != 10)
        {
            Thread.sleep(1000);
            retries++;
            if(dir.listFiles(fileFilter).length != 0)
            {
                System.out.println("[INFO] Zip file found");
                return true;
            }
        }
        System.out.println("[WARN] No Zip file found");
        return false;
    }

    public static void waitForModal(String xpath)
    {
        try
        {
            String exportXpath = "//div[@class='ant-modal-content']//span[text() = 'Export']//parent::button";
            boolean exists = false;
            int retries = 0;

            while (!exists && retries != 3)
            {
                Actions actions = new Actions(Constant.driver);
                WebElement elem=Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
                actions.doubleClick(elem).perform();
                Thread.sleep(1500);
                Control.setWaitTimeout(10);
                exists = Constant.driver.findElements(By.xpath(exportXpath)).size() != 0;
                Control.setWaitTimeout(Constant.defaultBrowserTimeOut);
                retries++;
            }
            clickElementByXpath(exportXpath);

            retries = 0;
            while(!checkZipFile() && retries != 3)
            {
                clickElementByXpath(exportXpath);
                retries++;
            }
        }
        catch (Exception e)
        {
            System.out.println("[ERR] BrowserHandlingWait for modal::" + e.getLocalizedMessage());
        }
    }

    public static void validateRawBulk() throws Exception
    {
        Date date = TestDataHandling.overrideDate();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String uploadDate = df.format(date);
        String xpath = "//td[contains(text(), '" + Constant.username + "_" + uploadDate +".xlsx')]";
        waitForModal(xpath);
    }

    public static void validateRejectBulk() throws Exception
    {
        Date date = TestDataHandling.overrideDate();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String uploadDate = df.format(date);
        String xpath = "//td[contains(text(), '" + Constant.username + "_" + uploadDate +"_Reject.xlsx')]";
        waitForModal(xpath);
    }

    public static void checkAccount(String account) throws Exception
    {
        try
        {
            String xpath = "//div[contains(normalize-space(),'" + account + "')]";
            Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        }
        catch (Exception e)
        {
            Constant.invalidAccounts++;
            System.out.println("[ERR] Approved Account not found in Approved Bulk Files: " + account);
            Generic.WriteTestData("Validate Approved Accounts in Bulk File", "", "",
                                  "Approved Bulk File account should be seen in AR File",
                                  "Account not found: " + account, "Fail");
        }
    }
    public static void validateApprovedBulk(String type) throws Exception
    {
        WebElement elem = null;
        String xpath = "";
        Date date = TestDataHandling.overrideDate();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String uploadDate = df.format(date);

        if (type.equals("Deposit Payment"))
        {
            Map<String, ArrayList<String>> approvedDepositAccounts = TestDataHandling.processApprovedDepositPaymentAccounts();
            for (Map.Entry<String,ArrayList<String>> entry : approvedDepositAccounts.entrySet())
            {
                xpath = "//td[contains(text(), '" + Constant.username + "_" + uploadDate +".xlsx')]//.." +
                        "//td[contains(text(), '" + entry.getKey().toUpperCase()+ ".txt')]";
                System.out.println(xpath);
                Actions actions = new Actions(Constant.driver);
                elem=Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
                actions.doubleClick(elem).perform();
                for (String account : entry.getValue())
                {
                    checkAccount(account);
                }
                Control.takeScreenshot();
                xpath = "//span[text() = 'Close']//parent::button";
                clickElementByXpath(xpath);
                Thread.sleep(1500);
            }
        }
        else
        {
            String[] approvedAccounts = TestDataHandling.processApprovedAccounts(type);
            xpath = "//td[contains(text(), '" + Constant.username + "_" + uploadDate +".xlsx')]";

            Actions actions = new Actions(Constant.driver);
            elem=Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            actions.doubleClick(elem).perform();
            for (String account : approvedAccounts)
            {
                checkAccount(account);
            }
            Control.takeScreenshot();
            xpath = "//span[text() = 'Close']//parent::button";
            clickElementByXpath(xpath);
        }
    }

    public static void validateBulkFile(String[] inputs) throws Exception
    {
        System.out.println(Arrays.toString(inputs));
        String fileSelection = inputs[0];
        String type = inputs[1];
        String dropdownXpath, optionXpath = "";
        Constant.invalidAccounts = 0;
        try
        {
            setUploadDate();
            dropdownXpath = "//div[@data-testid='file-selection-dropdown']";
            optionXpath = "//div[contains(@class, 'dropdown')]//li[text() = '" + fileSelection + "']";
            clickDropdown(dropdownXpath, optionXpath);

            dropdownXpath = "//div[@data-testid='file-type-dropdown']";
            optionXpath = "//div[contains(@class, 'dropdown')]//li[text() = '" + type + "']";
            clickDropdown(dropdownXpath, optionXpath);
            setDate();
            clickApplyButton();
            if(fileSelection.contains("AR"))
            {
                validateApprovedBulk(type);
            }
            else if(fileSelection.contains("Raw"))
            {
                validateRawBulk();
                TestDataHandling.validateRejectRawBulk("RAW");
            }
            else if(fileSelection.contains("Reject"))
            {
                validateRejectBulk();
                TestDataHandling.validateRejectRawBulk("REJECTED");
            }

            if(0 == Constant.invalidAccounts)
            {
                Generic.WriteTestData("Validate " + fileSelection + " Accounts in Bulk File", "", "",
                                      "Bulk File account should be seen in " + fileSelection + " File",
                                      "All Accounts are found", "Pass");
            }
            else if (-1 == Constant.invalidAccounts)
            {
                Generic.WriteTestData("Validate " + fileSelection + " Accounts in Bulk File", "", "",
                                      "Bulk File account should be seen in " + fileSelection + " File",
                                      "Mismatch of accounts in generated and uploaded file", "Fail");
            }
            else
            {
                Generic.WriteTestData("Validate " + fileSelection + " Accounts in Bulk File", "", "",
                                      "Bulk File account should be seen in " + fileSelection + " File",
                                      Integer.toString(Constant.invalidAccounts) + " account(s) are invalid", "Fail");

            }
        }
        catch (Exception e)
        {
            System.out.println("[ERR] " + e.getMessage());
            Generic.WriteTestData("Validate " + type + " Accounts in Bulk File", "", "",
                                  "Bulk File account should be seen in " + type + " File",
                                  "Error occured on validating: " + e.getMessage(), "Fail");
            e.printStackTrace();
        }
    }
}