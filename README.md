<p align="center">
  <img width="350px" height="150px" src="preview/hula.png" />
</p>

<p align="center">一款基于SpringCloud、SpringBoot3、Netty、MyBatis-Plus和RocketMQ构建的即时通讯系统服务端</p>

<div align="center">
  <img src="https://img.shields.io/badge/Spring_Cloud-2024-blue?logo=springcloud&logoColor=white">
  <img src="https://img.shields.io/badge/spring-boot3-brightgreen?logo=spring">
  <img src="https://img.shields.io/badge/Netty-343434?logo=netty&logoColor=white">
  <img src="https://img.shields.io/badge/MyBatis--Plus-00A1E9?logo=mybatis&logoColor=white">
  <img src="https://img.shields.io/badge/RocketMQ-D77310?logo=apacherocketmq&logoColor=white">
  <img src="https://img.shields.io/badge/Redis-DC382D?logo=redis&logoColor=white">
  <img src="https://img.shields.io/badge/MySQL-4479A1?logo=mysql&logoColor=white">
  <img src="https://img.shields.io/badge/WebSocket-010101?logo=websocket&logoColor=white">
  <img src="https://img.shields.io/badge/Java21-FF0000?logo=openjdk&logoColor=white">
  <img src="https://img.shields.io/badge/Maven-C71A36?logo=apachemaven&logoColor=white">
</div>

<p align="center">
  gitee：<a href="https://gitee.com/HulaSpark/HuLa-Server/stargazers"><img src="https://gitee.com/HulaSpark/HuLa-Server/badge/star.svg?theme=gvp" alt="star"></a>
  github：<a href="https://github.com/HulaSpark/HuLa-Server/stargazers"><img src="https://img.shields.io/github/stars/HulaSpark/HuLa-Server" alt="star"></a>
  gitcode：<a href="https://gitcode.com/HuLaSpark/HuLa-Server"><img src="https://gitcode.com/HuLaSpark/HuLa-Server/star/badge.svg" alt="star"></a>
</p>
<p align="center">
  微信: <img src="https://img.shields.io/badge/cy2439646234-07C160?logo=wechat&logoColor=fff">
</p>

<p align="center">
  🖥️ 客户端：<a href="https://github.com/HuLaSpark/HuLa-Server">github HuLa-Server</a> | <a href="https://gitee.com/HulaSpark/HuLa-Server">gitee HuLa-Server</a>
</p>

<p align="center"><a href="README.en.md">English</a> | 中文</p>

## 项目介绍

HuLa-Server 是一款基于 SpringCloud、SpringBoot3、Netty、MyBatis-Plus 和 RocketMQ 构建的高性能即时通讯系统服务端。它采用了微服务架构设计，提供高性能的实时通信能力，支持单聊、群聊、消息推送等核心功能。系统具有高可扩展性和可靠性，适用于各类即时通讯场景。

## 核心优势

- **模块化与高内聚**: 服务按功能拆分为独立模块（网关、认证、IM、AI、ws、base、system、presence等），通过<modules>清晰隔离，降低耦合度，提升开发与维护效率。

- **弹性扩展能力**: webflux异步架构，基于Spring Cloud 2024 & Spring Boot 3.x构建，支持动态扩缩容。如luohuo-gateway可通过增加节点应对高并发流量。

- **技术栈统一管理**: luohuo-dependencies-parent集中管控依赖版本，避免冲突，提升协作效率。

## 技术栈

- **Redis**: 高性能的内存数据库，用于存储用户会话信息、消息缓存等数据，提供了高速的数据访问能力。

- **MySQL**: 可靠的关系型数据库，用于存储用户信息、消息记录等持久化数据。

- **Netty**: Reactor 线程模型，高并发连接管理，零拷贝优化，支持实时消息推送。

- **RocketMQ**: 高性能消息中间件，各项服务之间解耦的关键，im场景下实现事务消息保障、顺序消费

## 全链路分布式能力

- **网关层**：luohuo-gateway实现路由鉴权，支持OAuth2.0安全认证，SA-Token权限框架 + XSS过滤（luohuo-xss-starter）保障系统安全。

