package site.kuril.mcpgateway.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import site.kuril.mcpgateway.model.CompanyEmployeeRequest;
import site.kuril.mcpgateway.model.CompanyEmployeeResponse;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@SpringJUnitConfig
class McpGatewayServiceTest {

    private final McpGatewayService mcpGatewayService = new McpGatewayService();

    @Test
    void testGetCompanyEmployee() {
        // 准备测试数据
        CompanyEmployeeRequest request = new CompanyEmployeeRequest();
        request.setCity("beijing");
        request.setEmployeeName("张三");
        
        CompanyEmployeeRequest.Company company = new CompanyEmployeeRequest.Company();
        company.setName("字节跳动");
        company.setType("technology");
        request.setCompany(company);

        // 执行测试
        CompanyEmployeeResponse response = mcpGatewayService.getCompanyEmployee(request);

        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getSalary());
        assertNotNull(response.getDayManHour());
        assertEquals("技术部", response.getDepartment());
        assertEquals("软件工程师", response.getPosition());
    }

    @Test
    void testGetCompanyInfo() {
        String result = mcpGatewayService.getCompanyInfo("测试公司", "北京");
        assertNotNull(result);
        assertTrue(result.contains("测试公司"));
        assertTrue(result.contains("北京"));
    }

    @Test
    void testGetCurrentTime() {
        String currentTime = mcpGatewayService.getCurrentTime();
        assertNotNull(currentTime);
        assertTrue(currentTime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    void testGetCompaniesInCity() {
        java.util.List<java.util.Map<String, String>> companies = mcpGatewayService.getCompaniesInCity("beijing");
        assertNotNull(companies);
        assertFalse(companies.isEmpty());
        assertTrue(companies.size() > 0);
    }
}