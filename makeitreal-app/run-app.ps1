# MakeItReal (eufyMake) Mobile Preview Launcher
# Launches the Compose Multiplatform Desktop Application simulating mobile viewport (420x880)

$env:JAVA_HOME = "C:\Users\38788\AppData\Local\mise\installs\java\21.0.2"
$gradleBat = "C:\Users\38788\AppData\Local\mise\installs\gradle\8.10.2\gradle-8.10.2\bin\gradle.bat"

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host " Starting eufyMake (MakeItReal) Mobile App " -ForegroundColor Yellow
Write-Host " Viewport: 420x880 Phone Simulation       " -ForegroundColor Gray
Write-Host "==========================================" -ForegroundColor Cyan

Set-Location -Path $PSScriptRoot
& $gradleBat :composeApp:run --quiet
