resource "aws_db_instance" "postgres" {
  identifier        = var.db_identifier
  engine            = "postgres"
  engine_version    = var.engine_version
  instance_class    = var.instance_class
  allocated_storage = var.allocated_storage
  max_allocated_storage = var.allocated_storage  # disables autoscaling
  db_name           = var.db_name
  username          = var.db_username
  password          = var.db_password
  publicly_accessible = var.publicly_accessible
  skip_final_snapshot = true
  backup_retention_period = 0
  multi_az          = false
  storage_type      = "gp2"
  tags              = var.tags
}
