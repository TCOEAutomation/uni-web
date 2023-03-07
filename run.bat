rmdir /s /q target 2>nul
rmdir /s /q Results 2>nul
del /q DataFiles\\*.xlsx
del /q DataFiles\\*.csv

mkdir DownloadedFiles  2>nul
mkdir Results 2>nul

call mvn package
call mvn exec:java -D exec.mainClass=webui.WebUI

C:\Python27_Excel_PDF\python.exe scripts\createHTMLreport.py
C:\Python27_Excel_PDF\python.exe scripts\createJUnitReport.py UNI UNI Results\Results.xlsx Results\Results.xml