variable "db_identifier" {
  description = "RDS instance identifier"
  type        = string
  default     = "free-postgres-db"
}

variable "db_name" {
  description = "Database name"
  type        = string
  default     = "mydb"
}

variable "db_username" {
  description = "Master DB username (cannot be a reserved word like admin/postgres/root)"
  type        = string
  default     = "pgadmin"   # safe choice
}

variable "db_password" {
  description = "Master DB password"
  type        = string
  sensitive   = true
}

variable "instance_class" {
  description = "RDS instance class (smallest to minimize cost)"
  type        = string
  default     = "db.t3.micro"
}

variable "engine_version" {
  description = "PostgreSQL engine version"
  type        = string
  default     = "18.1"
}

variable "allocated_storage" {
  description = "Storage in GB (minimal for cost savings)"
  type        = number
  default     = 20
}

variable "publicly_accessible" {
  description = "Whether the DB is publicly accessible"
  type        = bool
  default     = true
}

variable "tags" {
  description = "Tags for the RDS instance"
  type        = map(string)
  default     = {
    Environment = "dev"
    Terraform   = "true"
  }
}