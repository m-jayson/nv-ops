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

variable "expense_table" {
  type = string
}


variable "cognito_auth_server_url" {
  description = "Cognito OIDC issuer / auth server URL"
  type        = string
}

variable "cognito_client_id" {
  description = "Cognito User Pool client ID"
  type        = string
}