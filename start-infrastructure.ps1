$ErrorActionPreference = "Stop"
Write-Host "Checking Docker engine..." -ForegroundColor Cyan
docker info | Out-Null
Write-Host "Starting PostgreSQL and Redis..." -ForegroundColor Cyan
docker compose up -d db redis
docker compose ps
Write-Host "PostgreSQL is exposed on localhost:5433; Redis on localhost:6379." -ForegroundColor Green
