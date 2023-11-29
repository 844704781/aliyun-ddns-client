# 阿里云Java版使用教程
## 克隆代码
git clone git@github.com:844704781/aliyun-ddns-client.git
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
## 运行项目
```shell
cd target/
java -jar aliyun-ddns-client-1.0.jar
```
