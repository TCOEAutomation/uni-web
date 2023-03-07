package webui;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.List;
import java.util.Base64;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.FileWriter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import utility.*;

public class TestDataHandling
{
    public static final List<String> GTMHeaders = Arrays.asList(
                                                                "Receipt Method", "Account Number", "Reserved Date",
                                                                "Collector ID", "Branch Code", "Payment Amount",
                                                                "Payment Date", "Issuer Bank",  "Check Number",
                                                                "Credit Card Number", "Corp Name", "Approval Number",
                                                                "Reference ID", "Manual OR", "MSISDN", "Currency"
                                                               );
    public static List<String[]> rawGTMAccounts = new ArrayList<>();
    public static List<String[]> approvedGTMAccounts = new ArrayList<>();
    public static List<String[]> rejectedGTMAccounts = new ArrayList<>();

    public static List<String[]> headers = new ArrayList<>();
    public static List<String[]> rawAccounts = new ArrayList<>();
    public static List<String[]> approvedAccounts = new ArrayList<>();
    public static List<String[]> rejectedAccounts = new ArrayList<>();
    public static Map<String, List<String[]>> bulkAccounts = new HashMap<String, List<String[]>>();

    public static List<String[]> ReadCsv(String csvFile)
    {
        try
        {
            String absPath = new File("DataFiles\\raw\\" + csvFile).getAbsolutePath();
            FileReader filereader = new FileReader(absPath);

            CSVReader csvReader = new CSVReader(filereader);
            List<String[]> input = csvReader.readAll();
            csvReader.close();
            return input;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void categorizeGTMFiles(List<String[]> inputData)
    {
        approvedGTMAccounts.clear();
        rejectedGTMAccounts.clear();
        rawGTMAccounts.clear();

        int headerLocation = 0;
        List<String[]> gtmBufferRows = new ArrayList<>();

        for (int i = 0; i < inputData.size(); ++i)
        {
            String[] bufferRow = inputData.get(i);
            String[] br = Arrays.copyOfRange(bufferRow, 1, bufferRow.length);
            gtmBufferRows.add(br);
            if (GTMHeaders.get(0).equals(inputData.get(i)[1]))
            {
                headerLocation = i + 1;
                break;
            }
        }

        for (int i = headerLocation; i < inputData.size(); i++)
        {
            String[] accountRow = inputData.get(i);

            String headerToCheck = accountRow[0];

            String[] account = Arrays.copyOfRange(accountRow, 1, accountRow.length);
            rawGTMAccounts.add(account);

            if (headerToCheck.equals("REJECTED"))
            {
                rejectedGTMAccounts.add(account);
                continue;
            }
            approvedGTMAccounts.add(account);
        }

        try
        {
            int counter = 0;
            FileWriter outputfile = new FileWriter("DataFiles\\GTM_Template.csv");
            CSVWriter writer = new CSVWriter(outputfile, ',',
                                             CSVWriter.NO_QUOTE_CHARACTER,
                                             CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                                             CSVWriter.DEFAULT_LINE_END);
            List<String[]> data = new ArrayList<String[]>();
            for (String[] buffer : gtmBufferRows)
            {
                data.add(buffer);
            }
            for (String[] account : rawGTMAccounts)
            {
                data.add(account);
            }
            writer.writeAll(data);
            writer.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
        }
    }

    public static Map<String, String> getApprovedGTMAccounts()
    {
        Map<String, String> processedApprovedGTMAccount = new HashMap<>();

        for (int i = 0; i < approvedGTMAccounts.size(); i++)
        {
            String locator = "";
            for (int j = 0; j < GTMHeaders.indexOf("Approval Number"); j++)
            {
                String tmp = approvedGTMAccounts.get(i)[j];
                if (GTMHeaders.indexOf("Reserved Date") == j)
                {
                    for (int k = tmp.length(); k < 3; k++)
                    {
                        tmp = tmp + "0";
                    }
                    tmp = "\"" + tmp + "\",";
                }
                else if (GTMHeaders.indexOf("Payment Date") == j)
                {
                    tmp = tmp.replace("/", "");
                    tmp = "\"" + tmp + "\",";
                }
                else if(GTMHeaders.indexOf("Payment Amount") == j)
                {
                    Float payment = Float.parseFloat(tmp);
                    tmp = String.format("%.2f", payment) + ",";
                }
                else
                {
                    tmp = "\"" + tmp + "\",";
                }
                locator = locator + tmp;
            }

            String collectorID = approvedGTMAccounts.get(i)[GTMHeaders.indexOf("Collector ID")];
            processedApprovedGTMAccount.put(collectorID, locator);
        }
        return processedApprovedGTMAccount;
    }

    public static List<String[]> getRejectedGTMAccounts()
    {
        return rejectedGTMAccounts;
    }

    public static List<String[]> getRawGTMAccounts()
    {
        return rawGTMAccounts;
    }

    public static Map<String, List<String[]>> getBulkAccounts()
    {
        return bulkAccounts;
    }

    public static String[] processApprovedAccounts(String type)
    {
        List<String[]> approvedAccounts = getBulkAccounts().get("APPROVED");
        String[] processApprovedAccounts = new String[approvedAccounts.size()];
        int counter = 0;
        for (String[] accounts : approvedAccounts)
        {
            if (type.equals("Account Level") || type.equals("Charge Level") || type.equals("Invoice Level") || type.equals("Immediate Charge"))
            {
                Float payment = Float.parseFloat(accounts[2]);
                accounts[2] = String.format("%.2f", payment);
            }
            if (type.equals("Deposit Creation") || type.equals("Deposit Release") )
            {
                BigDecimal payment = new BigDecimal(accounts[1]);
                payment = payment.setScale(2, BigDecimal.ROUND_UP);

                accounts[1] = payment.toString();
            }
            if (type.equals("LPF"))
            {
                accounts[1] = "ACCUPD";
            }
            if (type.equals("Invoice Level"))
            {
                Float payment = Float.parseFloat(accounts[2]);
                accounts[2] = String.format("%.2f", payment);
                accounts[5] = "Auto";
            }
            if (type.equals("Zero-Out Refund"))
            {
                Map<String, String> refundMap = new HashMap<>();
                refundMap.put("200011", "PREPAD");
                refundMap.put("200012", "WRLINE");
                refundMap.put("200014", "WRLESS");
                refundMap.put("200015", "CORP");
                Float payment = Float.parseFloat(accounts[3]);
                if (refundMap.containsKey(accounts[0]) && !(refundMap.containsValue(accounts[5])))
                {
                    accounts[5] = refundMap.get(accounts[0]);
                }
                accounts[2] = accounts[2].toLowerCase();
                accounts[3] = String.format("%.2f", payment);
            }
            if (type.equals("Immediate Charge") || type.equals("Fund Transfer") || type.equals("Advance Payment Creation"))
            {
                Float payment = Float.parseFloat(accounts[4]);
                accounts[4] = String.format("%.2f", payment);
            }
            if (type.equals("Overpayment/Refund"))
            {
                Float payment = Float.parseFloat(accounts[4]);
                accounts[4] = String.format("%.2f", payment);
            }
            for (int i = 0; i < accounts.length; i++)
            {
                accounts[i] = accounts[i].replace("(", "").replace(")", "").replace(",", "");
                accounts[i] = accounts[i].strip();
            }
            processApprovedAccounts[counter++] = String.join(",", accounts);
        }
        return processApprovedAccounts;
    }

    public static Map<String, ArrayList<String>> processApprovedDepositPaymentAccounts()
    {

        Map<String, ArrayList<String>> depositPaymentAccounts = new HashMap<String, ArrayList<String>>();
        List<String[]> approvedAccounts = getBulkAccounts().get("APPROVED");
        for (String[] accounts : approvedAccounts)
        {
            Float payment = Float.parseFloat(accounts[4]);
            accounts[4] = String.format("%.2f", payment);
            for (int i = 0; i < accounts.length; i++)
            {
                accounts[i] = accounts[i].replace("(", "").replace(")", "").replace(",", "");
                accounts[i] = accounts[i].replace("/", "");
                accounts[i] = accounts[i].strip();
            }
            accounts[1] = accounts[1] + ",000";
            accounts[10] = accounts[10] + ",\"\"";
            accounts[12] = accounts[12] + ",N,\"\"";
            String x = String.join(",", accounts);
            if (!depositPaymentAccounts.containsKey(accounts[2])) {
                depositPaymentAccounts.put(accounts[2], new ArrayList<String>());
            }
            depositPaymentAccounts.get(accounts[2]).add(x);
        }
        return depositPaymentAccounts;
    }

    public static Date overrideDate() throws Exception
    {
        Date date;
        if (Constant.dateOverride.equals(""))
        {
            date = new Date();
        }
        else
        {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(Constant.dateOverride);
        }
        return date;
    }

    public static String createDataFile(String filePath, String sheetName) throws Exception
    {
        XSSFWorkbook workbook;
        XSSFSheet Worksheet;
        XSSFCell cell;
        try
        {
            Date date = overrideDate();
            DateFormat df = new SimpleDateFormat("yyyyMMdd");
            String uploadDate = df.format(date);
            String filename = filePath + " " + Constant.username + " " + uploadDate + ".xlsx";
            String absPath = new File("DataFiles\\" + filename).getAbsolutePath();
            workbook = new XSSFWorkbook();
            Worksheet =  workbook.createSheet(sheetName);

            int totalrows = bulkAccounts.get("RAW").size();
            int totalCols = bulkAccounts.get("HEADERS").get(0).length;

            XSSFRow row = Worksheet.createRow(0);
            for(int i=0; i < totalCols; i++)
            {
                String text = bulkAccounts.get("HEADERS").get(0)[i];
                cell = row.createCell(i);
                cell.setCellValue(text);
            }

            for(int i=0; i<totalrows; i++)
            {
                row = Worksheet.createRow(i+1);
                for(int j=0; j < totalCols; j++)
                {
                    String text = bulkAccounts.get("RAW").get(i)[j];
                    cell = row.createCell(j);
                    cell.setCellValue(text);
                }
            }
            FileOutputStream fos = new FileOutputStream(absPath);
            workbook.write(fos);
            fos.close();
            return filename;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static void generateAllBulkFiles() throws Exception
    {
        String absPath = new File("DataFiles\\raw\\").getAbsolutePath();
        File folder = new File(absPath);

        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++)
        {
            String filename = listOfFiles[i].getName();
            if (!filename.contains("GTM_Template") && filename.endsWith(".xlsx"))
            {
                generateBulkFile(filename.replace(".xlsx", ""));
            }
        }
    }

    public static String generateBulkFile(String filePath) throws Exception
    {
        XSSFWorkbook workbook;
        XSSFSheet Worksheet;
        String cellData=null;
        DataFormatter formatter = new DataFormatter();
        if (0 != bulkAccounts.size())
        {
            bulkAccounts.get("HEADERS").clear();
            bulkAccounts.get("REJECTED").clear();
            bulkAccounts.get("RAW").clear();
            bulkAccounts.get("APPROVED").clear();
            bulkAccounts.clear();
        }

        try
        {
            String absPath = new File("DataFiles\\raw\\" + filePath + ".xlsx").getAbsolutePath();

            FileInputStream ExcelFile= new FileInputStream(absPath);
            workbook = new XSSFWorkbook(ExcelFile);
            Worksheet =  workbook.getSheetAt(0);

            int totalrows=Worksheet.getLastRowNum();
            int totalcols=Worksheet.getRow(0).getPhysicalNumberOfCells();
            for(int i=0; i <= totalrows; i++)
            {
                String[] curRow = new String[totalcols];
                for(int j=1; j <= totalcols; j++)
                {
                    cellData = formatter.formatCellValue(Worksheet.getRow(i).getCell(j));
                    curRow[j - 1] = cellData;
                }

                String type = formatter.formatCellValue(Worksheet.getRow(i).getCell(0));

                if(i == 0)
                {
                    headers.add(curRow);
                    continue;
                }
                if(type.contains("APPROVED"))
                {
                    approvedAccounts.add(curRow);
                }
                else
                {
                    rejectedAccounts.add(curRow);
                }
                rawAccounts.add(curRow);
            }
            bulkAccounts.put("HEADERS", headers);
            bulkAccounts.put("REJECTED", rejectedAccounts);
            bulkAccounts.put("RAW", rawAccounts);
            bulkAccounts.put("APPROVED", approvedAccounts);
            String file = createDataFile(filePath, Worksheet.getSheetName());
            return file;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        return destFile;
    }

    public static void unzipFile() throws Exception
    {
        File dir = new File(Constant.downloadFolder);

        FileFilter fileFilter = new WildcardFileFilter("*.zip");
        int retries = 0;
        while(dir.listFiles(fileFilter).length == 0 && retries != 10)
        {
            Thread.sleep(1000);
            retries++;
        }
        String fileZip = dir.listFiles(fileFilter)[0].getAbsolutePath();

        File destDir = new File(Constant.downloadFolder);
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null)
        {
            File newFile = newFile(destDir, zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    public static void validateRejectRawBulk(String type)
    {
        XSSFWorkbook workbook;
        XSSFSheet Worksheet;

        DataFormatter formatter = new DataFormatter();
        try
        {
            Thread.sleep(1000);
            unzipFile();
            File dir = new File(Constant.downloadFolder);
            FileFilter fileFilter = new WildcardFileFilter("*.xlsx");
            String absPath = dir.listFiles(fileFilter)[0].getAbsolutePath();

            FileInputStream ExcelFile= new FileInputStream(absPath);
            workbook = new XSSFWorkbook(ExcelFile);
            Worksheet =  workbook.getSheetAt(0);

            int totalrows=Worksheet.getLastRowNum();
            int totalcols=Worksheet.getRow(0).getPhysicalNumberOfCells();

            if (bulkAccounts.get(type).size() != totalrows)
            {
                System.out.println("[ERR] Mismatch number of accounts: " + totalrows + " from generated " +
                                   "and " + bulkAccounts.get(type).size() + " from uploaded file");
                Constant.invalidAccounts = -1;
                return;
            }

            for(int i=1; i <= totalrows; i++)
            {
                for(int j=0; j < totalcols; j++)
                {
                    try
                    {
                        String fromDownloaded = formatter.formatCellValue(Worksheet.getRow(i).getCell(j));
                        String fromUploaded = bulkAccounts.get(type).get(i-1)[j].strip();
                        if (type.equals("REJECTED"))
                        {
                            fromUploaded = fromUploaded.replace(",", "");
                            fromUploaded = fromUploaded.replace("(", "");
                            fromUploaded = fromUploaded.replace(")", "");
                        }

                        if(!fromDownloaded.contains(fromUploaded))
                        {
                            Constant.invalidAccounts++;
                            System.out.println("[ERR] Invalid Account#" + i + ":" + fromDownloaded + "::" + fromUploaded);
                        }
                    }
                    catch (Exception e)
                    {
                        Constant.invalidAccounts++;
                        System.out.println("[ERR] Account# " + i + " is not found in uploaded or generated");
                        break;
                    }
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void clearDownloadDirectory() throws Exception
    {
        File dir = new File(Constant.downloadFolder);
        FileUtils.cleanDirectory(dir);
    }

    public static String getPassword()
    {
        String decrptData  = Constant.password;
        byte[] decodeBytes = Base64.getDecoder().decode(decrptData.getBytes());
        return new String(decodeBytes);
    }
}