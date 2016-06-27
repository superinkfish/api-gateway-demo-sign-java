# api-gateway-demo-sign-java
阿里云API网关签名算法Demo（Java实现）

## 使用方法

- clone或下载本项目到本地；
- 根据实际情况修改`src\main\java\com\aliyun\api\gateway\demo\Main.java`或`src\test\java\com\aliyun\api\gateway\demo\Demo.java`。
- 编译并运行（或测试）。

## 注意事项

- API授权授权给应用（根据APP_ID），而不是APP_KEY；
- 运行时，调用方需要提供被调用方APP_KEY和APP_SECRET，而不是APP_ID。
- API提供了测试和生产两套环境，可在`com.aliyun.api.gateway.demo.Client`构造器中设置；
- 若出现超时，可以在`com.aliyun.api.gateway.demo.Request`中指定超时时间。
- `CUSTOM_HEADERS_TO_SIGN_PREFIX`一般情况下不需要设置，若要自定义参与签名的header时才需要进行自定义。

