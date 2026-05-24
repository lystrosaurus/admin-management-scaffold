package io.github.lystrosaurus.admin.organization.employee.mapstruct;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lystrosaurus.admin.organization.employee.dto.EmployeeCreateDTO;
import io.github.lystrosaurus.admin.organization.employee.entity.HrEmployee;
import io.github.lystrosaurus.admin.organization.employee.vo.EmployeeDetailVO;
import io.github.lystrosaurus.admin.organization.employee.vo.EmployeeVO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** EmployeeMapper MapStruct 单元测试 */
@DisplayName("EmployeeMapper MapStruct 测试")
class EmployeeMapperTest {

  private final EmployeeMapper mapper = EmployeeMapper.INSTANCE;

  @Test
  @DisplayName("应该将 EmployeeCreateDTO 转换为 HrEmployee 实体")
  void should_convert_create_dto_to_entity() {
    // Given
    EmployeeCreateDTO dto =
        new EmployeeCreateDTO(
            "EMP001",
            "张三",
            "13800138000",
            "zhangsan@example.com",
            "高级工程师",
            1L,
            LocalDate.of(2024, 1, 15));

    // When
    HrEmployee entity = mapper.toEntity(dto);

    // Then
    assertNotNull(entity);
    assertEquals("EMP001", entity.getEmployeeNo());
    assertEquals("张三", entity.getName());
    assertEquals("13800138000", entity.getMobile());
    assertEquals("zhangsan@example.com", entity.getEmail());
    assertEquals("高级工程师", entity.getJobTitle());
    assertEquals(1L, entity.getPrimaryOrgId());
    assertEquals(LocalDate.of(2024, 1, 15), entity.getEntryDate());
    // 基础字段应被忽略
    assertNull(entity.getId());
    assertNull(entity.getCreatedAt());
    assertNull(entity.getUpdatedAt());
    assertNull(entity.getCreatedBy());
    assertNull(entity.getUpdatedBy());
    assertNull(entity.getDeleted());
    assertNull(entity.getVersion());
    assertNull(entity.getEmploymentStatus());
    assertNull(entity.getLeaveDate());
    assertNull(entity.getPreferredName());
    assertNull(entity.getSourceType());
  }

  @Test
  @DisplayName("应该将 HrEmployee 实体转换为 EmployeeVO")
  void should_convert_entity_to_vo() {
    // Given
    HrEmployee entity = new HrEmployee();
    entity.setId(1L);
    entity.setEmployeeNo("EMP001");
    entity.setName("张三");
    entity.setPreferredName("三哥");
    entity.setMobile("13800138000");
    entity.setEmail("zhangsan@example.com");
    entity.setJobTitle("高级工程师");
    entity.setEmploymentStatus("ACTIVE");
    entity.setPrimaryOrgId(100L);
    entity.setEntryDate(LocalDate.of(2024, 1, 15));
    entity.setLeaveDate(null);
    entity.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30));

    // When
    EmployeeVO vo = mapper.toVO(entity);

    // Then
    assertNotNull(vo);
    assertEquals(1L, vo.id());
    assertEquals("EMP001", vo.employeeNo());
    assertEquals("张三", vo.name());
    assertEquals("三哥", vo.preferredName());
    assertEquals("13800138000", vo.mobile());
    assertEquals("zhangsan@example.com", vo.email());
    assertEquals("高级工程师", vo.jobTitle());
    assertEquals("ACTIVE", vo.employmentStatus());
    assertEquals(100L, vo.primaryOrgId());
    assertNull(vo.orgUnitName());
    assertEquals(LocalDate.of(2024, 1, 15), vo.entryDate());
    assertNull(vo.leaveDate());
    assertEquals(LocalDateTime.of(2024, 1, 15, 10, 30), vo.createdAt());
  }

  @Test
  @DisplayName("应该将 HrEmployee 实体转换为 EmployeeDetailVO（orgs 字段被忽略）")
  void should_convert_entity_to_detail_vo_with_orgs_ignored() {
    // Given
    HrEmployee entity = new HrEmployee();
    entity.setId(1L);
    entity.setEmployeeNo("EMP001");
    entity.setName("张三");
    entity.setEmploymentStatus("ACTIVE");
    entity.setCreatedAt(LocalDateTime.now());

    // When
    EmployeeDetailVO detailVO = mapper.toDetailVO(entity);

    // Then
    assertNotNull(detailVO);
    assertEquals(1L, detailVO.id());
    assertEquals("EMP001", detailVO.employeeNo());
    assertEquals("张三", detailVO.name());
    assertNull(detailVO.orgUnitName());
    assertNull(detailVO.orgs()); // orgs 应被忽略，由 Service 层单独填充
  }

  @Test
  @DisplayName("转换 null 实体应返回 null")
  void should_return_null_when_entity_is_null() {
    assertNull(mapper.toVO(null));
    assertNull(mapper.toDetailVO(null));
    assertNull(mapper.toEntity(null));
  }
}
