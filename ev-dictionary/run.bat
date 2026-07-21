@echo off
setlocal
REM EV Dictionary Pro - Quick Start Script

cls
echo.
echo ====================================
echo  EV Dictionary Pro - Quick Launcher
echo ====================================
echo.

cd /d "%~dp0"

set "MVN_CMD=mvn"
where mvn >nul 2>nul
if errorlevel 1 (
    set "MVN_CMD=C:\Users\thang\AppData\Roaming\Code\User\globalStorage\pleiades.java-extension-pack-jdk\maven\latest\bin\mvn.cmd"
    if not exist "%MVN_CMD%" (
        echo.
        echo ERROR: Maven not found. Please install Maven or update run.bat.
        pause
        exit /b 1
    )
)

REM Compile the application
echo [1] Compiling application...
call %MVN_CMD% -q -DskipTests compile
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo.
echo [2] Compilation successful!
echo [3] Starting JavaFX application...
echo.

REM Run the application using JavaFX
call %MVN_CMD% -q javafx:run

echo.
echo If the window appears, EV Dictionary Pro is running.
echo Use the Word field and buttons to lookup, define, drop, or export.

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Failed to start application!
    echo.
    pause
    exit /b 1
)

pause
