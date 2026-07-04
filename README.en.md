<p align="center">
  <img width="350px" height="150px" src="preview/hula.png" />
</p>

<p align="center">An instant messaging system server built with Spring Cloud, Spring Boot 4, Netty, MyBatis-Plus and RocketMQ</p>

<div align="center">
  <img src="https://img.shields.io/badge/Spring%20Cloud-2025.1.2-6DB33F?style=flat-square&logo=spring&logoColor=white">
  <img src="https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?style=flat-square&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/Netty-4.2.15.Final-343434?style=flat-square&logo=netty&logoColor=white">
  <img src="https://img.shields.io/badge/MyBatis--Plus-3.5.16-00A1E9?style=flat-square&logo=mybatis&logoColor=white">
  <img src="https://img.shields.io/badge/RocketMQ%20Spring-2.3.6-D77310?style=flat-square&logo=apacherocketmq&logoColor=white">
  <img src="https://img.shields.io/badge/Redisson-4.6.1-DC382D?style=flat-square&logo=redis&logoColor=white">
  <img src="https://img.shields.io/badge/MySQL-8.0.30-4479A1?style=flat-square&logo=mysql&logoColor=white">
  <img src="https://img.shields.io/badge/WebSocket-Reactive-010101?style=flat-square&logo=websocket&logoColor=white">
  <img src="https://img.shields.io/badge/Java-25-ED8B00?style=flat-square&logo=openjdk&logoColor=white">
  <img src="https://img.shields.io/badge/Maven-3.9.16-C71A36?style=flat-square&logo=apachemaven&logoColor=white">
</div>

<p align="center">
  gitee：<a href="https://gitee.com/HulaSpark/HuLa-Server/stargazers"><img src="https://gitee.com/HulaSpark/HuLa-Server/badge/star.svg?theme=gvp" alt="star"></a>
  github：<a href="https://github.com/HulaSpark/HuLa-Server/stargazers"><img src="https://img.shields.io/github/stars/HulaSpark/HuLa-Server" alt="star"></a>
  gitcode：<a href="https://gitcode.com/HuLaSpark/HuLa-Server"><img src="https://gitcode.com/HuLaSpark/HuLa-Server/star/badge.svg" alt="star"></a>
</p>
<p align="center">
  WeChat: <img src="https://img.shields.io/badge/cy2439646234-07C160?logo=wechat&logoColor=fff">
</p>

<p align="center">
  🖥️ Client：<a href="https://github.com/HulaSpark/HuLa">github HuLa</a> | <a href="https://gitee.com/HulaSpark/HuLa">gitee HuLa</a>
</p>

<p align="center">English | <a href="README.md">中文</a></p>

## Project Introduction

HuLa-Server is a high-performance instant messaging system server built with Spring Cloud, Spring Boot 4, Netty, MyBatis-Plus and RocketMQ. It adopts a microservice architecture design, providing high-performance real-time communication capabilities, supporting core features such as private chat, group chat, and message push. The system has high scalability and reliability, suitable for various instant messaging scenarios.

## Technology Stack

- **Spring Boot 4.1.0**: Core application framework for WebMVC, WebFlux, Actuator and other service capabilities.

- **Spring Cloud 2025.1.2 / Spring Cloud Alibaba 2025.1.0.0**: Microservice foundation for service governance, discovery, configuration and gateway integration.

- **Java 25**: Current compile and runtime baseline, controlled by Maven through `maven.compiler.release=25`.

- **Netty**: A high-performance asynchronous event-driven network application framework, used to implement WebSocket long connection services, ensuring message real-time and reliability. Netty's high concurrent processing capability and excellent network programming model enable the system to support a large number of simultaneous online clients.

- **MyBatis-Plus**: Provides powerful enhancement functions and plugins for MyBatis, simplifies database operations, provides code generator, pagination plugin, performance analysis, and other features, greatly improving development efficiency.

- **RocketMQ**: A distributed message middleware, used for handling asynchronous communication between systems, supporting reliable message delivery, sequential messages, transaction messages, and other features, ensuring system scalability and decoupling.

- **Redis**: A high-performance in-memory database, used to store user session information, message cache, and other data, providing high-speed data access capabilities.

- **MySQL**: A reliable relational database, used to store user information, message records, and other persistent data.

- **WebSocket**: Implements full-duplex communication between client and server, supporting real-time message push.

## Client Preview

![img.png](preview/img.png)

![img_1.png](preview/img_1.png)

![img_2.png](preview/img_2.png)

![img_3.png](preview/img_3.png)

<div style="padding: 28px; display: inline-block;">
  <img src="preview/img_4.png" alt="img_4.png" style="border-radius: 8px; display: block;"  />
</div>

<div style="padding: 28px; display: inline-block;">
  <img src="preview/img_5.png" alt="img_5.png" style="border-radius: 8px; display: block;"  />
</div>

<div style="padding: 28px; display: inline-block;">
  <img src="preview/img_6.png" alt="img_6.png" style="border-radius: 8px; display: block;"  />
</div>

<div style="padding: 28px; display: inline-block;">
  <img src="preview/img_7.png" alt="img_7.png" style="border-radius: 8px; display: block;"  />
</div>

<div style="padding: 28px; display: inline-block;">
  <img src="preview/img_8.png" alt="img_8.png" style="border-radius: 8px; display: block;"  />
</div>

## Core Features

