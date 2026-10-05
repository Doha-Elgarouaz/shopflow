#!/bin/bash
# ShopFlow — One-click launcher for Unix / macOS / Git Bash

echo "================================================="
echo "  Starting ShopFlow Microservices Platform"
echo "================================================="

# 1. Copy env if missing
if [ ! -f "frontend/.env.local" ]; then
    echo "[INFO] Copying frontend/.env.local.example -> frontend/.env.local"
    cp frontend/.env.local.example frontend/.env.local
fi

# 2. Start containers
echo "[INFO] Building and starting all containers via Docker Compose..."
docker compose up -d --build

# 3. Print URLs
echo ""
echo "================================================="
echo "  ShopFlow is spinning up! Service URLs:"
echo "================================================="
echo "🛍️  Frontend:        http://localhost:3000"
echo "🛡️  Admin Panel:     http://localhost:3000/admin"
echo "🌐 API Gateway:      http://localhost:8080"
echo "🔐 Keycloak IAM:     http://localhost:8180 (admin / admin)"
echo "📨 Kafka UI:         http://localhost:8090"
echo "🔍 Jaeger Tracing:   http://localhost:16686"
echo "📊 Grafana:          http://localhost:3001 (admin / admin)"
echo "🎯 Prometheus:       http://localhost:9090"
echo ""
echo "Demo accounts:"
echo "  - Admin:    admin / admin123"
echo "  - Customer: customer / customer123"
echo "================================================="
