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
     * 调用MCP工具
     */
    @PostMapping("/mcp/tools/call")
    public Map<String, Object> callTool(@RequestBody Map<String, Object> request) {
        String toolName = (String) request.get("name");
        Map<String, Object> arguments = (Map<String, Object>) request.get("arguments");
        
        log.info("调用工具: {}, 参数: {}", toolName, arguments);
        
        try {
            Object result = null;
            
            switch (toolName) {
                case "getCompanyEmployee":
                    // 解析参数并调用服务
                    result = mcpGatewayService.getCompanyEmployee(parseCompanyEmployeeRequest(arguments));
                    break;
                case "getCompanyInfo":
                    String companyName = (String) arguments.get("companyName");
                    String city = (String) arguments.get("city");
                    result = mcpGatewayService.getCompanyInfo(companyName, city);
                    break;
                case "getCompaniesInCity":
                    String cityName = (String) arguments.get("city");
                    result = mcpGatewayService.getCompaniesInCity(cityName);
                    break;
                case "getCurrentTime":
                    result = mcpGatewayService.getCurrentTime();
                    break;
                case "callExternalApi":
                    String apiName = (String) arguments.get("apiName");
                    String params = (String) arguments.get("params");
                    result = mcpGatewayService.callExternalApi(apiName, params);
                    break;
                default:
                    return Map.of(
                        "success", false,
                        "error", "Unknown tool: " + toolName
                    );
            }
            
            return Map.of(
                "success", true,
                "result", result,
                "toolName", toolName
            );
            
        } catch (Exception e) {
            log.error("工具调用失败: {}", e.getMessage(), e);
            return Map.of(
                "success", false,
                "error", e.getMessage(),
                "toolName", toolName
            );
        }
    }
    
    private site.kuril.mcpgateway.model.CompanyEmployeeRequest parseCompanyEmployeeRequest(Map<String, Object> arguments) {
        site.kuril.mcpgateway.model.CompanyEmployeeRequest request = new site.kuril.mcpgateway.model.CompanyEmployeeRequest();
        
        // 解析xxxRequest01
        Map<String, Object> request01 = (Map<String, Object>) arguments.get("xxxRequest01");
        if (request01 != null) {
            request.setCity((String) request01.get("city"));
            
            Map<String, Object> company = (Map<String, Object>) request01.get("company");
            if (company != null) {
                site.kuril.mcpgateway.model.CompanyEmployeeRequest.Company companyObj = 
                    new site.kuril.mcpgateway.model.CompanyEmployeeRequest.Company();
                companyObj.setName((String) company.get("name"));
                companyObj.setType((String) company.get("type"));
                request.setCompany(companyObj);
            }
        }
        
        // 解析xxxRequest02
        Map<String, Object> request02 = (Map<String, Object>) arguments.get("xxxRequest02");
        if (request02 != null) {
            request.setEmployeeName((String) request02.get("employeeCount")); // 注意：这里employeeCount实际是员工姓名
        }
        
        return request;
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