package com.zzyl.nursing.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zzyl.common.annotation.Excel;
import com.zzyl.common.core.domain.BaseEntity;

/**
 * 护理计划对象 nursing_plan
 * 
 * @author ruoyi
 * @date 2024-09-07
 */
@Data
public class NursingPlan extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private Long id;

    /** 排序号 */
    @Excel(name = "排序号")
    private Integer sortNo;

    /** 名称 */
    @Excel(name = "名称")
    private String planName;

    /** 状态 0禁用 1启用 */
    @Excel(name = "状态 0禁用 1启用")
    private Integer status;

}
