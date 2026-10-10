param(
    [switch]$BuildImages = $false
)

$ErrorActionPreference = "Stop"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "       ShopFlow - Kubernetes Deployment Automator        " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# 1. Check prerequisites
if (-not (Get-Command kubectl -ErrorAction SilentlyContinue)) {
    Write-Error "kubectl n'est pas installe ou pas accessible dans le PATH."
    exit 1
}

Write-Host "`n[1/4] Verification du cluster Kubernetes..." -ForegroundColor Yellow
try {
    kubectl cluster-info | Out-Null
    Write-Host "Cluster Kubernetes accessible avec succes." -ForegroundColor Green
} catch {
    Write-Error "Impossible de contacter un cluster Kubernetes. Verifiez que Docker Desktop (K8s) ou Minikube est demarre."
    exit 1
}

# 2. Build images if requested
if ($BuildImages) {
    Write-Host "`n[2/4] Construction des images Docker locales..." -ForegroundColor Yellow
    $services = @(
        @{ name = "shopflow-api-gateway"; path = "./api-gateway" },
        @{ name = "shopflow-product-service"; path = "./product-service" },
        @{ name = "shopflow-order-service"; path = "./order-service" },
        @{ name = "shopflow-stock-service"; path = "./stock-service" },
        @{ name = "shopflow-payment-service"; path = "./payment-service" },
        @{ name = "shopflow-notification-service"; path = "./notification-service" },
        @{ name = "shopflow-frontend"; path = "./frontend" }
    )

    foreach ($svc in $services) {
        Write-Host "  -> Construction de $($svc.name):latest..." -ForegroundColor Gray
        docker build -t "$($svc.name):latest" $svc.path | Out-Null
    }
    Write-Host "Toutes les images Docker sont pretes." -ForegroundColor Green
} else {
    Write-Host "`n[2/4] Etape de build ignoree (utilisez -BuildImages pour construire)." -ForegroundColor DarkGray
}

# 3. Apply manifests via Kustomize
Write-Host "`n[3/4] Application des manifests K8s (k8s/kustomization.yaml)..." -ForegroundColor Yellow
kubectl apply -k ./k8s

# 4. Display status
Write-Host "`n[4/4] Verification de l'etat des ressources dans le namespace 'shopflow'..." -ForegroundColor Yellow
Start-Sleep -Seconds 3
kubectl get pods -n shopflow
kubectl get svc -n shopflow

Write-Host "`n==========================================================" -ForegroundColor Green
Write-Host "     ShopFlow Kubernetes Deploiement Initie !            " -ForegroundColor Green
Write-Host "==========================================================" -ForegroundColor Green
Write-Host "Pour acceder aux services via port-forwarding :" -ForegroundColor Cyan
Write-Host "  Frontend    : kubectl port-forward svc/frontend 3000:3000 -n shopflow"
Write-Host "  Gateway     : kubectl port-forward svc/api-gateway 8080:8080 -n shopflow"
Write-Host "  Keycloak    : kubectl port-forward svc/keycloak 8180:8080 -n shopflow"
Write-Host "  Grafana     : kubectl port-forward svc/grafana 3001:3000 -n shopflow"
Write-Host "  Jaeger UI   : kubectl port-forward svc/jaeger 16686:16686 -n shopflow"
Write-Host "  Kafka UI    : kubectl port-forward svc/kafka-ui 8090:8080 -n shopflow"
Write-Host "==========================================================" -ForegroundColor Cyan
