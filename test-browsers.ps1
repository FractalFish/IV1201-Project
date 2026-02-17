# Script to run cross-browser tests
# Note: Make sure Docker is running and app is started on localhost:8080 first

Write-Host "Starting Selenium containers..."
docker-compose -f docker-compose.selenium.yml up -d

# Give containers time to start up
Write-Host "Waiting for containers to be ready..."
Start-Sleep -Seconds 10

# Run tests for each browser
Write-Host "Running Chrome tests..."
.\mvnw.cmd test -Dtest=CrossBrowserTest -Dbrowser=chrome

Write-Host "Running Firefox tests..."
.\mvnw.cmd test -Dtest=CrossBrowserTest -Dbrowser=firefox

Write-Host "Running Edge tests..."
.\mvnw.cmd test -Dtest=CrossBrowserTest -Dbrowser=edge

# Clean up containers
Write-Host "Stopping containers..."
docker-compose -f docker-compose.selenium.yml down

Write-Host "Done!"
