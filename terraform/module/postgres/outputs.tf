output "db_endpoint" {
  description = "The endpoint of the RDS PostgreSQL instance"
  value       = aws_db_instance.postgres.endpoint
}

output "db_port" {
  description = "The port of the PostgreSQL instance"
  value       = aws_db_instance.postgres.port
}

output "db_username" {
  description = "The master username"
  value       = aws_db_instance.postgres.username
}
