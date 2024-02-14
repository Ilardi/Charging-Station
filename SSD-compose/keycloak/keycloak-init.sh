ROOT_KEY=$(cat /run/secrets/root_key)

sleep 1

vault_response=$(curl \
    -H "X-Vault-Token: $ROOT_KEY" \
    -X GET \
    http://vault:8200/v1/secret/data/application)

export KEYCLOAK_ADMIN=$(echo "$vault_response" | jq -r '.data.data.keycloak_admin')
export KEYCLOAK_ADMIN_PASSWORD=$(echo "$vault_response" | jq -r '.data.data.keycloak_admin_password')

/opt/keycloak/bin/kc.sh start-dev --proxy edge --hostname-strict=false --http-relative-path /keycloak 