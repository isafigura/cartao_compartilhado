@echo off
set "JAVA_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2025.2\jbr"
set "PATH=%JAVA_HOME%\bin;C:\Program Files\Apache\maven\apache-maven-3.9.11\bin;%PATH%"
cd /d "%~dp0"
echo ========================================================
echo Iniciando Back-end Spring Boot (Kotlin + Maven)...
echo Conectando ao PostgreSQL local (cartao_compartilhado)...
echo ========================================================
call mvn spring-boot:run
pause

