export SPRING_CLOUD_VAULT_TOKEN=$(cat /run/secrets/root_key)

sleep 10

java -jar /app.jar 
