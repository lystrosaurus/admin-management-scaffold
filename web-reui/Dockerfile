# web-reui 多阶段构建
# 阶段一：node 镜像内执行 tsc + vite build 产出 dist/
# 阶段二：nginx 镜像托管静态资源并代理 /api 到后端
#
# 构建：  docker build -t admin-web-reui:1.0.0 .
# 运行：  docker run -d --name admin-web -p 8080:80 admin-web-reui:1.0.0
#         （后端需可达，默认经 docker network 以服务名 backend:8080 访问，
#           可用 --add-host backend:host-gateway 指向宿主机后端）

# ---------- 构建阶段 ----------
FROM node:22-alpine AS builder

WORKDIR /app

# 依赖锁定文件优先拷贝，最大化利用镜像层缓存
COPY package.json package-lock.json* ./
RUN npm ci --no-audit --no-fund

# 源码与配置
COPY tsconfig.json tsconfig.app.json tsconfig.node.json ./
COPY vite.config.ts index.html ./
COPY src ./src
COPY public ./public

# 生产构建（tsc -b && vite build）
RUN npm run build

# ---------- 运行阶段 ----------
FROM nginx:1.27-alpine AS runner

# 精简 nginx 默认配置，替换为 SPA + API 代理配置
COPY deploy/nginx.conf /etc/nginx/conf.d/default.conf

# 构建产物
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

# 容器探活
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget -qO- http://127.0.0.1/healthz >/dev/null 2>&1 || exit 1

CMD ["nginx", "-g", "daemon off;"]
