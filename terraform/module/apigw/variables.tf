variable "name" {
  type = string
}

variable "lambda_function_name" {
  type = string
}

variable "lambda_integration_uri" {
  type        = string
  description = "Lambda invoke ARN to integrate with API Gateway"
}