package webui;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utility.Constant;
import utility.Control;
import utility.Generic;

public class KeywordHandling {
    public static void endExecution()
    {
        Generic.TestScriptEnds();
        Control.GeneratePDFReport();
        System.exit(-1);
    }

    public static void OneTimeInitializationForWeb()
    {
        if(!BrowserHandling.initializeWebDrivers())
        {
            System.out.println("[ One-Time initialization has failed, this is a mandatory step for all other steps, terminating flow ]");

            Generic.WriteTestData("One-time setup", "One-time setup", "", "One time setup should be successful", "One time setup failed", "Fail");
            Generic.TestScriptEnds();
            System.exit(-1);
        }
    }

    public static void DoAtStartOfEveryTestCase(int RowNo) throws Exception
    {
        Constant.terminateOnFailure=getExcelColumnValueFromTag(Constant.RowNo,"Terminate_On_Failure");
        System.out.println("************************** [ TestCase #:"+ RowNo+"] **************************");
        Generic.WriteTestCase(Constant.Map2.get("TestCase"+ RowNo).get("TestCaseNo"), Constant.Map2.get("TestCase"+ RowNo).get("TestCaseName"), Constant.Map2.get("TestCase"+ RowNo).get("Expected_result"), Constant.Map2.get("TestCase"+ RowNo).get("Actual Result"));
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

    //just for formatting the input value
    public static String getInputValue(int RowNo, int Col_input)
    {
        //does not change case
        String InputValue=Constant.Map2.get("TestCase"+ RowNo).get("Step_"+Col_input);

        if((InputValue == null))
        {
            InputValue="";
        }

        try
        {
            InputValue=InputValue.trim();
        }
        catch(Exception e)
        {
            InputValue="";
        }



        try
        {
            if(InputValue.startsWith("mandatory_"))
            {
                InputValue=InputValue.replace("mandatory_", "");
                Constant.isAMandatoryStep=true;
            }

        }
        catch(Exception e)
        {

        }

        System.out.println("After formatting : Step_"+Col_input+":"+InputValue);
        System.out.println("[ Input Value ] : "+InputValue);
        return InputValue;
    }

    //Driver Script for handling Keywords and mapping to functions
    public static void DriverScriptForChatBotExecution(int FromTestCaseNo,int ToTestCaseNo) throws Exception
    {
        // Control.RunProcessWithOutput("python keepWindowAtTop.py", 30, true);
        String InputValue=null;

        Constant.RowNo=FromTestCaseNo;
        Constant.lastTestCaseNumber=ToTestCaseNo;
        while(Constant.RowNo<=ToTestCaseNo)
        {
            Constant.atleastOneFailure=false;
            System.out.println("\n\n [ Handling Row# ] : "+Constant.RowNo);
            try
            {
                String firstColumnValue=getExcelColumnValueFromTag(Constant.RowNo,"Step_1");

                if(firstColumnValue.equals(""))
                {
                    System.out.println("Empty First Column encounted in Row#:"+Constant.RowNo);
                    continue;
                }

                DoAtStartOfEveryTestCase(Constant.RowNo);
                Constant.Col_input=0;
                while(++Constant.Col_input<99999)
                {
                    Constant.isAMandatoryStep=false;
                    InputValue=getInputValue(Constant.RowNo,Constant.Col_input);
                    if(InputValue==null || InputValue.equals(""))
                    {
                        System.out.println("Input Cell is NULL");
                        break;
                    }
                    System.out.println("[INFO] TestStep #"+Constant.Col_input);

                    if (InputValue.startsWith("LAUNCH_WEB_APP"))
                    {
                        OneTimeInitializationForWeb();
                    }

                    else if(InputValue.startsWith("CLICK_GET_STARTED_BUTTON"))
                    {
                        String keyword=InputValue.replace("CLICK_GET_STARTED_BUTTON", "");
                        System.out.println("Condition: [CLICK_GET_STARTED_BUTTON] Data: ["+keyword+"]");

                        WebElement elem = null;
                        try
                        {
                            String xpath = "//button[normalize-space()='Login via Google']";
                            elem=Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
                            elem.click();
                            Generic.WriteTestData("Click on 'Get Started' button", "", "",
                                                  "'Get Started' button should be Clickable",
                                                  "'Get Started' button was clicked", "Pass");
                        }
                        catch (Exception e)
                        {
                            Generic.WriteTestData("Click on 'Get Started' button", "", "",
                                                  "'Get Started' button should be Clickable",
                                                  "'Get Started' button was not clicked", "Fail");
                        }
                    }

                    else if(InputValue.startsWith("CHECK_TEXT_"))
                    {
                        String text = InputValue.replace("CHECK_TEXT_", "");
                        System.out.println("Condition: [CHECK_TEXT_] Data: [" + text + "]");
                        BrowserHandling.checkText(text);
                    }

                    else if(InputValue.startsWith("CLICK_MENU_"))
                    {
                        String keyword = InputValue.replace("CLICK_MENU_", "");
                        System.out.println("Condition: [CLICK_MENU_] Data: ["+keyword+"]");

                        WebElement elem = null;
                        try
                        {
                            String xpath = "//header[contains(@class, 'uni-header')]//*[text()='" + keyword + "']";
                            elem = Constant.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
                            elem.clear();
                            Generic.WriteTestData("Click on '" + keyword + "' in the Menu", "", "",
                                                  keyword + " should be Clickable",
                                                  keyword + " was clicked", "Pass");
                        }
                        catch (Exception e)
                        {
                            Generic.WriteTestData("Click on '" + keyword + "' in the Menu", "", "",
                                                  keyword + " should be Clickable",
                                                  keyword + " was not clicked", "Fail");
                        }
                    }

                    else if(InputValue.startsWith("CLICK_SUBMENU_"))
                    {
                        String keyword = InputValue.replace("CLICK_SUBMENU_", "");
                        System.out.println("Condition: [CLICK_SUBMENU_] Data: ["+keyword+"]");

                        WebElement elem = null;
                        try
                        {
                            String xpath = "//li[@role = 'menuitem']//*[text()='" + keyword + "']";
                            elem=Constant.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
                            elem.click();
                            Generic.WriteTestData("Click on '" + keyword + "' in the Submenu", "", "",
                                                  keyword + " should be Clickable",
                                                  keyword + " was clicked", "Pass");
                        }
                        catch (Exception e)
                        {
                            Generic.WriteTestData("Click on '" + keyword + "' in the Submenu", "", "",
                                                  keyword + " should be Clickable",
                                                  keyword + " was not clicked", "Fail");
                        }
                    }

                    else if(InputValue.startsWith("GO_TO_"))
                    {
                        String url = InputValue.replace("GO_TO_", "");
                        System.out.println("Condition: [GO_TO_] Data: ["+url+"]");
                        BrowserHandling.goToUrl(url);
                    }

                    else if(InputValue.startsWith("CHECK_URL_"))
                    {
                        String url = InputValue.replace("CHECK_URL_", "");
                        System.out.println("Condition: [CHECK_URL_] Data: [" + url + "]");
                        BrowserHandling.checkUrl(url);
                    }

                    else if(InputValue.startsWith("SET_FILE_TYPE_"))
                    {
                        String fileType = InputValue.replace("SET_FILE_TYPE_", "");
                        System.out.println("Condition: [SET_FILE_TYPE_] Data: [" + fileType + "]");
                        BrowserHandling.selectFileType(fileType);
                    }

                    else if(InputValue.startsWith("UPLOADFILE_") && InputValue.contains("GTM"))
                    {
                        InputValue = InputValue.replace("UPLOADFILE_", "");
                        String fileType = InputValue.split("_")[0];
                        String filePath = InputValue.replace(fileType + "_", "");
                        System.out.println("Condition: [UPLOADFILE_] Data: [" + filePath + "]");
                        BrowserHandling.processGTMFile();
                        BrowserHandling.selectFileType(fileType);
                        BrowserHandling.uploadFile(filePath);
                    }

                    else if(InputValue.startsWith("UPLOADBULK"))
                    {
                        InputValue = InputValue.replace("UPLOADBULK", "");
                        TestDataHandling.generateAllBulkFiles();
                        BrowserHandling.selectFileType("Bulk Adjustment Files");
                        BrowserHandling.uploadBulkFile();
                    }

                    else if(InputValue.startsWith("LOAD_"))
                    {
                        InputValue = InputValue.replace("LOAD_", "");
                        String file = TestDataHandling.generateBulkFile(InputValue);
                        System.out.println("[Info] Loaded values from " + file);
                    }

                    else if(InputValue.startsWith("GTM_"))
                    {
                        InputValue = InputValue.replace("GTM_", "");
                        String type = InputValue.split("_")[0];
                        String filePath = InputValue.replace(type + "_", "");

                        System.out.println("Condition: [GTM_] Data: [" + type + ", " + filePath +"]");
                        BrowserHandling.validateGTM(type);
                    }

                    else if(InputValue.startsWith("BATCH_"))
                    {
                        String input = InputValue.replace("BATCH_", "");
                        String[] inputs = input.split("_");
                        BrowserHandling.validateBatchFile(inputs);
                    }

                    else if(InputValue.startsWith("BULK_"))
                    {
                        String input = InputValue.replace("BULK_", "");
                        String[] inputs = input.split("_");
                        TestDataHandling.clearDownloadDirectory();
                        BrowserHandling.validateBulkFile(inputs);
                    }

                    else if(InputValue.startsWith("LOGOUT"))
                    {
                        System.out.println("Condition: [LOGOUT]");

                        WebElement elem = null;
                        WebDriverWait wait = new WebDriverWait(Constant.driver, 15);
                        try
                        {
                            elem=wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//u[text()='Log out']")));
                            elem.click();
                            Generic.WriteTestData("Click on the 'Logout' button", "", "",
                                                  "'Logout' button should be Clickable",
                                                  "'Logout' button was clicked", "Pass");
                        }
                        catch (Exception e)
                        {
                            Generic.WriteTestData("Click on the 'Logout' button", "", "",
                                                  "'Logout' button should be Clickable",
                                                  "'Logout' button was not clicked", "Fail");
                        }
                    }

                    else if(InputValue.startsWith("WAIT_"))
                    {
                        String keyword = InputValue.replace("WAIT_", "");
                        System.out.println("Condition: [WAIT] Data: ["+keyword+"]");
                        try
                        {
                            Thread.sleep(Integer.parseInt(keyword) * 1000);
                        }
                        catch (Exception e)
                        {
                            System.out.println(e.getMessage() );
                            e.printStackTrace();
                        }
                    }

                    else
                    {
                        System.out.println("Invalid keyword provided");
                        Generic.WriteTestData("Invalid keyword found in Input Excel", "Invalid Keyword : "+InputValue, "", "Only valid keyword shall be provided","Input Keyword found in Input Excel :"+InputValue, "Fail");
                    }
                }
            }

            catch(Exception e)
            {
                System.out.println("Exception encountered in Test Case : "+Constant.RowNo+"\nException is : "+e.getMessage() );
                e.printStackTrace();
            }

            finally
            {
            }
            ++Constant.RowNo;
            Generic.TestScriptEnds();
            System.out.println("At end of Test Case : \nRow#:"+Constant.RowNo+"Column#:"+Constant.Col_input+"\nToTestCase:"+ToTestCaseNo);
        }
    }
}
