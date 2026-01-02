# ========================
# IAM Role for Lambda
# ========================
resource "aws_iam_role" "lambda_exec_role" {
  name = "${var.name}-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "lambda_basic" {
  role       = aws_iam_role.lambda_exec_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

# ========================
# Lambda Function
# ========================
resource "aws_lambda_function" "fn" {
  function_name = "${var.name}-function"
  filename      = var.filename
  handler       = var.handler
  runtime       = var.runtime
  memory_size   = var.memory_size
  timeout       = var.timeout

  role = aws_iam_role.lambda_exec_role.arn

  environment {
    variables = {
      QUARKUS_LAMBDA_HANDLER  = "io.quarkus.amazon.lambda.runtime.QuarkusStreamHandler"
      EXPENSE_TABLE           = var.expense_table
      COGNITO_AUTH_SERVER_URL = var.cognito_auth_server_url
      COGNITO_CLIENT_ID       = var.cognito_client_id
    }
  }
}

resource "aws_iam_role_policy_attachment" "lambda_cognito_access" {
  role       = aws_iam_role.lambda_exec_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonCognitoPowerUser"
}