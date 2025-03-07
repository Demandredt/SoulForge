## 文件同步

上传任务表 (upload_task)
```sql
CREATE TABLE upload_task (
    upload_id VARCHAR(255) PRIMARY KEY,
    bucket VARCHAR(255),
    object_key VARCHAR(255),
    status ENUM('IN_PROGRESS', 'COMPLETED', 'CANCELLED','FAILED'),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    final_etag VARCHAR(255)
);

```

上传分块表(upload_part)

```sql
CREATE TABLE upload_part (
    upload_id VARCHAR(255),
    part_number INT,
    etag VARCHAR(255),
    PRIMARY KEY (upload_id, part_number),
    FOREIGN KEY (upload_id) REFERENCES upload_task(upload_id)
);
```

