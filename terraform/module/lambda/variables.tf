variable "name" { type = string }
variable "filename" { type = string }
variable "handler" { type = string }
variable "runtime" { type = string }

variable "memory_size" {
  type    = number
  default = 512
}
variable "timeout" {
  type    = number
  default = 15
}

variable "cognito_auth_server_jwks" {
  description = "Cognito OIDC issuer / auth server URL"
  type        = string
}

variable "cognito_issuer" {
  description = "Cognito Issuer"
  type        = string
}
variable "db_host" { type = string }
variable "db_port" { type = string }
variable "db_name" { type = string }
variable "db_user" { type = string }
variable "db_pwd" { type = string }
variable "allow_origin" { type = string }