- Instant Messaging: Supports basic communication functions such as private chat, group chat, and message push
- Message Management: Supports message storage, history query, message recall, and other functions
- User System: Provides user registration, login, personal information management, and other functions
- Group Management: Supports group creation, member management, group announcements, and other functions
- Friend System: Supports friend adding, deletion, grouping, and other functions
- Message Notification: Supports offline messages, system notifications, and other functions
- Moments: Supports moments posting, liking, commenting, sharing, and other functions

Under continuous development...

## Thanks to all contributors!

<a href="https://github.com/HuLaSpark/HuLa-Server/graphs/contributors">
  <img src="https://opencollective.com/HuLaSpark/contributors.svg?width=890" />
</a>

## Disclaimer

1. This project is provided as an open-source project, and the developer does not provide any express or implied warranties for the functionality, security, or suitability of the software within the scope permitted by law
2. Users expressly understand and agree that the use of this software is entirely at their own risk, and the software is provided on an "as is" and "as available" basis. The developer provides no warranties of any kind, whether express or implied, including but not limited to warranties of merchantability, fitness for a particular purpose, and non-infringement
3. In no event shall the developer or its suppliers be liable for any direct, indirect, incidental, special, punitive, or consequential damages, including but not limited to loss of profits, business interruption, personal information leakage, or other commercial damages or losses arising from the use of this software
4. All users who conduct secondary development on this project must commit to using this software for legal purposes and are responsible for complying with local laws and regulations
5. The developer reserves the right to modify the software's features or characteristics, as well as any part of this disclaimer at any time, and these modifications may be reflected in software updates

**The final interpretation right of this disclaimer belongs to the developer**

## Sponsor HuLa
If you find HuLa helpful, welcome to sponsor HuLa. Your support is our motivation to keep moving forward

<div style="display: flex;">
<img src="preview/zs.jpg" width="260" height="280" alt="赞助码" style="border-radius: 12px;" />

<img src="preview/zfb.png" width="260" height="280" alt="赞助码" style="border-radius: 12px; margin-left: 40px" />
</div>

## HuLa Community Discussion Group
<img src="preview/wx.png" width="260" height="300" alt="微信群二维码" style="border-radius: 12px;" />

## Sponsor List
Thanks to the following sponsors for their support!

### 🔐 User Authentication System
| Feature | Description | Status |
|---------|-------------|--------|
| 🔑 | Username/Password Login | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 📱 | QR Code Scan Login | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 💻 | Multi-device Login Management | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |

## References and Acknowledgments
- This project framework draws inspiration from lamp-cloud in its functionality or design. We hereby express our gratitude and acknowledge the source.
- project address：https://github.com/dromara/lamp-cloud
- If you have borrowed or learned from the source code of this project, please clearly indicate the reference and include the project address.

### 💬 Message Communication
| Feature | Description | Status |
|---------|-------------|--------|
| 👤 | One-on-one Private Chat | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 👥 | Group Chat | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| ↩️ | Message Recall | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 📢 | @Mention & Reply Features | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 👁️ | Message Read Status | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 😊 | Emoji Features | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 🖱️ | Message Right-click Menu | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 🔗 | Link Preview Cards | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 👍 | Message Like Interaction | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 📔 | Chat History Management | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |

### 🤝 Social Management
| Feature | Description | Status |
|---------|-------------|--------|
| ➕ | Add & Remove Friends | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 🔍 | Friend Search | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 🏢 | Group Creation & Management | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 🟢 | Friend Online Status | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 🎖️ | Friend Badge System | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 🚫 | Block & Do Not Disturb | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 📤 | Message Forwarding | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 📋 | Group Announcements | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 🏷️ | Nickname & Remark Management | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 📍 | Get and Send Location | ![In Progress](https://img.shields.io/badge/🐣-进行中-ee9f20?style=flat&labelColor=fef7e6&color=ee9f20) |

### 🎨 User Experience
| Feature | Description | Status |
|---------|-------------|--------|
| 🖼️ | Modern UI Design | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 🌙 | Dark & Light Theme | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 🎭 | Skin Theme Switching | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |

### 🛠️ System Features
| Feature | Description | Status |
|---------|-------------|--------|
| 🪟 | Multi-window Management | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 🔔 | System Tray Notifications | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 📷 | Image Viewer | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| ✂️ | Screenshot Feature | ![In Progress](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 📁 | File Upload (Qiniu Cloud) | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 🔄 | Auto-update System | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |

### 🌐 Cross-platform Support
| Feature | Description | Status |
|---------|-------------|--------|
| 💻 | Windows/macOS/Linux | ![Completed](https://img.shields.io/badge/✅-Completed-008080?style=flat&labelColor=e6f7f7&color=008080) |
| 📱 | iOS/Android Adaptation | ![In Progress](https://img.shields.io/badge/🐣-In_Progress-ee9f20?style=flat&labelColor=fef7e6&color=ee9f20) |

### 🤖 AI Integration
| Feature | Description | Status |
|---------|-------------|--------|
| 🧠 | AI Chat Assistant | ![In Progress](https://img.shields.io/badge/🐣-In_Progress-ee9f20?style=flat&labelColor=fef7e6&color=ee9f20) |
| 🔌 | Multi-platform AI Support | ![In Progress](https://img.shields.io/badge/🐣-In_Progress-ee9f20?style=flat&labelColor=fef7e6&color=ee9f20) |

## 👏 Thanks to all Contributors!

> Note: This list is manually updated. If you have sponsored but are not shown in the list, please contact us through:
 1. Submit an Issue on GitHub
 2. Send an email to: 2439646234@qq.com
 3. Contact via WeChat: cy2439646234
