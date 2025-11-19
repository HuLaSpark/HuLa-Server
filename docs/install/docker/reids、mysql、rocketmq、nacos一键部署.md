## 📝 修改环境配置

上传目录下的docker文件夹到服务器/home/docker下面, 需要修改env文件夹rocketmq文件夹里面的配置, 特别是[broker.conf](docker/rocketmq/broker/conf/broker.conf)里面brokerIP1的值

```bash
# 授权目录

mkdir /home/docker/rocketmq/broker/conf/ /home/docker/rocketmq/broker/logs/ /home/docker/rocketmq/broker/store/ -p

chmod 777 /home/docker/rocketmq/broker/conf/
chmod 777 /home/docker/rocketmq/broker/logs/
chmod 777 /home/docker/rocketmq/broker/store/
chmod 777 /home/docker/rocketmq/timerwheel/
```

## 🛠️ 启动命令
- **仔细阅读**: docker-compose.yml 的内容（redis的密码也在这里面设置的）、./env 文件夹下面的内容，里面包含了账号密码等信息
- **打开目录**: 当前文件夹下输入 cmd 回车
- **执行命令**: docker-compose up -d
- **导入nacos数据库**: [mysql-schema.sql](../mysql-schema.sql)
- **导入nacos命名空间数据**: [nacos_config_export_20251019194635.zip](../nacos/nacos_config_export_20251019194635.zip)