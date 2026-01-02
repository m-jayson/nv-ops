variable "user_pool_name" {
  description = "Name of the Cognito User Pool"
  type        = string
}

variable "user_pool_client_name" {
  description = "Name of the Cognito User Pool Client"
  type        = string
}

variable "password_min_length" {
  description = "Minimum password length"
  type        = number
  default     = 8
}

variable "callback_urls" {
  description = "List of callback URLs for OAuth2"
  type        = list(string)
}

variable "logout_urls" {
  description = "List of logout URLs"
  type        = list(string)
}

variable "domain_name" {
  description = "Optional Cognito Hosted UI domain prefix"
  type        = string
  default     = null
}

variable "tags" {
  description = "Tags for the Cognito resources"
  type        = map(string)
  default     = {}
}