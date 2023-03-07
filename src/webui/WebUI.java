package webui;
import utility.*;

public class WebUI
{
		public static void initialize() throws Exception
		{
			System.out.println("*******************************************************");
			System.out.println("\t\t\tInitializing");
			System.out.println("*******************************************************");
			utility.Generic.TestScriptStart(Constant.sheetName);
			utility.Generic.TestDataForUSSD(Constant.sheetName);
		}

		public static void endExecution()
		{
			Constant.driver.close();
			Control.GeneratePDFReport();
		}

		public static void main(String[] args) throws Exception
		{
			initialize();
			KeywordHandling.DriverScriptForChatBotExecution(1, 19);
			endExecution();
			System.exit(0);
		}

}