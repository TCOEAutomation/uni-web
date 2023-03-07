/*
 *Description: Control Functions library
'Author :Sunanda Tirunagari
 */

package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;

import org.apache.poi.ss.usermodel.*;

public class Generic {


    private static XSSFWorkbook workbook;
    private static XSSFSheet Worksheet;
    private static XSSFCell cell;

    private static String getcelldata(int rownum, int colnum) throws Exception
    {
        String celldata=null;
        DataFormatter formatter = new DataFormatter();
        try{
            //cell= Worksheet.getRow(rownum).getCell(colnum);

         //celldata=cell.getStringCellValue();
            celldata=formatter.formatCellValue(Worksheet.getRow(rownum).getCell(colnum));

        }catch (Exception e) {
            System.out.println("Exception while getCellData : Row,Col"+rownum+","+colnum+e.getMessage());
            e.printStackTrace();

            //System.exit(-1);
        }
        return celldata;
    }

    public static String ReadFromExcel(String strVariable, String strSheetname,int iColumnNo) throws Exception
    {
        //System.out.println("In Read from Excel");
        String strText = null;
        String strData;
        try {
            FileInputStream ExcelFile= new FileInputStream(Constant.TestDataFilePath);
            workbook = new XSSFWorkbook(ExcelFile);
            Worksheet =  workbook.getSheet(strSheetname);
            int totalrows=Worksheet.getLastRowNum();
            for(int i=0; i<totalrows+1;i++)
            {
                strData=getcelldata(i,0);
                //System.out.println("StrData is "+strData);
                //System.out.println("StrVar is "+strVariable);
                if(strVariable.equals(strData.toString())){
                    strText=getcelldata(i,iColumnNo);
                    //System.out.println("*************************** [ FINALLY EQUAL ] *************************** ");
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("Exception while reading from Excel : "+e.getMessage());
            e.printStackTrace();
        }
        return strText;
    }

    public static void TestScriptStart(String UserStoryName) throws Exception
    {
        Constant.UserStoryName = UserStoryName;
        String Main_Folder;
        Main_Folder = new File("").getAbsolutePath() + "\\Results\\";
        Constant.defaultWaitTimeout = Integer.parseInt(ReadFromExcel("Timeout", "AI_TestData", 1));
        Constant.uploadTimeout = Integer.parseInt(ReadFromExcel("UploadTimeout", "AI_TestData", 1));
        Constant.username = ReadFromExcel("Username", "AI_TestData", 1);
        DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        DateFormat TimeFormat = new SimpleDateFormat("HHMMSS");
        Date date2= new Date();

        String Date3 = TimeFormat.format(date2);
        Date date= new Date();
        String Date1 = dateFormat.format(date);
        String strFolderName  = UserStoryName +Date1+"_"+Date3;
        File file = new File(Main_Folder);
        if(!file.exists()) {
            file.mkdir();
        }
        String strFolderPath = Main_Folder;
        file = new File(strFolderPath);
        if(!file.exists()) {
            file.mkdir();
        }
        strFolderName = strFolderPath;
        file = new File(strFolderName);
        if (!file.exists()) {
            file.mkdir();
        }
        String strXlsFileName = strFolderName + "Results.xlsx";

        if (file.exists())
        {
            File FileName= new File(strXlsFileName);
            FileOutputStream fos =new FileOutputStream (FileName);
            XSSFWorkbook Wb = new  XSSFWorkbook();
            XSSFSheet sh = Wb.createSheet("Results");
            Row row = sh.createRow(0);
            String[] columNames = {"Test Scenario Sr No","Test Step No","Test Case/Step Desc","Test Object Name","Test Data","Expected Result","Actual Result","Test Case/Step Status"  ,"Screenshot", "StartTime", "EndTime"};

            for(int i=0;i<=10;i++) {
                Cell cell =row.createCell(i);
                cell.setCellValue(columNames[i]);
            }
            Wb.write(fos);
            fos.close();
            Wb.close();
            strFolderName = strFolderName + File.separator + "Screenshots";

            Constant.ScreenshotFolderName = strFolderName;
            file = new File(strFolderName);
            if(!file.exists())
            {
                file.mkdir();
            }
        }
        Constant.ResultFilePath=strXlsFileName;
    }

    public static void OnlyWriteTestData(String strStepDesc, String strObjectname, String strTestData, String strExpectedResult,String strActualResult, String strStepStatus )
    {
        try
        {
            String strXlsFileName = Constant.ResultFilePath;
            //System.out.println("Results file : "+strXlsFileName);
            int strData,strData1, strData11, strData111;
            strData = Constant.StepIndex;
            strData1 = strData+1;
            Constant.StepIndex = strData1;
            strData = Constant.StepIndex;
            strData11 = Constant.TestStepIndex;
            strData111 = strData11+1;
            Constant.TestStepIndex = strData111;
            strData111 = Constant.TestStepIndex;
            FileInputStream ExcelFile=null;
            try {
            ExcelFile= new FileInputStream(strXlsFileName);
            workbook = new XSSFWorkbook(ExcelFile);
            Worksheet =  workbook.getSheet("Results");
            Row row = Worksheet.createRow(strData);
            String[] columNames = {""+strData111+"",strStepDesc,strObjectname,strTestData,strExpectedResult,strActualResult,strStepStatus};
            for(int i=1;i<8;i++) {
                Cell cell =row.createCell(i);
                cell.setCellValue(columNames[i-1]);
            }

            if(strStepStatus.equalsIgnoreCase("Fail")) {

                    strData11 = Constant.StepStatus;
                    strData11 = strData11+1;
                    Constant.StepStatus = strData11;


                Control.takeScreenshot(false);
                Cell cell = row.createCell(8);
                cell.setCellValue(Constant.ScreenshotFolderName+File.separator+Constant.RecentScreenshot);
                final Hyperlink href = workbook.getCreationHelper().createHyperlink(HyperlinkType.FILE);
                String FolderPath= "File:///"+ Constant.ScreenshotFolderName+File.separator+Constant.RecentScreenshot;
                FolderPath= FolderPath.replace("\\", "/");
                href.setAddress(FolderPath);
                cell.setHyperlink(href);
            }

            }catch (Exception e) {
                System.out.println("Exception in WriteTestData(): "+e.getMessage());
                e.printStackTrace();

            }

            finally
            {
                ExcelFile.close();
                FileOutputStream fos = new FileOutputStream(strXlsFileName);
                workbook.write(fos);
                workbook.close();
                fos.close();
            }
        }
        catch(Exception e)
        {
            System.out.println("Exception while Writing test data : "+e.getMessage());
            e.printStackTrace();
        }
    }

    public static void WriteTestData(String strStepDesc, String strObjectname, String strTestData, String strExpectedResult,String strActualResult, String strStepStatus )
    {
        try
        {
            String strXlsFileName = Constant.ResultFilePath;
            //System.out.println("Results file : "+strXlsFileName);
            int strData,strData1, strData11, strData111;
            strData = Constant.StepIndex;
            strData1 = strData+1;
            Constant.StepIndex = strData1;
            strData = Constant.StepIndex;
            strData11 = Constant.TestStepIndex;
            strData111 = strData11+1;
            Constant.TestStepIndex = strData111;
            strData111 = Constant.TestStepIndex;
            FileInputStream ExcelFile=null;
            try {
            ExcelFile= new FileInputStream(strXlsFileName);
            workbook = new XSSFWorkbook(ExcelFile);
            Worksheet =  workbook.getSheet("Results");
            Row row = Worksheet.createRow(strData);
            String[] columNames = {""+strData111+"",strStepDesc,strObjectname,strTestData,strExpectedResult,strActualResult,strStepStatus};
            for(int i=1;i<8;i++) {
                Cell cell =row.createCell(i);
                cell.setCellValue(columNames[i-1]);
            }

            if(strStepStatus.equalsIgnoreCase("Fail")) {

                    strData11 = Constant.StepStatus;
                    strData11 = strData11+1;
                    Constant.StepStatus = strData11;


                Control.takeScreenshot(false);
                Cell cell = row.createCell(8);
                cell.setCellValue(Constant.ScreenshotFolderName+File.separator+Constant.RecentScreenshot);
                final Hyperlink href = workbook.getCreationHelper().createHyperlink(HyperlinkType.FILE);
                String FolderPath= "File:///"+ Constant.ScreenshotFolderName+File.separator+Constant.RecentScreenshot;
                //FolderPath= FolderPath.replace("\\", "/");
                FolderPath= FolderPath.replace("\\", "/").replace(" ", "%20");
                href.setAddress(FolderPath);
                cell.setHyperlink(href);
            }

            }catch (Exception e) {
                System.out.println("Exception in WriteTestData(): "+e.getMessage());
                e.printStackTrace();

            }

            finally
            {
                ExcelFile.close();

                FileOutputStream fos = new FileOutputStream(strXlsFileName);
                workbook.write(fos);
                workbook.close();
                fos.close();
            }
        }
        catch(Exception e)
        {
            System.out.println("Exception while Writing test data : "+e.getMessage());
            e.printStackTrace();
        }

        finally
        {
            if(strStepStatus.toLowerCase().equals("fail"))
            {
                Constant.atleastOneFailure=true;

                if(Constant.isAMandatoryStep)
                    setSkipIndicator();
            }


            if(Constant.terminateOnFailure.toLowerCase().equals("yes") && Constant.atleastOneFailure && Constant.isAMandatoryStep)
            {
                System.out.println("Flag1:Terminate on Failure is set to YES, will terminate the flow");
                Generic.OnlyWriteTestData("Terminating further execution as Terminate On Failure Indicator is set to Yes in Test Data Sheet", "Terminate Condition Encountered", "Terminate Condition Encountered", "Terminate Condition Encountered", "Terminate Condition Encountered", "Terminate Condition Encountered");

                Generic.TestScriptEnds();

                Control.GeneratePDFReport();
                System.exit(-1);
            }
        }
    }

    public static void setSkipIndicator()
    {
        //to be set incase the mandatory step fails
        String rowNumberToSkipTo=Control.getExcelColumnValueFromTag(Constant.RowNo,"Skip_To_Row_If_Failed");
        if(rowNumberToSkipTo.equals(""))
            Constant.skipIndicator=false;

        else
        {
            try
            {
                Constant.skipToRowNumber=Integer.parseInt(rowNumberToSkipTo);
                Constant.skipIndicator=true;
            }
            catch(Exception e)
            {
                Generic.OnlyWriteTestData("Incorrect value: "+rowNumberToSkipTo+"in Test Excel for Column: Skip_To_Row_If_Failed & Row#: "+Constant.RowNo, "", "SkipToNextPromo", "Input shall be provided in correct format", "Input provided is in incorrect format. Expected in a numeric value.", "Fail");
                Constant.skipIndicator=false;
            }
        }
    }

    public static void WriteTestCase(String sTestCaseNumber,String strScenarioDesc,String strExpectedResult,String strActualResult)
    {
        try
        {
            String strXlsFileName = Constant.ResultFilePath;
            int strData1,strData2, stepStatus, strDiff;
            strData1 = Constant.StepIndex;
            strData2 = Constant.TestStepIndex;
            stepStatus = Constant.StepStatus;
            strDiff= strData1-strData2;
            FileInputStream ExcelFile= new FileInputStream(strXlsFileName);
            workbook =  new XSSFWorkbook(ExcelFile);
            Worksheet =  workbook.getSheet("Results");

            Constant.StepStatus=0;

        int strData = Constant.StepIndex;
        if(Constant.TestCaseNumber==0) {
            strData1=strData+1;
            Constant.StepIndex=strData1 ;
        }
            strData =Constant.StepIndex;

            strData2 = Constant.TestCaseIndex;
            int strData3=strData2+1;
            Constant.TestCaseIndex=strData3;
            //int strData4 = Constant.TestCaseIndex;

            Constant.TestStepIndex=0;
            //Row row1 = Worksheet.createRow(strDiff+1);
            Row row1 = Worksheet.createRow(strData);
            Cell cell=row1.createCell(0);
            cell.setCellValue(sTestCaseNumber);
            cell=row1.createCell(5);
            cell.setCellValue(strExpectedResult);
            cell=row1.createCell(6);
            cell.setCellValue(strActualResult);
            cell=row1.createCell(2);
           // Worksheet.addMergedRegion(new CellRangeAddress(strData,strData,1,4));
            cell.setCellValue(strScenarioDesc);
            cell=row1.createCell(1);
            cell.setCellValue("");
            cell=row1.createCell(3);
            cell.setCellValue("");
            cell=row1.createCell(4);
            cell.setCellValue("");
            cell=row1.createCell(7);
            cell.setCellValue("");
            DateTimeFormatter dtf= DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            cell=row1.createCell(9);
            String tim = LocalDateTime.now().format(dtf);
            cell.setCellValue(tim);

            ExcelFile.close();
            FileOutputStream fos = new FileOutputStream(strXlsFileName);
            workbook.write(fos);
            workbook.close();
            fos.close();
            Constant.strScenarioDesc =strScenarioDesc;
            Constant.strExpectedResult =strExpectedResult;
            Constant.strActualResult =strActualResult;
            Constant.TestCaseNumber=Constant.TestCaseNumber+1;
        }
        catch(Exception e)
        {
            System.out.println("Exception in WriteTestCase: "+e.getMessage());
            e.printStackTrace();
        }
    }

    public static void TestScriptEnds()
    {
        try
        {

            String strXlsFileName = Constant.ResultFilePath;

            int strData1,strData2, stepStatus, strDiff;

            strData1 = Constant.StepIndex;

                strData2 = Constant.TestStepIndex;

                stepStatus = Constant.StepStatus;

                strDiff= strData1-strData2;

                try {

                FileInputStream ExcelFile= new FileInputStream(strXlsFileName);

                        workbook = new XSSFWorkbook(ExcelFile);

                        Worksheet =  workbook.getSheet("Results");

                        Row row = Worksheet.getRow(strDiff);

                        CellStyle style = workbook.createCellStyle();

                        for(int i=0;i<8;i++) {
                                Cell cell =row.getCell(i);
                                if(stepStatus==0) {
                                    style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
                                    style.setAlignment(HorizontalAlignment.CENTER);
                                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                                    cell.setCellStyle(style);
                                }
                                else{
                                    style.setFillForegroundColor(IndexedColors.RED.getIndex());
                                    style.setAlignment(HorizontalAlignment.CENTER);
                                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                                    cell.setCellStyle(style);
                                }
                        }


                        Cell cell =row.getCell(7);
                        if(stepStatus==0) {
                            cell.setCellValue("Pass");
                            cell=row.createCell(10);
                            DateTimeFormatter dtf= DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                            cell.setCellValue(LocalDateTime.now().format(dtf).toString());
                            }
                            else
                            {
                                cell.setCellValue("Fail");
                                cell=row.createCell(10);
                                DateTimeFormatter dtf= DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                                cell.setCellValue(LocalDateTime.now().format(dtf).toString());
                            }


                        cell = row.createCell(8);
                        if(stepStatus==0) {

                        }
                        else
                        {

                            cell.setCellValue(Constant.ScreenshotFolderName);
                            final Hyperlink href = workbook.getCreationHelper().createHyperlink(HyperlinkType.FILE);
                            String FolderPath= "File:///"+ Constant.ScreenshotFolderName;
                            FolderPath= FolderPath.replace("\\", "/");
                            System.out.println(FolderPath);
                            href.setAddress(FolderPath);
                            cell.setHyperlink(href);
                        }



                        Constant.StepStatus=0;
                        int strData = Constant.StepIndex;
                        strData1=strData+1;
                        Constant.StepIndex=strData1 ;
                        strData =Constant.StepIndex;
                        strData2 = Constant.TestCaseIndex;
                        int strData3=strData2+1;
                        Constant.TestCaseIndex=strData3;
                        ExcelFile.close();
                        FileOutputStream fos = new FileOutputStream(strXlsFileName);
                        workbook.write(fos);
                        workbook.close();
                        fos.close();
                }catch (Exception e) {

                        System.out.println(e.getMessage());
                        e.printStackTrace();
                }

        }
        catch(Exception e)
        {
            System.out.println("Exception in TestScriptEnds():"+e.getMessage());
            e.printStackTrace();
        }
    }

    public static void setScreenshothyperlink(String screenshotFilePath) throws Exception
    {
        int strData=Constant.StepIndex;
        try
        {
            FileInputStream ExcelFile= new FileInputStream(Constant.ResultFilePath);
            workbook = new XSSFWorkbook(ExcelFile);
            Worksheet =  workbook.getSheet("Results");
            Row row = Worksheet.getRow(strData);
            Cell cell = row.createCell(8);
            cell.setCellValue(screenshotFilePath);
            final Hyperlink href = workbook.getCreationHelper().createHyperlink(HyperlinkType.FILE);
            String FolderPath= "File:///"+ screenshotFilePath;
            FolderPath= FolderPath.replace("\\", "/");
            href.setAddress(FolderPath);
            cell.setHyperlink(href);
            ExcelFile.close();
            FileOutputStream fos = new FileOutputStream(Constant.ResultFilePath);
            workbook.write(fos);
            workbook.close();
            fos.close();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    public static void TestDataForUSSD(String SheetName) throws Exception
    {
        String ColumnName,ColumnValue;
        int totalrows,totalColumns;
        try {
            FileInputStream ExcelFile= new FileInputStream(Constant.TestDataFilePath);
            workbook = new XSSFWorkbook(ExcelFile);
            Worksheet =  workbook.getSheet(SheetName);
            totalrows = Worksheet.getLastRowNum();
            totalColumns= Worksheet.getRow(0).getLastCellNum();
            for(int i=0; i<=totalrows;i++) {
                Constant.Map2.put("TestCase"+ i , new HashMap<String,String>());
                for(int j=0; j <= totalColumns; j++){
                    ColumnName= getcelldata(0,j);

                        ColumnValue = getcelldata(i,j);
                        //System.out.println("Sheet : "+SheetName+"(Row,Col) : ("+i+","+j+"):"+ColumnValue);
                        Constant.Map2.get("TestCase"+ i).put(ColumnName, ColumnValue);

                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void GenerateReport(String path_to_python_scripts,String path_to_input_excel,String path_to_output_pdf,String input_excel_name,String output_pdf_name)
    {
        String python_command=path_to_python_scripts+"python.exe RP3.py "+path_to_python_scripts+" "+input_excel_name+" "+path_to_input_excel+" "+path_to_output_pdf+" "+output_pdf_name;
        try
        {
            path_to_python_scripts=URLDecoder.decode(path_to_python_scripts, "UTF-8");
            System.out.println("Path to python script : "+path_to_python_scripts);
            python_command=URLDecoder.decode(python_command, "UTF-8");
             System.out.println("Python command: "+python_command);
        }
        catch(Exception e)
        {
                System.out.println("Exception while setting Python Path : "+e.getMessage());
                System.exit(-1);
        }


        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "cd "+path_to_python_scripts+" && "+python_command);
        builder.redirectErrorStream(true);
        try
        {
            String line=null;
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            System.out.println("Buffered Reader : "+r.readLine());

            while (true)
            {
                try
                {
                    line =r.readLine();
                    if(line==null)
                        break;
                    System.out.println(""+line);
                }

                catch(Exception e)
                {
                    System.out.println("Execption while receiving output from Python Script : "+e.getMessage());
                    break;
                }
            }
        }
        catch(Exception e)
        {
        System.out.println("Exception while triggering process to initialize PDF Generation Script  : "+e.getMessage());
        }
    }
}