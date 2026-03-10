package site.kuril.mcpgateway.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

/**
 * 公司员工查询响应对象
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyEmployeeResponse {

    @JsonProperty(value = "salary")
    @JsonPropertyDescription("员工薪资")
    private String salary;

    @JsonProperty(value = "dayManHour")
    @JsonPropertyDescription("每日工作小时数")
    private String dayManHour;

    @JsonProperty(value = "department")
    @JsonPropertyDescription("所属部门")
    private String department;

    @JsonProperty(value = "position")
    @JsonPropertyDescription("职位")
    private String position;
}