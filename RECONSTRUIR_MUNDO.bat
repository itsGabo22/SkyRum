@echo off
echo ==========================================
echo    SKYRUM: RECONSTRUCCION DEL MUNDO
echo ==========================================
echo.
echo 1. Limpiando archivos antiguos...
del /s /q *.class >nul 2>&1
echo [OK] Archivos antiguos eliminados.
echo.
echo 2. Compilando cronicas del norte...
javac @sources.txt
if %errorlevel% neq 0 (
    echo [ERROR] La compilacion ha fallado. Revisa tu configuracion de Java (JDK).
    pause
    exit /b %errorlevel%
)
echo [OK] Compilacion exitosa.
echo.
echo 3. Preparando el ambiente...
if not exist audio mkdir audio
echo [OK] Carpeta de audios verificada.
echo.
echo 4. Iniciando SkyRum Server...
echo El servidor estara disponible en: http://localhost:8080
echo.
java skyrum.SkyRumServer
pause
