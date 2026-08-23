#!/bin/bash
set -e

APP_DIR="/home/ec2-user/app"
AWS_REGION="ap-southeast-2"
ECR_REGISTRY="102001485020.dkr.ecr.ap-southeast-2.amazonaws.com"
IMAGE_TAG="__IMAGE_TAG__"

mkdir -p "$APP_DIR"

aws ecr get-login-password --region "$AWS_REGION" \
  | docker login --username AWS --password-stdin "$ECR_REGISTRY"

sed "s|IMAGE_TAG_PLACEHOLDER|${IMAGE_TAG}|" \
  "$APP_DIR/docker-compose.template.yaml" > "$APP_DIR/docker-compose.yaml"

cd "$APP_DIR"
docker-compose pull

# shellcheck disable=SC2046
export $(aws secretsmanager get-secret-value \
  --secret-id tuning_server_secrets \
  --region "$AWS_REGION" \
  --query SecretString --output text \
  | jq -r 'to_entries|map("\(.key)=\(.value)")|.[]')

docker-compose up -d

for i in $(seq 1 10); do
  if curl -f http://localhost:8080/actuator/health; then
    echo "Health check passed."
    exit 0
  fi
  echo "Attempt $i failed, retrying..."
  sleep 5
done

echo "Health check failed after 10 attempts."
exit 1