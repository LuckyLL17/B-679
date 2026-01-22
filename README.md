# 校园活动报名与签到系统 (Campus Event Management System)

一个基于 Spring Boot 和 Vue 3 的现代化校园活动管理平台，支持用户报名、取消报名、活动签到以及管理员后台管理。

## 📋 原始需求

> 我需要你给我写一个校园活动报名与签到系统，要求如下：
>
> 1. 此系统需要基于 SpringBoot + Vue 框架
> 2. 需要有两个角色，活动管理员和用户
> 3. 管理员能够对活动增删改查，用户可以对活动报名，到时间签到等等
> 4. 需要对功能进行补充完善
> 5. 交付的所有代码必须以能运行为基础
> 6. 主色调使用深红色（RGB：150, 30, 25），搭配白色和浅灰色。风格要求简洁、现代、大气，具有学术感。

## ✨ 核心功能

### 👤 普通用户 (User)

- **浏览活动**: 查看近期活动列表，支持卡片式预览。
- **活动详情**: 查看活动时间、地点、人数限制等详细信息。
- **在线报名**: 在规定时间内报名参加活动（实时校验人数限制）。
- **取消报名**: 在签到开始前可随时取消报名。
- **活动签到**: 仅在签到窗口期内开放，防止提前或过期签到。

### 🛠 管理员 (Admin)

- **活动管理**: 发布新活动、编辑现有活动、删除活动。
- **名单管理**: 查看任意活动的报名名单与签到状态。
- **数据统计**: 实时查看已报名人数和已签到人数。
- **权限隔离**: 独立的管理后台布局，普通用户无法访问。

## 🏗 技术栈

### 前端 (Frontend)

- **框架**: Vue 3 + Vite
- **语言**: JavaScript
- **路由**: Vue Router 4 (含路由守卫)
- **样式**: CSS Variables (深红色学术主题) + Flex/Grid 布局
- **交互**: 原生 Modal 弹窗、Toast 提示

### 后端 (Backend)

- **框架**: Spring Boot 3
- **语言**: Java 17
- **数据库**: PostgreSQL 15
- **ORM**: Spring Data JPA
- **安全**: Spring Security (基于 Session/Cookie 的认证)

### 部署 (DevOps)

- **容器化**: Docker + Docker Compose
- **Server**: Nginx (前端静态资源托管 + 反向代理)

## 🛠 工程细节

为了确保系统的健壮性与可维护性，本项目在实现基础需求之上进行了以下深度的工程优化：

### 1. 前端架构优化

- **布局分离 (Layout Separation)**: 实现 `UserLayout` 与 `AdminLayout`，针对不同角色提供差异化的导航与视觉体验。
- **深红色学术主题**: 使用 CSS Variables 定义全局色彩系统 (`--primary-color: rgb(150, 30, 25)`), 确保全站视觉风格高度统一且符合学术氛围。
- **交互细节**: 实现表单实时验证（用户名/密码规则）、操作二次确认（删除活动）、加载状态反馈（Loading Spinner）以及 Toast 消息提示。
- **路由守卫**: 基于 Vue Router 的全局拦截器，防止未登录或无权限用户非法访问后台页面。

### 2. 后端健壮性设计

- **统一异常处理 (Global Exception Handling)**: 通过 `@ControllerAdvice` 捕获所有运行时异常，统一返回 JSON 格式错误信息。
- **中文化响应**: 所有 API 返回提示语（包括校验错误、业务异常）均已适配为中文。
- **数据完整性**: 手动处理级联删除逻辑（删除活动前自动清理关联的报名记录），防止数据库外键约束报错。
- **时间窗校验**: 严格校验报名时间与签到时间，防止逻辑漏洞。

### 3. 全栈容器化

- **前端 Docker 化**: 使用 Nginx 容器托管 Vue 构建产物，并配置反向代理解决跨域问题。
- **一键编排**: 提供 `docker-compose.yml`，一条命令即可拉起 前端 + 后端 + 数据库 三个服务，实现开箱即用。

## 🚀 快速开始

### 1. 环境准备

- Docker & Docker Compose
- Java 17+ (开发环境)
- Node.js 18+ (开发环境)

### 2. 启动服务 (推荐)

使用 Docker Compose 一键启动所有服务（前端 + 后端 + 数据库）：

```bash
docker-compose up
```

启动成功后访问：

- **前端首页**: `http://localhost`
- **默认管理员账号**: `admin` / `admin123`
- **默认用户账号**: `student1` / `123456`

### 3. 本地开发运行

**后端**:

```bash
cd backend
./mvnw spring-boot:run
```

**前端**:

```bash
cd frontend
pnpm install
pnpm dev
```

## 📂 项目结构

```
.
├── backend/            # Spring Boot 后端项目
│   ├── src/main/java/  # Java 源代码
│   └── Dockerfile      # 后端容器构建文件
├── frontend/           # Vue 3 前端项目
│   ├── src/views/      # 页面组件 (Admin/User/Auth)
│   ├── nginx.conf      # Nginx 配置
│   └── Dockerfile      # 前端容器构建文件
├── docker-compose.yml  # 服务编排配置
└── README.md           # 项目文档
```

## 🔒 权限设计

| 角色      | 权限               | 访问范围              |
| --------- | ------------------ | --------------------- |
| **User**  | 报名、签到、浏览   | `/user/*`             |
| **Admin** | 活动CRUD、名单管理 | `/admin/*`, `/user/*` |
