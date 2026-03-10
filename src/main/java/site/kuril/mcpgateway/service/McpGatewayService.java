package site.kuril.mcpgateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.kuril.mcpgateway.model.CompanyEmployeeRequest;
import site.kuril.mcpgateway.model.CompanyEmployeeResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * MCP 网关服务
 * 这个服务将作为 MCP 工具提供给 LLM 调用
 */
@Slf4j
@Service
public class McpGatewayService {

    private final Random random = new Random();

    /**
     * 获取公司员工信息
     * 这个方法会被包装成 MCP 工具，供 LLM 调用
     * 
     * @param request 员工查询请求
     * @return 员工信息响应
     */
    public CompanyEmployeeResponse getCompanyEmployee(CompanyEmployeeRequest request) {
        log.info("查询员工信息 - 城市: {}, 公司: {}, 员工: {}", 
                request.getCity(), 
                request.getCompany().getName(), 
                request.getEmployeeName());

        // 这里可以调用实际的业务接口，比如 HTTP 接口、数据库查询等
        // 目前返回模拟数据
        CompanyEmployeeResponse response = new CompanyEmployeeResponse();
        
        response.setSalary(String.valueOf(8000 + random.nextInt(12000))); // 8000-20000
        response.setDayManHour(String.valueOf(8 + random.nextInt(4))); // 8-12小时
        
        // 根据公司类型模拟部门和职位
        String companyType = request.getCompany().getType();
        if ("tech".equalsIgnoreCase(companyType) || "technology".equalsIgnoreCase(companyType)) {
            response.setDepartment("技术部");
            response.setPosition("软件工程师");
        } else if ("finance".equalsIgnoreCase(companyType)) {
            response.setDepartment("财务部");
            response.setPosition("财务分析师");
        } else {
            response.setDepartment("业务部");
            response.setPosition("业务专员");
        }

        log.info("返回员工信息: {}", response);
        return response;
    }

    /**
     * 获取公司基本信息
     */
    public String getCompanyInfo(String companyName, String city) {
        log.info("查询公司信息 - 公司: {}, 城市: {}", companyName, city);
        
        // 模拟返回公司信息
        return String.format("公司 %s 位于 %s，成立于2010年，员工规模500-1000人，主要业务为软件开发和技术服务。", 
                companyName, city);
    }

    /**
     * 获取城市的公司列表
     */
    public List<Map<String, String>> getCompaniesInCity(String city) {
        log.info("查询城市公司列表 - 城市: {}", city);
        
        List<Map<String, String>> companies = new ArrayList<>();
        
        // 模拟不同城市的公司数据
        if ("beijing".equalsIgnoreCase(city) || "北京".equals(city)) {
            companies.add(createCompanyMap("字节跳动", "technology", "互联网"));
            companies.add(createCompanyMap("百度", "technology", "搜索引擎"));
            companies.add(createCompanyMap("京东", "ecommerce", "电商"));
        } else if ("shanghai".equalsIgnoreCase(city) || "上海".equals(city)) {
            companies.add(createCompanyMap("阿里巴巴", "technology", "电商科技"));
            companies.add(createCompanyMap("拼多多", "ecommerce", "电商"));
            companies.add(createCompanyMap("携程", "travel", "在线旅游"));
        } else {
            // 默认返回一些通用公司
            companies.add(createCompanyMap("本地科技公司", "technology", "软件开发"));
            companies.add(createCompanyMap("本地贸易公司", "trade", "贸易"));
        }
        
        return companies;
    }

    /**
     * 获取当前系统时间
     */
    public String getCurrentTime() {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("获取当前时间: {}", currentTime);
        return currentTime;
    }

    /**
     * 模拟调用外部API
     */
    public Map<String, Object> callExternalApi(String apiName, String params) {
        log.info("调用外部API - API名称: {}, 参数: {}", apiName, params);
        
        Map<String, Object> result = new HashMap<>();
        result.put("apiName", apiName);
        result.put("params", params);
        result.put("timestamp", System.currentTimeMillis());
        result.put("status", "success");
        result.put("data", "这是模拟的API返回数据");
        
        return result;
    }

    private Map<String, String> createCompanyMap(String name, String type, String industry) {
        Map<String, String> company = new HashMap<>();
        company.put("name", name);
        company.put("type", type);
        company.put("industry", industry);
        return company;
    }
}