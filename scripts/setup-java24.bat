@echo off
echo ========================================
echo  MDB Java 24 Setup Script
echo ========================================
echo.

echo Current Java version:
java -version 2>nul
if %errorlevel% neq 0 (
    echo Java is not installed or not in PATH
) else (
    echo.
)

echo Project requires Java 24 for latest features and optimal performance.
echo This project has been upgraded to use Java 24.
echo.

echo AUTOMATED INSTALLATION OPTIONS:
echo.
echo Option 1: Download and install Eclipse Temurin 24 (Recommended)
echo   - Visit: https://adoptium.net/temurin/releases
echo   - Select "Java 24" from the version dropdown
echo   - Select "Windows" as the operating system
echo   - Select "x64" as the architecture
echo   - Download the .msi installer
echo   - Run the installer with administrator privileges
echo.

echo Option 2: Use Chocolatey (if installed)
echo   - Run: choco install temurin24
echo.

echo Option 3: Use Scoop (if installed)
echo   - Run: scoop bucket add java
echo   - Run: scoop install temurin24-jdk
echo.

echo MANUAL VERIFICATION STEPS:
echo 1. After installation, open a new command prompt
echo 2. Run: java -version
echo 3. Verify output shows "openjdk version \"24.x.x\""
echo 4. Run: javac -version
echo 5. Verify output shows "javac 24.x.x"
echo.

echo ENVIRONMENT VARIABLES (if needed):
echo - JAVA_HOME should point to Java 24 installation directory
echo - PATH should include %%JAVA_HOME%%\bin
echo.

echo POST-INSTALLATION:
echo - Restart your IDE/command prompt
echo - Run backend build: cd backend && gradlew.bat build
echo - Verify successful compilation with Java 24
echo.

echo Press any key to continue...
pause >nul