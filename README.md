## apollo-spring-data ##

基于[apollo](https://github.com/ctripcorp/apollo)的spring数据源热更新服务，能够在不重启服务的情况下，进行数据源切换。

数据源切换服务已打包成springboot starter,详情可查看模块`spring-datasource`,`spring-redis`

目前数据源支持mysql和redis(需使用lettuce)

### 开发环境 ###
apollo: 1.7.1

springboot: 2.0.3.RELEASE

### 运行demo ###
1.在apollo中创建你的项目配置，并加入数据源相关配置属性

```
## redis config
#spring.redis.host=
#spring.redis.port=
#spring.redis.database=
#spring.redis.lettuce.pool.max-active=
#spring.redis.lettuce.pool.max-wait=
#spring.redis.lettuce.pool.min-idle=
#spring.redis.lettuce.pool.max-idle=1
#spring.redis.lettuce.shutdown-timeout=

## mysql config
#spring.datasource.url=
#spring.datasource.driver-class-name=
#spring.datasource.username=
#spring.datasource.password=
```

2.数据库表可导入script中的sql文件

3.修改spring-use-case模块配置`application.properties`中的关于apollo相关配置

```
# your project id
app.id=

# your apollo meta server host
apollo.meta=

# your apollo cluster
apollo.cluster=
```

4.启动spring-use-case



