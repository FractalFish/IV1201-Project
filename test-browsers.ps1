# Instructions:

#1. docker compose up db -d

#2. .\mvnw.cmd spring-boot:run

#3. .\test_browser.ps1

Write-Host "Running Edge tests..."
.\mvnw.cmd test "-Dtest=CrossBrowserTest" "-Dbrowser=edge" "-Dtest.mode=local" "-Dsurefire.excludes=none"

Write-Host "Running Chrome tests..."
.\mvnw.cmd test "-Dtest=CrossBrowserTest" "-Dbrowser=chrome" "-Dtest.mode=local" "-Dsurefire.excludes=none"

Write-Host "Running Firefox tests..."
.\mvnw.cmd test "-Dtest=CrossBrowserTest" "-Dbrowser=firefox" "-Dtest.mode=local" "-Dsurefire.excludes=none"

Write-Host "Done!"