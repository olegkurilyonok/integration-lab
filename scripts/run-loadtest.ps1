param(
  [string]$Script = "loadtest.js"
)

$k6 = Get-Command k6 -ErrorAction SilentlyContinue
if (-not $k6) {
  $fallbackPaths = @(
    "$env:ProgramFiles\\k6\\k6.exe",
    "$env:ProgramFiles(x86)\\k6\\k6.exe",
    "$env:LOCALAPPDATA\\Programs\\k6\\k6.exe"
  ) | Where-Object { $_ -and (Test-Path $_) }
  if ($fallbackPaths.Count -gt 0) {
    $k6 = $fallbackPaths[0]
  } else {
    throw "k6 is not installed or not in PATH. See https://k6.io/docs/get-started/installation/"
  }
}

& $k6 run $Script
