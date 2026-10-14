@echo off
setlocal

REM
REM Config
REM

set "DIR=%~dp0"
set "VERSION_FILE=%DIR%version"
set "REMOTE_VERSION_URL=https://raw.githubusercontent.com/yetnt/jaiva/main/core/jaiva-cli/version"

REM
REM Explicit version installation
REM
REM Usage:
REM     jaiva-update 6.0.0-beta.0
REM
REM This skips the latest-version check and installs the
REM specified release directly.
REM

if not "%~1"=="" (
    set "REMOTE_VERSION=%~1"
    echo Installing Jaiva %REMOTE_VERSION%...
    goto DOWNLOAD_UPDATE
)

REM
REM Check local version
REM

set "LOCAL_VERSION=NO-VERSION"
if exist "%VERSION_FILE%" (
    @REM echo No version file found. Will install latest.
    set /p LOCAL_VERSION=<"%VERSION_FILE%"
    @REM exit /b 1
)
echo Checking for Jaiva updates...
if "%LOCAL_VERSION%"=="" (
    echo The local version file is empty.
    exit /b 1
)

REM
REM Fetch latest version
REM

set "TEMP_VERSION=%TEMP%\jaiva-remote-version-%RANDOM%.txt"
powershell -NoProfile -Command ^
"$ErrorActionPreference = 'Stop'; Invoke-WebRequest -Uri '%REMOTE_VERSION_URL%' -OutFile '%TEMP_VERSION%'"
if errorlevel 1 (
    echo Failed to fetch the latest Jaiva version.
    if exist "%TEMP_VERSION%" del /q "%TEMP_VERSION%"
    exit /b 1
)
set "REMOTE_VERSION="
set /p REMOTE_VERSION=<"%TEMP_VERSION%"
del /q "%TEMP_VERSION%"
if "%REMOTE_VERSION%"=="" (
    echo The remote version file is empty.
    exit /b 1
)

REM
REM Compare versions
REM

echo Installed version: %LOCAL_VERSION%
echo Latest version: %REMOTE_VERSION%
if "%LOCAL_VERSION%"=="%REMOTE_VERSION%" (
    echo.
    echo Jaiva is already up to date.
    exit /b 0
)
echo.
echo Updating Jaiva from %LOCAL_VERSION% to %REMOTE_VERSION%...

REM
REM Download release
REM
REM This is also the validation for an explicit version:
REM if the tag does not exist, or the release does not expose
REM jaiva.zip, the download fails.
REM

:DOWNLOAD_UPDATE
set "ZIP=%TEMP%\jaiva-%REMOTE_VERSION%.zip"
set "ZIP_URL=https://github.com/yetnt/jaiva/releases/download/v%REMOTE_VERSION%/jaiva.zip"
set "EXTRACT_DIR=%TEMP%\jaiva-update-%RANDOM%"
powershell -NoProfile -Command ^
"$ErrorActionPreference = 'Stop'; Invoke-WebRequest -Uri '%ZIP_URL%' -OutFile '%ZIP%'"
if errorlevel 1 (
    echo.
    echo Could not find Jaiva release %REMOTE_VERSION% or its jaiva.zip artifact.
    if exist "%ZIP%" del /q "%ZIP%"
    exit /b 1
)

REM
REM Extract release
REM

powershell -NoProfile -Command ^
"$ErrorActionPreference = 'Stop'; Expand-Archive -LiteralPath '%ZIP%' -DestinationPath '%EXTRACT_DIR%' -Force"
if errorlevel 1 (
    echo Failed to extract Jaiva %REMOTE_VERSION%.
    if exist "%ZIP%" del /q "%ZIP%"
    if exist "%EXTRACT_DIR%" rmdir /s /q "%EXTRACT_DIR%"
    exit /b 1
)
del /q "%ZIP%"

REM
REM Remove old Jaiva files
REM

@REM echo Removing old Jaiva files...
@REM for /f "delims=" %%F in ('dir /b /a-d "%DIR%"') do (
@REM     echo %%F | findstr /i /c:"jaiva-install" >nul
@REM     if errorlevel 1 (
@REM         del /q "%DIR%%%F"
@REM     )
@REM )

echo Removing old Jaiva files...

for %%F in (
    "jaiva"
    "jaiva.cmd"
    "jaiva-sources.jar"
    "jaiva.jar"
    "version"
    "jaiva-src.cmd"
    "jaiva-src"
) do (
    if exist "%DIR%%%~F" del /q "%DIR%%%~F"
)

if exist "%DIR%lib" (
    rmdir /s /q "%DIR%lib"
)

REM
REM Install new Jaiva files
REM

echo Installing Jaiva %REMOTE_VERSION%...
xcopy "%EXTRACT_DIR%\*" "%DIR%" /E /I /Y /Q >nul
if errorlevel 1 (
    echo Failed to copy the updated Jaiva files.
    rmdir /s /q "%EXTRACT_DIR%"
    exit /b 1
)

REM
REM Cleanup
REM

rmdir /s /q "%EXTRACT_DIR%"

REM
REM Done
REM

echo.
echo Jaiva has been updated to %REMOTE_VERSION%.
echo.
echo Please restart your terminal and any processes currently using Jaiva.
echo %REMOTE_VERSION%>"%VERSION_FILE%"
exit /b 0