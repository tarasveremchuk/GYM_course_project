@echo off
set JAVA="C:\Program Files\Java\jdk-22\bin\java.exe"
set FX="C:\javafx-sdk-21.0.7\lib"
%JAVA% --module-path %FX% --add-modules javafx.controls,javafx.fxml @"C:\Users\verem\AppData\Local\Temp\cp_7jy3l6pnm30fxvlyeir1dwwmm.argfile" com.gym.Main
pause
