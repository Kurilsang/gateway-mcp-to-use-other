package site.kuril.mcpgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class McpGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpGatewayApplication.class, args);
    }

    // 新版本的 Spring AI MCP 使用自动配置
    // 只需要在服务类上使用 @McpTool 注解即可自动注册
}
