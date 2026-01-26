@echo off
echo ========================================
echo Compilation du projet Bibliotheque
echo ========================================
echo.

REM Nettoyage des anciens fichiers .class
echo [1/3] Nettoyage...
if exist fr rmdir /s /q fr
if exist sources.txt del sources.txt

REM Compilation
echo [2/3] Compilation...
powershell -Command "$files = Get-ChildItem -Recurse -Filter '*.java' src/ | Select-Object -ExpandProperty FullName; javac -d . -encoding UTF-8 $files"

if %ERRORLEVEL% EQU 0 (
    echo [3/3] Compilation reussie !
    echo.
    echo ========================================
    echo Utilisez 'run.bat' pour lancer l'application
    echo ========================================
) else (
    echo [ERREUR] Echec de la compilation
    exit /b 1
)

pause