- **通信层**：WebFlux + Netty 异步模型，基于Reactor响应式编程模型，用户在线状态指纹级别映射，实时消息推送低延迟。

- **数据层**：MyBatis-Plus + Dynamic Datasource支持多租户分库分表。

## AI与IM深度集成

• 集成Spring AI（deepseek、OpenAI、通义千问等），luohuo-ai-biz调用ai服务，提供调用第三方ai、工作流（TinyFlow）能力，扩展业务场景。

## 客户端预览

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

## 核心功能

- 即时通讯：支持单聊、群聊、消息推送等基础通讯功能
- 消息管理：支持消息存储、历史记录查询、消息撤回等功能
- 用户系统：提供用户注册、登录、个人信息管理等功能
- 群组管理：支持群组创建、成员管理、群公告等功能
- 好友系统：支持好友添加、删除、分组等功能
- 消息通知：支持离线消息、系统通知等功能
- 朋友圈：支持朋友圈发布、点赞、评论、转发等功能

持续开发中...

## 免责声明

1. 本项目是作为一款开源项目提供的，开发者在法律允许的范围内不对软件的功能性、安全性或适用性提供任何形式的明示或暗示的保证
2. 用户明确理解并同意，使用本软件的风险完全由用户自己承担，软件以"现状"和"现有"基础提供。开发者不提供任何形式的担保，无论是明示还是暗示的，包括但不限于适销性、特定用途的适用性和非侵权的担保
3. 在任何情况下，开发者或其供应商都不对任何直接的、间接的、偶然的、特殊的、惩罚性的或后果性的损害承担责任，包括但不限于使用本软件产生的利润损失、业务中断、个人信息泄露或其他商业损害或损失
4. 所有在本项目上进行二次开发的用户，都需承诺将本软件用于合法目的，并自行负责遵守当地的法律和法规
5. 开发者有权在任何时间修改软件的功能或特性，以及本免责声明的任何部分，并且这些修改可能会以软件更新的形式体现

**本免责声明的最终解释权归开发者所有**

## 赞助HuLa
如果您觉得HuLa对您有帮助，欢迎赞助HuLa，您的支持是我们不断前进的动力

<div style="display: flex;">
<img src="preview/zs.jpg" width="260" height="280" alt="赞助码" style="border-radius: 12px;" />

<img src="preview/zfb.png" width="260" height="280" alt="赞助码" style="border-radius: 12px; margin-left: 40px" />
</div>


## 💬 加入社区

<div align="center">
  <h3>🤝 HuLa 社区讨论群</h3>
  <p><em>与开发者和用户一起交流讨论，获取最新资讯和技术支持</em></p>

  <div>
    <img src="preview/wx.png" width="220" height="260" alt="微信群二维码">
  </div>

  <p><strong>📱 扫码加入微信讨论群</strong></p>
</div>

## 🙏 感谢赞助者

<div align="center">
  <h3>💎 贡献者荣誉榜</h3>
  <p><em>感谢以下朋友对 HuLa 项目的慷慨支持！</em></p>
</div>

