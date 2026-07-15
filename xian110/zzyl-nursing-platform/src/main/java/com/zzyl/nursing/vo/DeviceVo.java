package com.zzyl.nursing.vo;

import com.aliyun.tea.NameInMap;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("设备信息响应模型")
public class DeviceVo {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")//get
    protected LocalDateTime createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")//get
    protected LocalDateTime updateTime;

    /**
     * 创建人名称
     */
    @ApiModelProperty(value = "创建人名称")
    private String creator;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "位置类型 0 随身设备 1固定设备")
    private Integer locationType;

    @ApiModelProperty(value = "绑定位置")
    private Long bindingLocation;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "物理位置类型")
    private Integer physicalLocationType;

    @ApiModelProperty(value = "物联网设备ID")
    private String iotId;

    @ApiModelProperty(value = "位置名称回显字段")
    private String deviceDescription;

    @ApiModelProperty(value = "产品是否包含门禁，0：否，1：是")
    private Integer haveEntranceGuard;

    @NameInMap("DeviceSecret")
    public String deviceSecret;

    @NameInMap("FirmwareVersion")
    public String firmwareVersion;

    @NameInMap("GmtActive")
    public String gmtActive;

    @NameInMap("GmtCreate")
    public String gmtCreate;

    @NameInMap("GmtOnline")
    public String gmtOnline;

    @NameInMap("IpAddress")
    public String ipAddress;

    @ApiModelProperty(value = "设备备注名称")
    @NameInMap("Nickname")
    public String nickname;

    @NameInMap("NodeType")
    public Integer nodeType;

    @NameInMap("Owner")
    public Boolean owner;

    @ApiModelProperty(value = "产品key")
    @NameInMap("ProductKey")
    public String productKey;

    @ApiModelProperty(value = "产品名称")
    @NameInMap("ProductName")
    public String productName;

    @NameInMap("Region")
    public String region;

    @NameInMap("Status")
    public String status;

    @NameInMap("UtcActive")
    public String utcActive;

    @NameInMap("UtcCreate")
    public String utcCreate;

    @NameInMap("UtcOnline")
    public String utcOnline;

}