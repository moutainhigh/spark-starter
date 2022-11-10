# Spark Email Starter

![](./logoly.pro.png)

> 开箱即用的邮件组件

我们在 `spark-starter-notify` 提供的接口之上, 实现邮件发送的服务, 只需要简单的配置即可同步或者异步发送邮件.

使用时请注意邮件的默认端口修改为了 **465**, 因为阿里云开放 **25** 端口贼麻烦.

## Table of Contents

- [Background](#background)
- [Install](#install)
- [Usage](#usage)
- [Maintainers](#maintainers)
- [Contributing](#contributing)
- [License](#license)

## Background

如果直接使用 `spring-boot-starter-mail` 组件, 配置较多且需要自己实现异步发送, 可直接使用 @Async 注解, 但这种方式不支持返回值,
因此我们使用了 `CompletableFuture` 来替代 Spring Boot 提供的异步方式,
且我们替换了默认端口 (25), 主要是防止部署到阿里服务器导致邮件发送失败的问题(阿里云服务器已默认关闭了 25 端口, 且申请开通较麻烦且不一定成功).

底层依赖于 `spark-starter-notify` 组件, 按照规范实现通知接口, 此组件保证了每条消息都会自动生成唯一的 messageId, 便于发送失败后的补偿处理.

## Install

```
mvn clean install -U -Dmaven.test.skip=true
```

## Usage

1. 添加 `spark-email-spring-boot-starter` 依赖;
    此组件从 1.4.0 开始提供, 因此可能需要修改 kh-boot-dependencies.version` 和 `spark-element-dependencies.version`

    ```xml
    <spark-element-dependencies.version>2022.1.1-SNAPSHOT</spark-element-dependencies.version>
    <spark-boot-dependencies.version>2022.1.1-SNAPSHOT</spark-boot-dependencies.version>
    ```

2. 添加配置

    ```properties
    spark:
      notify:
        email:
          host:
          username:
          password:
          send-from:
          send-to:
    ```

3. 发送邮件

    ```java
    /**
     * 同步发送邮件
     *
     * @since 1.0.0
     */
    @Test
    void test_synchro() {
        EmailMessage message = this.emailNotifyService.notify(new EmailMessage("test", "aaaa"));
        log.info("{}", message.getMessageId());
    }

    /**
     * 异步发送邮件
     *
     * @since 1.0.0
     */
    @SneakyThrows
    @Test
    void test_asynchro() {
        CompletableFuture<EmailMessage> future = this.emailNotifyService.asyncNotify(new EmailMessage("test", "aaaa"));

        future.thenApply(message -> {
            log.info("messageId = {}", message.getMessageId());
            return message;
        });

        // 阻塞获取返回结果
        log.info("{}", future.get());
    }
    ```

可运行 `spark-email-spring-boot-sample` 中的单元测试.

## Todo

- [x] 自动生成 messageId;
- [ ] 使用模板引擎, 可通过模板发送邮件;

## Maintainers

[@dong4j](mailto:dong4j@gmail.com)

## Contributing

请按照 Spark 代码规范进行编码开发, 使用 checkstyle 检查通过后才提交代码.

## License

