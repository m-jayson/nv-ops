terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.92"
    }
  }

  required_version = ">= 1.2"
}


provider "aws" {
  region = var.aws_region
}

module "postgres" {
  source      = "./module/postgres"
  db_password = "MySuperSecret123!"
  db_username = "pgadmin" # must not be reserved
}

module "cognito" {
  source = "./module/cognito"

  user_pool_name        = "my-app-user-pool"
  user_pool_client_name = "my-app-client"
  callback_urls         = ["http://localhost:3000/callback"]
  logout_urls           = ["http://localhost:3000"]
  domain_name           = "my-app-auth" # optional, for Hosted UI
  tags = {
    Environment = "dev"
    Project     = "my-app"
  }
}

# # Lambda module example
# module "lambda" {
#   source = "./module/lambda"
#
#   name                     = "quarkus-api"
#   filename                 = "function.zip"
#   handler                  = "not.used"
#   runtime                  = "java17"
#   memory_size              = 1024
#   timeout                  = 30
#   expense_table            = "ExpenseTable"
#
#   cognito_auth_server_url  = module.cognito.auth_server_url
#   cognito_client_id        = module.cognito.user_pool_client_id
# }


