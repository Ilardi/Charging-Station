UNSEAL_KEY=$(cat /run/secrets/unseal_key)

vault server -config=/ssd & PID=$!

sleep 0.5

vault operator unseal -address=http://127.0.0.1:8200 $UNSEAL_KEY

wait $PID