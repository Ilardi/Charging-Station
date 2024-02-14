ROOT_KEY=$(cat /run/secrets/root_key)

if ! command -v jq &> /dev/null; then
    echo "jq is not installed. Installing jq..."
    apt-get update
    apt-get install -y jq
fi

sleep 1

vault_response=$(curl \
    -H "X-Vault-Token: $ROOT_KEY" \
    -X GET \
    http://vault:8200/v1/secret/data/application)

private_key=$(printf "%s" "$vault_response" | jq -r '.data.data.private_key')

echo "$private_key" > /localhost.key

nginx -g "daemon off;"