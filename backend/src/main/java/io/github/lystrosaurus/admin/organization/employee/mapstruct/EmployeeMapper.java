package io.github.lystrosaurus.admin.organization.employee.mapstruct;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import io.github.lystrosaurus.admin.organization.employee.dto.EmployeeCreateDTO;
import io.github.lystrosaurus.admin.organization.employee.entity.HrEmployee;
import io.github.lystrosaurus.admin.organization.employee.vo.EmployeeDetailVO;
import io.github.lystrosaurus.admin.organization.employee.vo.EmployeeVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** 员工 MapStruct 映射器 */
@Mapper(componentModel = SPRING)
public interface EmployeeMapper {

  /**
   * 将 EmployeeCreateDTO 转换为 HrEmployee 实体
   *
   * @param dto 员工创建DTO
   * @return 员工实体
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "employmentStatus", ignore = true)
  @Mapping(target = "leaveDate", ignore = true)
  @Mapping(target = "preferredName", ignore = true)
  @Mapping(target = "sourceType", ignore = true)
  HrEmployee toEntity(EmployeeCreateDTO dto);

  /**
   * 将 HrEmployee 实体转换为 EmployeeVO
   *
   * @param entity 员工实体
   * @return 员工VO
   */
  @Mapping(target = "orgUnitName", ignore = true)
  EmployeeVO toVO(HrEmployee entity);

  /**
   * 将 HrEmployee 实体转换为 EmployeeDetailVO
   *
   * @param entity 员工实体
   * @return 员工详情VO
   */
  @Mapping(target = "orgs", ignore = true)
  @Mapping(target = "orgUnitName", ignore = true)
  EmployeeDetailVO toDetailVO(HrEmployee entity);
}
