# ========================
# API Gateway (HTTP API)
# ========================
resource "aws_apigatewayv2_api" "api_gateway" {
  name          = "${var.name}-api-gw"
  protocol_type = "HTTP"
}

# ========================
# Lambda Integration
# ========================
resource "aws_apigatewayv2_integration" "lambda_integration" {
  api_id                 = aws_apigatewayv2_api.api_gateway.id
  integration_type       = "AWS_PROXY"
  integration_uri        = var.lambda_integration_uri
  payload_format_version = "2.0"
}

# ========================
# Routes
# ========================
resource "aws_apigatewayv2_route" "catch_all_route" {
  api_id    = aws_apigatewayv2_api.api_gateway.id
  route_key = "ANY /{proxy+}"

  target = "integrations/${aws_apigatewayv2_integration.lambda_integration.id}"

  authorization_type = "NONE"
}

# ========================
# Stage (Production)
# ========================
resource "aws_apigatewayv2_stage" "env_stage" {
  api_id      = aws_apigatewayv2_api.api_gateway.id
  name        = "$default"
  auto_deploy = true   # ✅ prevent accidental downtime

  # ✅ enable structured logging into CloudWatch
  access_log_settings {
    destination_arn = aws_cloudwatch_log_group.api_logs.arn
    format = jsonencode({
      requestId         = "$context.requestId"
      apiId             = "$context.apiId"
      httpMethod        = "$context.httpMethod"
      routeKey          = "$context.routeKey"
      status            = "$context.status"
      integrationStatus = "$context.integrationStatus"
      requestTime       = "$context.requestTime"
      userAgent         = "$context.identity.userAgent"
      sourceIp          = "$context.identity.sourceIp"
    })
  }

  default_route_settings {
    data_trace_enabled     = false
    logging_level          = "INFO"
    throttling_burst_limit = 2000
    throttling_rate_limit  = 1000
  }
}

# ========================
# CloudWatch Log Group for API Gateway
# ========================
resource "aws_cloudwatch_log_group" "api_logs" {
  name              = "/aws/apigateway/${var.name}"
  retention_in_days = 30
}

# ========================
# Permission for API Gateway to Invoke Lambda
# ========================
resource "aws_lambda_permission" "apigw_invoke" {
  statement_id  = "AllowAPIGatewayInvoke"
  action        = "lambda:InvokeFunction"
  function_name = var.lambda_function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.api_gateway.execution_arn}/*"
}