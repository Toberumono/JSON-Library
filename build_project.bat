@echo OFF
SET startpath="%~dp0"

cd ..\
powershell -Command "(New-Object Net.WebClient).DownloadFile('https://github.com/Toberumono/Lexer/archive/master.zip', 'lexer.zip')"
"%JAVA_HOME%\bin\jar" xf lexer.zip
del lexer.zip
Rename Lexer-master lexer

cd lexer
if exist setup_project.bat (call setup_project.bat) else (call ant)

cd ..\
cd %startpath%
call ant
