package com.zzyl.nursing.dto;

import com.aliyun.iot20180120.models.RegisterDeviceRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "设备注册参数")
public class DeviceDto {

    /** 备注 */
    private String remark;

    @ApiModelProperty(value = "注册参数")
    RegisterDeviceRequest registerDeviceRequest;

    @ApiModelProperty(value = "设备id")
    public String iotId;

    @ApiModelProperty(value = "设备昵称")
    public String nickname;

    @ApiModelProperty(value = "产品的key")
    public String productKey;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "位置名称回显字段")
    private String deviceDescription;

    @ApiModelProperty(value = "位置类型 0 老人 1位置")
    Integer locationType;

    @ApiModelProperty(value = "绑定位置")
    Long bindingLocation;

    @ApiModelProperty(value = "设备名称")
    String deviceName;

    @ApiModelProperty(value = "物理位置类型 0楼层 1房间 2床位")
    Integer physicalLocationType;
}