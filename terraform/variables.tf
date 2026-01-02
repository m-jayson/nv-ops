variable "aws_region" {
  default = "ap-southeast-1"
}

variable "name" {
  default = "asn-tracker"
}

variable "tags" {
  description = "AWS resource tags"
  type        = map(string)
  default     = {}
}