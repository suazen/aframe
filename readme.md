# aframe框架
**项目快速开发脚手架**

### 项目结构  

```yaml
aframe
--aframe-support 基础支撑包
----aframe-auth 鉴权模块（依赖web模块）
----aframe-core 基础模块
----aframe-web web模块（依赖core模块）
----aframe-system 系统模块
------aframe-system-core 系统基础模块（依赖core）
------aframe-system-service 系统服务模块（依赖system-core、web）
----aframe-generator 代码生成模块
--aframe-admin 后台模块（依赖auth、system-service)
--aframe-monitor 监控模块
--aframe-starter 启动模块
--aframe-module 业务模块
...
```


### 技术栈

- springboot 
- hutool
- mybatis-plus
- alibaba druid
- fastjson
- redisson
- sa-token