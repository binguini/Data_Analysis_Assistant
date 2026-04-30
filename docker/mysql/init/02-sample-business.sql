CREATE TABLE IF NOT EXISTS user_info (
  user_id BIGINT PRIMARY KEY,
  register_time DATETIME NOT NULL,
  channel VARCHAR(64) NOT NULL,
  region VARCHAR(64) NOT NULL
) COMMENT='示例用户信息表';

TRUNCATE TABLE user_info;

INSERT INTO user_info (user_id, register_time, channel, region) VALUES
  (10001, DATE_SUB(NOW(), INTERVAL 29 DAY), '自然流量', '华东'),
  (10002, DATE_SUB(NOW(), INTERVAL 29 DAY), '广告投放', '华南'),
  (10003, DATE_SUB(NOW(), INTERVAL 28 DAY), '自然流量', '华北'),
  (10004, DATE_SUB(NOW(), INTERVAL 27 DAY), '应用商店', '华东'),
  (10005, DATE_SUB(NOW(), INTERVAL 26 DAY), '广告投放', '华东'),
  (10006, DATE_SUB(NOW(), INTERVAL 25 DAY), '自然流量', '西南'),
  (10007, DATE_SUB(NOW(), INTERVAL 24 DAY), '应用商店', '华南'),
  (10008, DATE_SUB(NOW(), INTERVAL 23 DAY), '自然流量', '华东'),
  (10009, DATE_SUB(NOW(), INTERVAL 22 DAY), '广告投放', '华北'),
  (10010, DATE_SUB(NOW(), INTERVAL 21 DAY), '应用商店', '华东'),
  (10011, DATE_SUB(NOW(), INTERVAL 20 DAY), '自然流量', '华南'),
  (10012, DATE_SUB(NOW(), INTERVAL 19 DAY), '广告投放', '西南'),
  (10013, DATE_SUB(NOW(), INTERVAL 18 DAY), '自然流量', '华东'),
  (10014, DATE_SUB(NOW(), INTERVAL 17 DAY), '应用商店', '华北'),
  (10015, DATE_SUB(NOW(), INTERVAL 16 DAY), '广告投放', '华东'),
  (10016, DATE_SUB(NOW(), INTERVAL 15 DAY), '自然流量', '华南'),
  (10017, DATE_SUB(NOW(), INTERVAL 14 DAY), '自然流量', '华东'),
  (10018, DATE_SUB(NOW(), INTERVAL 13 DAY), '广告投放', '华北'),
  (10019, DATE_SUB(NOW(), INTERVAL 12 DAY), '应用商店', '西南'),
  (10020, DATE_SUB(NOW(), INTERVAL 11 DAY), '自然流量', '华东'),
  (10021, DATE_SUB(NOW(), INTERVAL 10 DAY), '广告投放', '华南'),
  (10022, DATE_SUB(NOW(), INTERVAL 9 DAY), '自然流量', '华东'),
  (10023, DATE_SUB(NOW(), INTERVAL 8 DAY), '应用商店', '华北'),
  (10024, DATE_SUB(NOW(), INTERVAL 7 DAY), '自然流量', '华东'),
  (10025, DATE_SUB(NOW(), INTERVAL 6 DAY), '广告投放', '华南'),
  (10026, DATE_SUB(NOW(), INTERVAL 5 DAY), '应用商店', '华东'),
  (10027, DATE_SUB(NOW(), INTERVAL 4 DAY), '自然流量', '西南'),
  (10028, DATE_SUB(NOW(), INTERVAL 3 DAY), '广告投放', '华东'),
  (10029, DATE_SUB(NOW(), INTERVAL 2 DAY), '自然流量', '华北'),
  (10030, DATE_SUB(NOW(), INTERVAL 1 DAY), '应用商店', '华东'),
  (10031, NOW(), '自然流量', '华南'),
  (10032, NOW(), '广告投放', '华东');

INSERT INTO datasource_config (id, name, db_type, host, port, database_name, username, password_encrypt, biz_domain, status)
VALUES (1, '本地示例 MySQL 业务库', 'mysql', 'localhost', 3306, 'text2sql_meta', 'text2sql', 'text2sql123', 'user', 1)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  db_type = VALUES(db_type),
  host = VALUES(host),
  port = VALUES(port),
  database_name = VALUES(database_name),
  username = VALUES(username),
  password_encrypt = VALUES(password_encrypt),
  biz_domain = VALUES(biz_domain),
  status = VALUES(status);

INSERT INTO table_metadata (datasource_id, table_name, table_comment, biz_name, biz_description, is_enabled, schema_version)
SELECT 1, 'user_info', '示例用户信息表', '用户信息', '用于第一阶段 MVP 的用户注册分析示例表', 1, 'v1'
WHERE NOT EXISTS (
  SELECT 1 FROM table_metadata WHERE datasource_id = 1 AND table_name = 'user_info'
);

INSERT INTO column_metadata (table_id, column_name, column_type, column_comment, biz_name, biz_description, example_value, is_sensitive, is_queryable, is_filterable, is_aggregatable)
SELECT t.id, c.column_name, c.column_type, c.column_comment, c.biz_name, c.biz_description, c.example_value, 0, 1, 1, c.is_aggregatable
FROM table_metadata t
JOIN (
  SELECT 'user_id' AS column_name, 'BIGINT' AS column_type, '用户唯一 ID' AS column_comment, '用户 ID' AS biz_name, '用户唯一标识' AS biz_description, '10001' AS example_value, 1 AS is_aggregatable
  UNION ALL SELECT 'register_time', 'DATETIME', '注册时间', '注册时间', '用户完成注册的时间', '2026-04-01 10:00:00', 0
  UNION ALL SELECT 'channel', 'VARCHAR', '注册渠道', '渠道', '用户来源渠道', '自然流量', 0
  UNION ALL SELECT 'region', 'VARCHAR', '地区', '地区', '用户所属地区', '华东', 0
) c ON 1 = 1
WHERE t.datasource_id = 1
  AND t.table_name = 'user_info'
  AND NOT EXISTS (
    SELECT 1 FROM column_metadata cm WHERE cm.table_id = t.id AND cm.column_name = c.column_name
  );

INSERT INTO table_relation (datasource_id, source_table, source_column, target_table, target_column, relation_type, description)
SELECT 1, 'user_info', 'user_id', 'query_history', 'user_id', 'one_to_many', '用户信息与查询历史按用户 ID 关联'
WHERE NOT EXISTS (
  SELECT 1 FROM table_relation
  WHERE datasource_id = 1
    AND source_table = 'user_info'
    AND source_column = 'user_id'
    AND target_table = 'query_history'
    AND target_column = 'user_id'
);

INSERT INTO sql_example (question, question_pattern, sql_content, biz_domain, used_tables, used_fields, chart_type, quality_score, status)
VALUES (
  '统计最近30天每天新增用户数',
  '最近{n}天每天新增用户数',
  'SELECT DATE(register_time) AS stat_date, COUNT(DISTINCT user_id) AS new_user_count FROM user_info WHERE register_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) GROUP BY DATE(register_time) ORDER BY stat_date',
  'user',
  'user_info',
  'register_time,user_id',
  'line',
  100,
  1
);
