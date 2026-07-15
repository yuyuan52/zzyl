package com.zzyl.nursing.service;

import java.util.List;

import com.aliyun.iot20180120.models.DeleteDeviceRequest;
import com.aliyun.iot20180120.models.QueryDevicePropertyStatusRequest;
import com.aliyun.iot20180120.models.QueryThingModelPublishedRequest;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.nursing.domain.Device;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzyl.nursing.dto.DeviceDto;
import com.zzyl.nursing.vo.DeviceVo;
import com.zzyl.nursing.vo.ProductVo;

/**
 * 设备Service接口
 * 
 * @author ruoyi
 * @date 2024-09-21
 */
public interface IDeviceService extends IService<Device>
{
    /**
     * 查询设备
     * 
     * @param id 设备主键
     * @return 设备
     */
    public Device selectDeviceById(Long id);

    /**
     * 查询设备列表
     * 
     * @param device 设备
     * @return 设备集合
     */
    public List<Device> selectDeviceList(Device device);

    /**
     * 新增设备
     * 
     * @param device 设备
     * @return 结果
     */
    public int insertDevice(Device device);

    /**
     * 修改设备
     * 
     * @param device 设备
     * @return 结果
     */
    public int updateDevice(Device device);

    /**
     * 批量删除设备
     * 
     * @param ids 需要删除的设备主键集合
     * @return 结果
     */
    public int deleteDeviceByIds(Long[] ids);

    /**
     * 删除设备信息
     * @return 结果
     */
    public int deleteDeviceById(DeleteDeviceRequest request);

    /**
     * 同步iot产品数据到redis
     */
    void syncProductList();

    /**
     * 查询产品列表
     * @return
     */
    List<ProductVo> allProduct();

    /**
     * 注册设备
     * @param deviceDto
     */
    void registerDevice(DeviceDto deviceDto);

    /**
     * 查询设备详情
     * @param deviceDto
     * @return
     */
    DeviceVo queryDeviceDetail(DeviceDto deviceDto);

    /**
     * 查询物模型的属性状态
     * @param request
     * @return
     */
    AjaxResult queryDevicePropertyStatus(QueryDevicePropertyStatusRequest request);

    /**
     * 查询产品的物模型功能定义
     * @param request
     * @return
     */
    AjaxResult queryThingModelPublished(QueryThingModelPublishedRequest request);
}
