# ========================
# Cognito User Pool
# ========================
resource "aws_cognito_user_pool" "this" {
  name = var.user_pool_name

  username_attributes = ["email"]
  auto_verified_attributes = ["email"]

  password_policy {
    minimum_length    = var.password_min_length
    require_lowercase = true
    require_uppercase = true
    require_numbers   = true
    require_symbols   = false
  }

  schema {
    name                = "email"
    attribute_data_type = "String"
    required            = true
    mutable             = false
  }

  tags = var.tags
}

# ========================
# Cognito User Pool Client
# ========================
resource "aws_cognito_user_pool_client" "this" {
  name         = var.user_pool_client_name
  user_pool_id = aws_cognito_user_pool.this.id

  generate_secret = false  # SPA safe

  explicit_auth_flows = [
    "ALLOW_USER_PASSWORD_AUTH",
    "ALLOW_REFRESH_TOKEN_AUTH",
    "ALLOW_USER_SRP_AUTH"
  ]

  callback_urls = var.callback_urls
  logout_urls   = var.logout_urls

  supported_identity_providers = ["COGNITO"]

  allowed_oauth_flows = ["code"]
  allowed_oauth_flows_user_pool_client = true
  allowed_oauth_scopes = ["email", "openid", "profile"]
}

# ========================
# Optional Hosted UI Domain
# ========================
resource "aws_cognito_user_pool_domain" "this" {
  count        = var.domain_name != null ? 1 : 0
  domain       = var.domain_name
  user_pool_id = aws_cognito_user_pool.this.id
}