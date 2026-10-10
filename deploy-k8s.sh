#!/usr/bin/env bash
set -e

BUILD_IMAGES=false

while [[ "$#" -gt 0 ]]; do
    case $1 in
        --build) BUILD_IMAGES=true ;;
        *) echo "Option inconnue: $1"; exit 1 ;;
    esac
    shift
done

echo "=========================================================="
echo "       ShopFlow - Kubernetes Deployment Automator        "
echo "=========================================================="

# 1. Verification kubectl
if ! command -v kubectl &> /dev/null; then
    echo "Erreur: kubectl n'est pas installe ou pas dans le PATH."
    exit 1
fi

echo ""
echo "[1/4] Verification du cluster Kubernetes..."
kubectl cluster-info > /dev/null || {
    echo "Erreur: Impossible de contacter le cluster Kubernetes."
    exit 1
}
echo "Cluster Kubernetes connecte."

# 2. Build Docker images if requested
if [ "$BUILD_IMAGES" = true ]; then
    echo ""
    echo "[2/4] Construction des images Docker locales..."
    docker build -t shopflow-api-gateway:latest ./api-gateway
    docker build -t shopflow-product-service:latest ./product-service
    docker build -t shopflow-order-service:latest ./order-service
    docker build -t shopflow-stock-service:latest ./stock-service
    docker build -t shopflow-payment-service:latest ./payment-service
    docker build -t shopflow-notification-service:latest ./notification-service
    docker build -t shopflow-frontend:latest ./frontend
    echo "Toutes les images Docker sont pretes."
else
    echo ""
    echo "[2/4] Construction des images ignoree (utiliser --build pour compiler)."
fi

# 3. Apply manifests via Kustomize
echo ""
echo "[3/4] Application des manifests K8s (k8s/kustomization.yaml)..."
kubectl apply -k ./k8s

# 4. Show status
echo ""
echo "[4/4] Etat des ressources dans le namespace 'shopflow'..."
sleep 3
kubectl get pods -n shopflow
kubectl get svc -n shopflow

echo ""
echo "=========================================================="
echo "     ShopFlow Kubernetes Deploiement Initie !            "
echo "=========================================================="
echo "Commandes de port-forwarding recommandees :"
echo "  Frontend : kubectl port-forward svc/frontend 3000:3000 -n shopflow"
echo "  Gateway  : kubectl port-forward svc/api-gateway 8080:8080 -n shopflow"
echo "  Keycloak : kubectl port-forward svc/keycloak 8180:8080 -n shopflow"
echo "  Grafana  : kubectl port-forward svc/grafana 3001:3000 -n shopflow"
echo "  Jaeger   : kubectl port-forward svc/jaeger 16686:16686 -n shopflow"
echo "  Kafka UI : kubectl port-forward svc/kafka-ui 8090:8080 -n shopflow"
echo "=========================================================="
