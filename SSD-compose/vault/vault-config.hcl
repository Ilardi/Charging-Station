ui=true
log_level = "info"
log_format = "json"
log_file = "/vault/logs/vault.log"

storage "file" {
  path = "/vault/data"
}

listener "tcp" {
  address          = "0.0.0.0:8200"
  tls_disable      = 1
}