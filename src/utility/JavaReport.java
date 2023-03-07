/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URLDecoder;

public class JavaReport {

    /*private static final String path_to_python_scripts_raw="C:\\Python27\\";
    private static final String path_to_input_excel="C:\\Python27\\Input\\";
    private static final String path_to_output_pdf="C:\\Python27\\Output\\";
    private static final String input_excel_name="GP_Menu_Validation.xlsx";
    private static final String output_pdf_name="GP_Menu_Validation.pdf";
    private static  String path_to_python_script=null;*/
    /*private static final String python_command_raw=path_to_python_scripts_raw+"python.exe RP3.py "+path_to_python_scripts_raw+" "+input_excel_name+" "+path_to_input_excel+" "+path_to_output_pdf+" "+output_pdf_name;
    private static  String python_command=null;
    private static  String line=null;*/
	//private static final String python_command_raw=path_to_python_scripts_raw+"python.exe RP3.py "+path_to_python_scripts_raw+" "+input_excel_name+" "+path_to_input_excel+" "+path_to_output_pdf+" "+output_pdf_name;

    public void GenerateReport(String path_to_python_scripts,String path_to_input_excel,String path_to_output_pdf,String input_excel_name,String output_pdf_name)
    {
    	String python_command = path_to_python_scripts+"python.exe RP3.py "+path_to_python_scripts+" "+input_excel_name+" "+path_to_input_excel+" "+path_to_output_pdf+" "+output_pdf_name+" "+"PC"+" "+"C:\\Results";
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


                ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "cd "+path_to_python_scripts+" && "+python_command);
                builder.directory(new File(path_to_python_scripts));
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

    public static void main(String[] args)
    {
        // JavaReport jr=new JavaReport();
        //jr.GenerateReport(path_to_python_scripts_raw,path_to_input_excel,path_to_output_pdf,input_excel_name,output_pdf_name);
    }
}
