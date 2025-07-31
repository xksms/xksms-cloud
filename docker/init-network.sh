#!/bin/bash

NETWORK_NAME="xksms-net"

echo "🔧 Checking if Docker network '${NETWORK_NAME}' exists..."

docker network inspect $NETWORK_NAME >/dev/null 2>&1

if [ $? -ne 0 ]; then
  echo "✅ Creating network: ${NETWORK_NAME}"
  docker network create ${NETWORK_NAME}
else
  echo "✅ Network '${NETWORK_NAME}' already exists."
fi
