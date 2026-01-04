output "user_pool_id" {
  value = module.cognito.user_pool_id
}

output "user_pool_client_id" {
  value = module.cognito.user_pool_client_id
}

output "auth_server_url" {
  value = module.cognito.auth_server_url
}

output "issuer" {
  value = module.cognito.issuer
}

output "api_endpoint" {
  value = module.identity_apigw.api_endpoint
}