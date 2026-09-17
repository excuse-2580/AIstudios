# AI智能体工坊

一个功能丰富的AI智能体平台，支持创建、管理和与AI智能体对话。

## 功能特性

### 🏠 首页
- 快捷操作入口
- 最近使用的智能体
- 最近对话记录
- 数据统计

### 🤖 智能体管理
- 创建新智能体（名称、描述、头像）
- 设置智能体性格和专长
- 配置语音音色
- 调整AI参数（温度、最大token数）
- 智能体设定优化改进

### 💬 对话功能
- 与智能体进行文字对话
- 实时消息显示
- 消息状态追踪

### 👥 群聊功能
- 创建群聊邀请多个智能体
- 多智能体协作
- 群聊历史记录

### 📞 通话功能
- 语音通话
- 屏幕共享
- 静音/扬声器切换

### ⚙️ 设置
- 深色模式
- 通知管理
- 语音设置

## 技术架构

### Android端
- **语言**: Kotlin
- **UI框架**: Jetpack Compose
- **架构模式**: MVVM + Hilt依赖注入
- **数据库**: Room
- **网络**: Retrofit + OkHttp
- **实时通信**: WebRTC (语音通话/屏幕共享)
- **图片加载**: Coil

### Web端 (开发中)
- React + TypeScript
- 统一的API接口

### 后端 (开发中)
- Go + Gin
- PostgreSQL + Redis
- WebSocket实时通信

## 项目结构

```
AIStudio/
├── app/
│   ├── src/main/
│   │   ├── java/com/aistudio/app/
│   │   │   ├── data/           # 数据层
│   │   │   │   ├── local/      # 本地数据库
│   │   │   │   └── repository/ # 仓库
│   │   │   ├── domain/         # 领域层
│   │   │   │   └── model/      # 数据模型
│   │   │   ├── di/            # 依赖注入
│   │   │   ├── service/       # 服务
│   │   │   └── ui/            # 界面层
│   │   │       ├── components/ # UI组件
│   │   │       ├── navigation/# 导航
│   │   │       ├── screens/   # 页面
│   │   │       └── theme/     # 主题
│   │   └── res/              # 资源文件
│   └── build.gradle.kts
├── gradle/
└── build.gradle.kts
```

## 开发计划

- [x] 基础框架搭建
- [x] 智能体CRUD功能
- [x] 文字对话功能
- [ ] 群聊功能完善
- [ ] Web端开发
- [ ] 后端API开发
- [ ] WebRTC集成
- [ ] 语音通话功能
- [ ] 屏幕共享功能
- [ ] 云端同步

## License

MIT License
