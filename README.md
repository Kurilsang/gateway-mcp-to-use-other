# MCP Gateway

这是一个基于 Spring AI MCP 框架实现的简易 MCP 网关服务。

## 功能特性

- **SSE 通信协议**: 使用 Server-Sent Events 与 LLM 进行通信
- **动态工具注册**: 通过 `@McpTool` 注解将普通方法包装成 MCP 工具
- **企业信息查询**: 提供员工信息、公司信息等查询功能
- **可扩展架构**: 易于添加新的工具和功能

## 可用工具

1. **getCompanyEmployee**: 获取公司员工详细信息
2. **getCompanyInfo**: 获取公司基本信息
3. **getCompaniesInCity**: 根据城市获取公司列表
4. **getCurrentTime**: 获取当前系统时间
5. **callExternalApi**: 模拟外部API调用

## 快速启动

### 1. 编译项目
```bash
mvn clean compile
```

### 2. 启动服务
```bash
mvn spring-boot:run
```

### 3. 验证服务
访问健康检查接口：
```
GET http://localhost:8701/api/health
```

### 4. MCP 端点
MCP SSE 消息端点：
```
http://localhost:8701/mcp/messages
```

## 配置说明

### application.yml 关键配置

```yaml
spring:
  ai:
    mcp:
      server:
        name: mcp-gateway
        version: 1.0.0
        type: ASYNC
        sse-message-endpoint: /mcp/messages
        capabilities:
          tool: true
          resource: true
          prompt: true
          completion: true
```

## 使用示例

当 LLM 连接到这个 MCP 服务后，可以调用以下工具：

```json
{
  "method": "tools/call",
  "params": {
    "name": "getCompanyEmployee",
    "arguments": {
      "city": "beijing",
      "company": {
        "name": "字节跳动",
        "type": "technology"
      },
      "employeeName": "张三"
    }
  }
}
```

## 扩展开发

要添加新的工具，只需：

1. 在 `McpGatewayService` 中添加新方法
2. 使用 `@McpTool` 注解标记方法
3. 使用 `@McpToolParam` 注解标记参数
4. 添加详细的描述信息
5. 重启服务即可

### 示例代码

```java
@McpTool(name = "myNewTool", description = "这是一个新工具的描述")
public String myNewTool(
        @McpToolParam(description = "参数描述", required = true) String param1,
        @McpToolParam(description = "可选参数", required = false) String param2) {
    // 工具逻辑
    return "结果";
}
```

## 技术栈

- Spring Boot 2.6.13
- Spring AI MCP Server 1.1.2
- Spring WebFlux (SSE支持)
- Lombok
- Jackson

## 依赖说明

项目使用了最新的 Spring AI MCP 依赖：

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-mcp-server</artifactId>
    <version>1.1.2</version>
</dependency>
```

## 注意事项

- 当前实现返回的是模拟数据
- 实际使用时需要替换为真实的业务接口调用
- 建议在生产环境中添加认证和限流机制
- 新版本使用 `@McpTool` 和 `@McpToolParam` 注解，支持自动配置