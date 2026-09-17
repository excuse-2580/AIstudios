# AIStudio 构建说明

## 环境要求

- **JDK 17+** - 必须安装 JDK 17 或更高版本
- **Android Studio Hedgehog (2023.1.1)** 或更高版本
- **Android SDK** - 确保已安装 Android SDK 34

## 快速开始

### 方法一：Android Studio 自动构建

1. 用 Android Studio 打开 `AIStudio` 文件夹
2. 等待 Gradle Sync 完成（会自动下载 `gradle-wrapper.jar`）
3. 点击 **Build → Build Bundle(s) / APK(s) → Build APK(s)**
4. APK 输出位置：`app/build/outputs/apk/debug/app-debug.apk`

### 方法二：命令行构建

```bash
cd AIStudio

# 首次需要下载 wrapper
./gradlew wrapper --gradle-version=8.5

# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK（需要签名配置）
./gradlew assembleRelease
```

## 缺少的文件

如果遇到 `gradle-wrapper.jar` 缺失的错误，手动下载：

```bash
# 下载 gradle-wrapper.jar
curl -L -o gradle/wrapper/gradle-wrapper.jar \
  https://github.com/gradle/gradle/raw/v8.5.0/gradle/wrapper/gradle-wrapper.jar
```

## 构建后配置

### 1. 配置 AI API Key

构建完成后，在 APP 内进入：**设置 → API Key 配置**

输入以下任一 API Key：
- **OpenAI**: 从 https://platform.openai.com 获取
- **Claude**: 从 https://console.anthropic.com 获取

### 2. 权限说明

首次使用时需要授权：
- 麦克风权限（语音通话）
- 相机权限（视频通话）
- 存储权限（保存文件）
- 通知权限（消息推送）

## 项目结构

```
AIStudio/
├── app/
│   ├── src/main/
│   │   ├── java/com/aistudio/app/
│   │   │   ├── domain/model/    # 数据模型
│   │   │   ├── data/           # 数据层（Repository, DAO, Remote）
│   │   │   ├── di/             # Hilt 依赖注入
│   │   │   ├── service/        # 后台服务
│   │   │   └── ui/             # UI 层（Compose）
│   │   └── res/                # 资源文件
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/wrapper/
```

## 功能模块

| 模块 | 状态 | 说明 |
|------|------|------|
| 智能体管理 | ✅ 完成 | 创建/编辑/删除智能体 |
| 对话功能 | ✅ 完成 | 单聊 + AI 回复（需配置 API Key） |
| 群聊功能 | ✅ 完成 | 群组聊天 |
| 语音通话 | 🟡 框架 | 需接入 WebRTC |
| 屏幕共享 | 🟡 框架 | 需接入 WebRTC |
| AI 接入 | ✅ 完成 | 支持 OpenAI / Claude |

## 已知限制

1. **AI 回复**：需要配置 API Key，否则使用模拟数据
2. **WebRTC**：通话和屏幕共享界面已完成，需对接实际信令服务器
3. **后端**：数据仅存储在本地 Room 数据库，无云同步

## 技术栈

- Kotlin 1.9.20
- Jetpack Compose (BOM 2023.10.01)
- Hilt 2.48.1
- Room 2.6.1
- Retrofit + OkHttp
- WebRTC (Stream)

---
*构建问题？欢迎反馈！*
