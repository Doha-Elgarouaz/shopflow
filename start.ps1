# ShopFlow — One-click launcher for Windows PowerShell
Write-Host "=================================================" -ForegroundColor Cyan
Write-Host "  Starting ShopFlow Microservices Platform" -ForegroundColor Cyan
Write-Host "=================================================" -ForegroundColor Cyan

# 1. Check if .env.local exists for frontend
if (-not (Test-Path "frontend\.env.local")) {
    Write-Host "[INFO] Copying frontend\.env.local.example -> frontend\.env.local" -ForegroundColor Yellow
    Copy-Item "frontend\.env.local.example" "frontend\.env.local"
}

# 2. Start containers with Docker Compose
Write-Host "[INFO] Building and starting all containers via Docker Compose..." -ForegroundColor Green
docker compose up -d --build

# 3. Print URLs
Write-Host ""
Write-Host "=================================================" -ForegroundColor Green
Write-Host "  ShopFlow is spinning up! Service URLs:" -ForegroundColor Green
Write-Host "=================================================" -ForegroundColor Green
Write-Host "🛍️  Frontend:        http://localhost:3000" -ForegroundColor White
Write-Host "🛡️  Admin Panel:     http://localhost:3000/admin" -ForegroundColor White
Write-Host "🌐 API Gateway:      http://localhost:8080" -ForegroundColor White
Write-Host "🔐 Keycloak IAM:     http://localhost:8180 (admin / admin)" -ForegroundColor White
Write-Host "📨 Kafka UI:         http://localhost:8090" -ForegroundColor White
Write-Host "🔍 Jaeger Tracing:   http://localhost:16686" -ForegroundColor White
Write-Host "📊 Grafana:          http://localhost:3001 (admin / admin)" -ForegroundColor White
Write-Host "🎯 Prometheus:       http://localhost:9090" -ForegroundColor White
Write-Host ""
Write-Host "Demo accounts:" -ForegroundColor Yellow
Write-Host "  - Admin:    admin / admin123" -ForegroundColor White
Write-Host "  - Customer: customer / customer123" -ForegroundColor White
Write-Host "=================================================" -ForegroundColor Green
