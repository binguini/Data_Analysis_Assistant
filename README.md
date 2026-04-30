# Text-to-SQL 数据分析助手项目骨架

本项目基于大模型的 Text-to-SQL 数据分析助手，提供一套可扩展的前后端分离项目架构，用于后续实现自然语言转 SQL、SQL 安全校验、查询执行、结果解释与可视化能力。

## 目录结构

- `backend/`：Spring Boot 后端服务
- `frontend/`：Vue 3 + Vite 前端应用
- `docker/`：本地开发所需的 MySQL、Redis、容器编排配置
- `docs/`：补充设计文档
- `基于大模型的Text-to-SQL数据分析助手技术方案.md`：技术方案原文

## 快速开始

### 1. 启动依赖环境

```bash
docker compose up -d
```

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run
```

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```


