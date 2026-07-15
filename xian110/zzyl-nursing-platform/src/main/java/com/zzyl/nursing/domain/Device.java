package com.zzyl.nursing.domain;

import lombok.Data;
import com.zzyl.common.annotation.Excel;
import com.zzyl.common.core.domain.BaseEntity;

/**
 * 设备对象 device
 * 
 * @author ruoyi
 * @date 2024-09-21
 */
@Data
public class Device extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 物联网设备ID */
    @Excel(name = "物联网设备ID")
    private String iotId;

    /** 绑定位置 */
    @Excel(name = "绑定位置")
    private String bindingLocation;

    /** 位置类型 0：随身设备 1：固定设备 */
    @Excel(name = "位置类型 0：随身设备 1：固定设备")
    private Integer locationType;

    /** 物理位置类型 0楼层 1房间 2床位 */
    @Excel(name = "物理位置类型 0楼层 1房间 2床位")
    private Integer physicalLocationType;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String deviceName;

    /** 备注名称 */
    @Excel(name = "备注名称")
    private String nickname;

    /** 产品key */
    @Excel(name = "产品key")
    private String productKey;

    /** 产品名称 */
    @Excel(name = "产品名称")
    private String productName;

    /** 位置备注 */
    @Excel(name = "位置备注")
    private String deviceDescription;

    /** 产品是否包含门禁，0：否，1：是 */
    @Excel(name = "产品是否包含门禁，0：否，1：是")
    private Integer haveEntranceGuard;



}
