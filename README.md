# 项目索引

这里整理我公开维护的工具，以及其他工程和课程实践。每个项目的代码、运行说明和限制以对应仓库为准。

## 本地工具

### [Nai学长工作室](https://github.com/h1neolzr7f/NaiXueZhang-Studio-Upgrade)

把本地图库、提示词、批量生成和后处理放在同一个工作台中。后端使用 Python、FastAPI 和 SQLite，工作区使用 TypeScript。

项目中值得查看的部分是图库检索、任务状态持久化和付费生成的失败处理。任务入队时固定参数，对扣费结果未知的任务不自动重试；具体规则见项目 README。

[源码与说明](https://github.com/h1neolzr7f/NaiXueZhang-Studio-Upgrade) · [Windows 下载](https://github.com/h1neolzr7f/NaiXueZhang-Studio-Upgrade/releases) · [升级说明](https://github.com/h1neolzr7f/NaiXueZhang-Studio-Upgrade/blob/main/docs/UPGRADE.md)

### [丁真笔记本](https://github.com/h1neolzr7f/dingzhen-notebook)

将已经完成、且有权查看的试卷整理为本地错题本，包含采集、OCR、校对、复习和组卷。桌面端使用 Python，提供 Android 采集端。

题目缺少官方答案、解析或必要证据时进入待校对，AI 不覆盖官方字段。仓库提供合成回归数据和 pytest 测试。Android APK 目前使用 debug 证书，适合个人侧载。

[源码与说明](https://github.com/h1neolzr7f/dingzhen-notebook) · [Windows / Android 下载](https://github.com/h1neolzr7f/dingzhen-notebook/releases) · [测试工作流](https://github.com/h1neolzr7f/dingzhen-notebook/blob/main/.github/workflows/tests.yml)

### [Manga Editor Desu · NAI](https://github.com/h1neolzr7f/Manga-Editor-Desu-NAI)

基于 [new-sankaku/manga-editor-desu](https://github.com/new-sankaku/manga-editor-desu) 的 GPL-3.0 修改发行版。分镜、气泡、图层和多页工程来自上游；本版本增加 NovelAI 接入、Windows 启动器、模拟器工作区和可选本地抠图等功能。

[源码与归属说明](https://github.com/h1neolzr7f/Manga-Editor-Desu-NAI) · [Windows 下载](https://github.com/h1neolzr7f/Manga-Editor-Desu-NAI/releases) · [相对上游的改动](https://github.com/h1neolzr7f/Manga-Editor-Desu-NAI/blob/main/CHANGELOG.md)

这些工具均为非官方项目，与所涉及的第三方平台没有隶属关系。使用范围和第三方内容权利请参阅各项目说明。

## 其他工程与课程实践

| 项目 | 内容 | 主要技术 |
| --- | --- | --- |
| [智能鱼缸](./projects/smart-aquarium) | 水质监测、自动投喂、换水与 MQTT 上报 | C、STM32、ESP8266 |
| [工地安全识别](https://github.com/h1neolzr7f/smart-site-safety) | 安全帽检测、姿态规则判断与预警记录 | Python、YOLO、OpenCV、Streamlit |
| [体育馆预约](https://github.com/h1neolzr7f/campus-sport) | 场地预约、器材借还和管理端报表 | Spring Boot、MySQL、Vue 3 |
| [学生管理](https://github.com/h1neolzr7f/student-manager) | Java 课程实验，桌面端信息管理 | Java、Swing、JDBC |
| [樱夜·尸潮](https://github.com/h1neolzr7f/sakurayo-zombietide) | 离线 Roguelite 射击游戏 | Canvas、JavaScript、Android WebView |

更多项目说明见[项目经历](./项目经历.md)。

## 仓库与运行数据

本仓库保存智能鱼缸代码，其余项目在独立仓库维护。Nai学长工作室的当前维护线为 `NaiXueZhang-Studio-Upgrade`，v1.4 保留在 `NaiXueZhang-Studio`；樱夜·尸潮的玩家入口为 `sakurayo-zombietide`，开发仓为 `sakurayo-v46-handoff`。

公开署名使用 GitHub 账号。仓库不收录真实身份信息、设备密钥、私人运行数据和完整训练数据集。演示账号只用于本地运行，部署前请修改配置，不要提交真实口令。
