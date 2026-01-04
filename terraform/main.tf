terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.92"
    }
  }

  required_version = ">= 1.2"

  backend "s3" {
    bucket         = "nv-app-states"
    key            = "nv-app/dev/terraform.tfstate"
    region         = "ap-southeast-1"
    encrypt        = true
  }
}


provider "aws" {
  region = var.aws_region
}


module "cognito" {
  source = "./module/cognito"

  user_pool_name        = "${var.name}-pool"
  user_pool_client_name = "${var.name}-client"
  callback_urls = ["http://localhost:3000/callback"]
  logout_urls = ["http://localhost:3000"]
  domain_name           = "${var.name}-auth"
  tags = {
    Environment = "dev"
    Project     = var.name
  }
}

# # Lambda module example
module "lambda" {
  source = "./module/lambda"
  name   = var.name

  filename = "${path.root}/../target/function.zip"
  handler  = "bootstrap"
  runtime  = "provided.al2023"

  cognito_auth_server_jwks = module.cognito.jwks_url
  cognito_issuer           = module.cognito.issuer

  db_host = "nv-ops-markjaysongonzaga1990-7047.l.aivencloud.com"
  db_name = "defaultdb"
  db_port = "21215"
  db_pwd  = "AVNS_USo4Hk70Kr0gNBw0oSQ"
  db_user = "avnadmin"
  allow_origin = "*"
}

# Deploy API Gateway + attach lambda
module "identity_apigw" {
  source = "./module/apigw"

  name                   = var.name
  lambda_function_name   = module.lambda.lambda_name
  lambda_integration_uri = module.lambda.invoke_arn
}

