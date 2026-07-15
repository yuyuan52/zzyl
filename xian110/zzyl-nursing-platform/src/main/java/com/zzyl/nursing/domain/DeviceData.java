package com.zzyl.nursing.domain;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import com.zzyl.common.annotation.Excel;
import com.zzyl.common.core.domain.BaseEntity;
import lombok.NoArgsConstructor;

/**
 * 设备数据对象 device_data
 * 
 * @author ruoyi
 * @date 2024-09-22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceData extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 告警规则ID，自增主键 */
    private Long id;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String deviceName;

    /** 设备ID */
    @Excel(name = "设备ID")
    private String iotId;

    /** 备注名称 */
    @Excel(name = "备注名称")
    private String nickname;

    /** 所属产品的key */
    @Excel(name = "所属产品的key")
    private String productKey;

    /** 产品名称 */
    @Excel(name = "产品名称")
    private String productName;

    /** 功能名称 */
    @Excel(name = "功能名称")
    private String functionId;

    /** 接入位置 */
    @Excel(name = "接入位置")
    private String accessLocation;

    /** 位置类型 0：随身设备 1：固定设备 */
    @Excel(name = "位置类型 0：随身设备 1：固定设备")
    private Integer locationType;

    /** 物理位置类型 0楼层 1房间 2床位 */
    @Excel(name = "物理位置类型 0楼层 1房间 2床位")
    private Integer physicalLocationType;

    /** 位置备注 */
    @Excel(name = "位置备注")
    private String deviceDescription;

    /** 数据值 */
    @Excel(name = "数据值")
    private String dataValue;

    /** 数据上报时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "数据上报时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime alarmTime;



}
