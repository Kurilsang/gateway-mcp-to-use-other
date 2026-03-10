package site.kuril.mcpgateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import site.kuril.mcpgateway.service.McpGatewayService;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MCP 控制器
 * 处理 MCP 协议的 SSE 连接和消息
 */
@Slf4j
@RestController
public class McpController {

    @Autowired
    private McpGatewayService mcpGatewayService;

    private final Map<String, SseEmitter> connections = new ConcurrentHashMap<>();

    /**
     * SSE 端点
     */
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter messages(@RequestParam(required = false) String sessionId) {
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
        }
        
        log.info("新的 SSE 连接: sessionId={}", sessionId);
        
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        connections.put(sessionId, emitter);
        
        try {
            // 发送初始化消息
            emitter.send(SseEmitter.event()
                    .name("endpoint")
                    .data("/sse?sessionId=" + sessionId));
            
            // 发送服务器信息
            emitter.send(SseEmitter.event()
                    .name("server_info")
                    .data("{\"name\":\"mcp-gateway\",\"version\":\"1.0.0\",\"description\":\"MCP Gateway Service\"}"));
            
        } catch (IOException e) {
            log.error("发送初始化消息失败: {}", e.getMessage());
            connections.remove(sessionId);
            emitter.completeWithError(e);
        }
        
        final String finalSessionId = sessionId;
        emitter.onCompletion(() -> {
            log.info("SSE 连接完成: sessionId={}", finalSessionId);
            connections.remove(finalSessionId);
        });
        
        emitter.onError(throwable -> {
            log.error("SSE 连接错误: sessionId={}, error={}", finalSessionId, throwable.getMessage());
            connections.remove(finalSessionId);
        });
        
        emitter.onTimeout(() -> {
            log.warn("SSE 连接超时: sessionId={}", finalSessionId);
            connections.remove(finalSessionId);
        });
        
        return emitter;
    }

    /**
     * 获取可用工具列表
     */
    @GetMapping("/mcp/tools")
    public Map<String, Object> getTools() {
        return Map.of(
            "tools", new Object[]{
                Map.of("name", "getCompanyEmployee", "description", "获取公司员工信息，包括薪资、工作时长、部门和职位等详细信息"),
                Map.of("name", "getCompanyInfo", "description", "获取公司基本信息，包括公司规模、成立时间等"),
                Map.of("name", "getCompaniesInCity", "description", "根据城市名称获取该城市的主要公司列表"),
                Map.of("name", "getCurrentTime", "description", "获取当前系统时间，格式为 yyyy-MM-dd HH:mm:ss"),
                Map.of("name", "callExternalApi", "description", "模拟调用外部API获取数据，可以用于测试网关的代理功能")
            }
        );
    }

    /**
     * 健康检查
     */
    @GetMapping("/mcp/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "service", "mcp-gateway",
            "connections", connections.size(),
            "message", "MCP Gateway is running"
        );
    }
}