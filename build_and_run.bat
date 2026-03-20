@echo off
echo =========================================
echo Compilando clases de SkyRum...
echo =========================================
dir /s /B *.java > sources.txt
javac @sources.txt
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ ERROR EN LA COMPILACION. Por favor, verifica que tienes instalado el JDK.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo =========================================
echo Iniciando servidor web de SkyRum...
echo =========================================
echo Abre en tu navegador: http://localhost:8080
echo.
java skyrum.SkyRumServer
pause
