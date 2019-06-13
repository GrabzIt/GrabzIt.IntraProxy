@echo off

setlocal

::- Get the Java Version
set KEY="HKLM\SOFTWARE\JavaSoft\Java Runtime Environment"
set VALUE=CurrentVersion
reg query %KEY% /v %VALUE% 2>nul || (
    echo JRE not installed 
    exit /b 1
)
set JRE_VERSION=
for /f "tokens=2,*" %%a in ('reg query %KEY% /v %VALUE% ^| findstr %VALUE%') do (
    set JRE_VERSION=%%b
)

echo JRE VERSION: %JRE_VERSION%

::- Get the JavaHome
set KEY="HKLM\SOFTWARE\JavaSoft\Java Runtime Environment\%JRE_VERSION%"
set VALUE=JavaHome
reg query %KEY% /v %VALUE% 2>nul || (
    echo JavaHome not installed
    exit /b 1
)

set JAVAHOME=
for /f "tokens=2,*" %%a in ('reg query %KEY% /v %VALUE% ^| findstr %VALUE%') do (
    set JAVAHOME=%%b
)

echo JavaHome: %JAVAHOME%
nssm install GrabzItIntraProxy "%JAVAHOME%\bin\java.exe" -jar "%1"

endlocal
