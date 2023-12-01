# 阿里云DDNS Java版使用教程
## 前言
```text
可将动态公网IP更新到阿里云域名解析服务器
```
## 编译运行环境
```text
jdk1.8,maven
```
## 克隆代码
```shell
git clone git@github.com:844704781/aliyun-ddns-client.git
```
## 编译
```shell
cd aliyun-ddns-client/
mvn clean package -DskipTests;
```
## 配置相关参数
```text
# 从阿里云控制台获取
aliyun.access_key.id= xxx
# 从阿里云控制台获取
aliyun.access_key.secret= xxx
# 获取方式: https://blog.csdn.net/ren991630622/article/details/77174574
aliyun.access_key.record_id= xxx
```
## 测试运行项目
```shell
cd target/
java -jar aliyun-ddns-client-1.0.jar
```

## 添加定时任务
```shell
# 打开定时任务配置文件
crontab -e
# 文件中新增cron表达式，代表一个小时候执行一次命令
0 * * * * java -jar /home/watermelon/workspace/ddns/aliyun-ddns-client-1.0.jar > /dev/null 2>&1
# 验证定时任务
crontab -l
```
