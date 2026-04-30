CREATE TABLE IF NOT EXISTS datasource_config (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  db_type VARCHAR(32) NOT NULL,
  host VARCHAR(128) NOT NULL,
  port INT NOT NULL,
  database_name VARCHAR(128) NOT NULL,
  username VARCHAR(128) NOT NULL,
  password_encrypt VARCHAR(512) NOT NULL,
  biz_domain VARCHAR(64),
  status TINYINT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS table_metadata (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  datasource_id BIGINT NOT NULL,
  table_name VARCHAR(128) NOT NULL,
  table_comment VARCHAR(512),
  biz_name VARCHAR(128),
  biz_description TEXT,
  is_enabled TINYINT DEFAULT 1,
  schema_version VARCHAR(64),
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS column_metadata (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  table_id BIGINT NOT NULL,
  column_name VARCHAR(128) NOT NULL,
  column_type VARCHAR(64),
  column_comment VARCHAR(512),
  biz_name VARCHAR(128),
  biz_description TEXT,
  example_value VARCHAR(512),
  enum_desc TEXT,
  is_sensitive TINYINT DEFAULT 0,
  mask_type VARCHAR(64),
  is_queryable TINYINT DEFAULT 1,
  is_filterable TINYINT DEFAULT 1,
  is_aggregatable TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS table_relation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  datasource_id BIGINT NOT NULL,
  source_table VARCHAR(128) NOT NULL,
  source_column VARCHAR(128) NOT NULL,
  target_table VARCHAR(128) NOT NULL,
  target_column VARCHAR(128) NOT NULL,
  relation_type VARCHAR(64) DEFAULT 'one_to_many',
  description VARCHAR(512),
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS metric_definition (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  metric_code VARCHAR(64) NOT NULL,
  metric_name VARCHAR(128) NOT NULL,
  metric_alias VARCHAR(512),
  biz_domain VARCHAR(64),
  definition TEXT,
  statistic_logic TEXT,
  filter_condition TEXT,
  related_tables VARCHAR(512),
  related_fields VARCHAR(512),
  example_sql TEXT,
  status TINYINT DEFAULT 1
);

CREATE TABLE IF NOT EXISTS sql_example (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  question TEXT NOT NULL,
  question_pattern VARCHAR(512),
  sql_content TEXT NOT NULL,
  biz_domain VARCHAR(64),
  used_tables VARCHAR(512),
  used_fields VARCHAR(512),
  chart_type VARCHAR(32),
  quality_score INT DEFAULT 80,
  status TINYINT DEFAULT 1
);

CREATE TABLE IF NOT EXISTS query_history (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  query_no VARCHAR(64) NOT NULL,
  user_id BIGINT,
  datasource_id BIGINT,
  question TEXT,
  prompt_version VARCHAR(64),
  model_name VARCHAR(128),
  model_output LONGTEXT,
  generated_sql TEXT,
  executed_sql TEXT,
  status VARCHAR(32),
  error_message TEXT,
  duration_ms INT,
  row_count INT,
  chart_type VARCHAR(32),
  result_summary TEXT,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
