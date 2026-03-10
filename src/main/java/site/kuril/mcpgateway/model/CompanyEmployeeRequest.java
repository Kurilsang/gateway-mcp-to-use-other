package site.kuril.mcpgateway.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

/**
 * 公司员工查询请求对象
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyEmployeeRequest {

    @JsonProperty(required = true, value = "city")
    @JsonPropertyDescription("城市名称,如果是中文汉字请先转换为汉语拼音,例如北京:beijing")
    private String city;

    @JsonProperty(required = true, value = "company")
    @JsonPropertyDescription("公司信息,如果是中文汉字请先转换为汉语拼音,例如北京:jd/alibaba")
    private Company company;

    @JsonProperty(required = true, value = "employeeName")
    @JsonPropertyDescription("员工姓名")
    private String employeeName;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Company {

        @JsonProperty(required = true, value = "name")
        @JsonPropertyDescription("公司名称")
        private String name;

        @JsonProperty(required = true, value = "type")
        @JsonPropertyDescription("公司类型")
        private String type;
    }
}