param(
    [ValidateRange(1, 65535)]
    [int]$Port = 8000,
    [switch]$NoWatch
)

$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

Write-Host "Building the website..."
& mvn --batch-mode --no-transfer-progress --projects website --also-make verify
if ($LASTEXITCODE -ne 0) {
    throw "Website build failed (Maven exit code $LASTEXITCODE)."
}

$baseUrl = "http://localhost:$Port/"
Write-Host "Starting the website at $baseUrl (press Ctrl+C to stop)..."
$serveArguments = @(
    "--file", "website/pom.xml",
    "-Delectrostatic.inputDirectory=target/site-source",
    "-Delectrostatic.outputDirectory=target/generated-site",
    "-Delectrostatic.baseUrl=$baseUrl",
    "-Delectrostatic.port=$Port"
)
if (-not $NoWatch) {
    $serveArguments += "-Delectrostatic.watch=true"
    Write-Host "Watch mode is enabled."
}
$serveArguments += "io.github.teggr:electrostatic-maven-plugin:0.0.4:serve"
& mvn @serveArguments

if ($LASTEXITCODE -ne 0) {
    throw "Electrostatic server exited with code $LASTEXITCODE."
}
