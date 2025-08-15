${projectPrefix}:
  database:
<#if pg.seata?? && pg.seata>
    isSeata: true
</#if>
#    initDatabasePrefix:
#      - ${projectPrefix}_base

server:
  port: ${pg.serverPort?c}

springdoc:
  group-configs:
    - group: '${serviceName}'
      displayName: '${serviceName}服务'
      paths-to-match: '/**'
      packages-to-scan: ${pg.parent}.${moduleName}.controller

## 请在nacos中新建一个名为: ${projectPrefix}-${serviceName}-server.yml 的配置文件，并将： ${projectPrefix}-${serviceName}-server/src/main/resources/application.yml 配置文件的内容移动过去
## 然后在项目文件中，删除此文件！！！