### 🏆 金牌赞助者 (￥100+)
| 💝 日期 | 👤 赞助者 | 💰 金额 | 🏷️ 平台 |
|---------|----------|--------|---------|
| 2025-04-25 | **上官俊斌** | `¥200` | ![微信赞赏](https://img.shields.io/badge/微信赞赏-07C160?style=flat&logo=wechat&logoColor=white) |
| 2025-05-27 | **临安居士** | `¥188` | ![微信赞赏](https://img.shields.io/badge/微信赞赏-07C160?style=flat&logo=wechat&logoColor=white) |
| 2025-04-20 | **姜兴(Simon)** | `¥188` | ![微信赞赏](https://img.shields.io/badge/微信赞赏-07C160?style=flat&logo=wechat&logoColor=white) |
| 2025-02-17 | **禾硕** | `¥168` | ![支付宝赞赏](https://img.shields.io/badge/支付宝赞赏-1677FF?style=flat&logo=alipay&logoColor=white) |
| 2025-08-13 | **zhongjing** | `¥100` | ![微信赞赏](https://img.shields.io/badge/微信赞赏-07C160?style=flat&logo=wechat&logoColor=white) |
| 2025-07-15 | **粉兔** | `¥100` | ![微信赞赏](https://img.shields.io/badge/微信赞赏-07C160?style=flat&logo=wechat&logoColor=white) |
| 2025-02-8 | **Boom....** | `¥100` | ![微信赞赏](https://img.shields.io/badge/微信赞赏-07C160?style=flat&logo=wechat&logoColor=white) |

### 🥈 银牌赞助者 (￥50-99)
| 💝 日期 | 👤 赞助者 | 💰 金额 | 🏷️ 平台 |
|---------|----------|--------|---------|
| 2025-06-26 | **m udDy🐖** | `¥88` | ![微信赞赏](https://img.shields.io/badge/微信赞赏-07C160?style=flat&logo=wechat&logoColor=white) |
| 2025-05-09 | **犹豫，就会败北。** | `¥88` | ![微信赞赏](https://img.shields.io/badge/微信赞赏-07C160?style=flat&logo=wechat&logoColor=white) |
| 2025-04-01 | **墨** | `¥88.88` | ![微信赞赏](https://img.shields.io/badge/微信赞赏-07C160?style=flat&logo=wechat&logoColor=white) |
| 2025-02-8 | **邓伟** | `¥88` | ![微信赞赏](https://img.shields.io/badge/微信赞赏-07C160?style=flat&logo=wechat&logoColor=white) |
| 2025-02-7 | **dennis** | `¥80` | ![Gitee赞赏](https://img.shields.io/badge/Gitee赞赏-C71D23?style=flat&logo=gitee&logoColor=white) |
| 2025-02-6 | **小二** | `¥62` | ![微信赞赏](https://img.shields.io/badge/微信赞赏-07C160?style=flat&logo=wechat&logoColor=white) |
| 2025-05-15 | **孤鸿影** | `¥56` | ![微信赞赏](https://img.shields.io/badge/微信赞赏-07C160?style=flat&logo=wechat&logoColor=white) |

### 🥉 铜牌赞助者 (￥20-49)
| 💝 日期 | 👤 赞助者 | 💰 金额 | 🏷️ 平台 |
|---------|----------|--------|---------|
| 2025-08-12 | ***持** | `¥20` | ![支付宝赞赏](https://img.shields.io/badge/支付宝赞赏-1677FF?style=flat&logo=alipay&logoColor=white) |
| 2025-06-03 | **洪流** | `¥20` | ![微信赞赏](https://img.shields.io/badge/微信赞赏-07C160?style=flat&logo=wechat&logoColor=white) |
| 2025-05-27 | **刘启成** | `¥20` | ![微信赞赏](https://img.shields.io/badge/微信赞赏-07C160?style=flat&logo=wechat&logoColor=white) |
| 2025-05-20 | **匿名赞助者** | `¥20` | ![微信赞赏](https://img.shields.io/badge/微信赞赏-07C160?style=flat&logo=wechat&logoColor=white) |

<div align="center">
  <br>

> 📝 **温馨提示**
> 该名单为手动更新，如果您已赞助但未在列表中，请联系我们：
> 🐛 [GitHub Issue](https://github.com/HuLaSpark/HuLa-Server/issues) | 📧 邮箱: `2439646234@qq.com` | 💬 微信: `cy2439646234`

  <br>
</div>

---

## 📄 开源许可

<div align="center">
  <h3>⚖️ 许可证信息</h3>

  <p>
    <a href="https://app.fossa.com/projects/git%2Bgithub.com%2FHuLaSpark%2FHuLa?ref=badge_large">
      <img src="https://app.fossa.com/api/projects/git%2Bgithub.com%2FHuLaSpark%2FHuLa.svg?type=large" alt="FOSSA Status" style="max-width: 100%; border-radius: 8px;">
    </a>
  </p>

  <p><em>本项目遵循开源许可协议，详细信息请查看上方许可证报告</em></p>
</div>

---

<div align="center">
  <h3>🌟 感谢您的关注</h3>
  <p>
    <em>如果您觉得 HuLa 有价值，请给我们一个 ⭐ Star，这是对我们最大的鼓励！</em>
  </p>
  <p>
    <strong>让我们一起构建更好的即时通讯体验 🚀</strong>
  </p>
</div>
