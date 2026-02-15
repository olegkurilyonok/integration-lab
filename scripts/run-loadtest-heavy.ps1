param(
  # Path to the k6 test script to run; defaults to loadtest-heavy.js in the current directory.
  [string]$Script = "loadtest-heavy.js"
)

if (-not (Get-Command k6 -ErrorAction SilentlyContinue)) {
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
} else {
  $k6 = "k6"
}

& $k6 run $Script
