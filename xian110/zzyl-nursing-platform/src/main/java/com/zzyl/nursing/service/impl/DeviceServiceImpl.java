package com.zzyl.nursing.service.impl;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.aliyun.iot20180120.Client;
import com.aliyun.iot20180120.models.*;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zzyl.common.constant.CacheConstants;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.common.utils.StringUtils;
import com.zzyl.framework.config.properties.AliIoTConfigProperties;
import com.zzyl.nursing.dto.DeviceDto;
import com.zzyl.nursing.vo.DeviceVo;
import com.zzyl.nursing.vo.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.DeviceMapper;
import com.zzyl.nursing.domain.Device;
import com.zzyl.nursing.service.IDeviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
/**
 * 设备Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-09-21
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper,Device> implements IDeviceService
{
    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private Client client;

    @Autowired
    private AliIoTConfigProperties aliIoTConfigProperties;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 查询产品的物模型功能定义
     *
     * @param request
     * @return
     */
    @Override
    public AjaxResult queryThingModelPublished(QueryThingModelPublishedRequest request) {
        request.setIotInstanceId(aliIoTConfigProperties.getIotInstanceId());
        //调用接口
        QueryThingModelPublishedResponse response;
        try {
            response = client.queryThingModelPublished(request);
        } catch (Exception e) {
            throw new BaseException("IOT调用，查询产品物模型定义失败");
        }
        //判断接口调通了
        if(!response.getBody().success){
            throw new BaseException(response.getBody().getErrorMessage());
        }

        return AjaxResult.success(response.getBody().getData());
    }

    /**
     * 查询物模型的属性状态
     * @param request
     * @return
     */
    @Override
    public AjaxResult queryDevicePropertyStatus(QueryDevicePropertyStatusRequest request) {

        request.setIotInstanceId(aliIoTConfigProperties.getIotInstanceId());
        //调用接口
        QueryDevicePropertyStatusResponse response;
        try {
            response = client.queryDevicePropertyStatus(request);
        } catch (Exception e) {
            throw new BaseException("IOT调用，查询设备属性状态失败");
        }
        //判断接口调通了
        if(!response.getBody().success){
            throw new BaseException(response.getBody().getErrorMessage());
        }
        return AjaxResult.success(response.getBody().getData());
    }

    /**
     * 查询设备详情
     *
     * @param deviceDto
     * @return
     */
    @Override
    public DeviceVo queryDeviceDetail(DeviceDto deviceDto) {


        //查询iot平台的设备数据
        QueryDeviceDetailRequest queryDeviceDetailRequest = new QueryDeviceDetailRequest();
        queryDeviceDetailRequest.setIotId(deviceDto.getIotId());
        queryDeviceDetailRequest.setIotInstanceId(aliIoTConfigProperties.getIotInstanceId());
        queryDeviceDetailRequest.setProductKey(deviceDto.getProductKey());
        QueryDeviceDetailResponse queryDeviceDetailResponse;
        try {
            queryDeviceDetailResponse = client.queryDeviceDetail(queryDeviceDetailRequest);
        } catch (Exception e) {
            throw new BaseException("IOT调用，查询设备详情失败");
        }
        //判断接口调通了
        if(!queryDeviceDetailResponse.getBody().success){
            throw new BaseException(queryDeviceDetailResponse.getBody().getErrorMessage());
        }
        //获取数据
        QueryDeviceDetailResponseBody.QueryDeviceDetailResponseBodyData data = queryDeviceDetailResponse.getBody().getData();
        //根据iotid查询数据库的设备
        Device device = getOne(Wrappers.<Device>lambdaQuery().eq(Device::getIotId, deviceDto.getIotId()));
        //第一次拷贝，只包含了数据库中的设备数据
        DeviceVo deviceVo = BeanUtil.toBean(device, DeviceVo.class);
        //第二次拷贝，从物联网中查询的设备数据，拷贝到devicevo中
        BeanUtil.copyProperties(data,deviceVo, CopyOptions.create().ignoreNullValue());
        return deviceVo;
    }

    /**
     * 注册设备
     * @param deviceDto
     */
    @Override
    public void registerDevice(DeviceDto deviceDto) {
        //判断设备名称是否重复
        long count = count(Wrappers.<Device>lambdaQuery().eq(Device::getDeviceName, deviceDto.getDeviceName()));
        if(count > 0){
            throw new BaseException("设备名称重复，请重新输入");
        }

        //在IOT平台中新增设备
        RegisterDeviceRequest registerDeviceRequest = deviceDto.getRegisterDeviceRequest();
        registerDeviceRequest.setIotInstanceId(aliIoTConfigProperties.getIotInstanceId());
        RegisterDeviceResponse registerDeviceResponse;
        try {
            registerDeviceResponse = client.registerDevice(registerDeviceRequest);
        } catch (Exception e) {
            throw new BaseException("IOT调用，注册设备失败");
        }
        //判断接口调通了
        if(!registerDeviceResponse.getBody().success){
            throw new BaseException(registerDeviceResponse.getBody().getErrorMessage());
        }

        //属性拷贝
        Device device = BeanUtil.toBean(deviceDto, Device.class);
        //iotid  是物联网平台对于设备的唯一标识
        device.setIotId(registerDeviceResponse.getBody().getData().getIotId());

        //获取产品名称
        QueryProductRequest request = new QueryProductRequest();
        request.setProductKey(deviceDto.getProductKey());
        request.setIotInstanceId(aliIoTConfigProperties.getIotInstanceId());
        QueryProductResponse queryProductResponse;
        try {
            queryProductResponse = client.queryProduct(request);
        } catch (Exception e) {
            throw new BaseException("IOT调用，查询产品失败");
        }
        //判断接口调通了
        if(!queryProductResponse.getBody().success){
            throw new BaseException(queryProductResponse.getBody().getErrorMessage());
        }
        //赋值产品名称
        device.setProductName(queryProductResponse.getBody().getData().getProductName());
        //判断位置类型，是0，给物理位置类型-1
        if(device.getLocationType().equals(0)){
            device.setPhysicalLocationType(-1);
        }
        try {
            //保存数据
            save(device);
        } catch (Exception e) {
            //删除物联网中的设备数据
            DeleteDeviceRequest deleteDeviceRequest = new DeleteDeviceRequest();
            deleteDeviceRequest.setDeviceName(device.getDeviceName());
            deleteDeviceRequest.setIotId(device.getIotId());
            deleteDeviceRequest.setIotInstanceId(aliIoTConfigProperties.getIotInstanceId());
            deleteDeviceRequest.setProductKey(device.getProductKey());
            try {
                client.deleteDevice(deleteDeviceRequest);
            } catch (Exception ex) {
                throw new BaseException("IOT调用，删除产品失败");
            }
            //抛出异常,同一个位置或老人不能绑定同一个产品
            throw new BaseException("该老人/位置已绑定该产品，请重新选择");
        }


    }

    /**
     * 查询产品列表
     *
     * @return
     */
    @Override
    public List<ProductVo> allProduct() {
        //1.从redis中获取数据
        String jsonStr = redisTemplate.opsForValue().get(CacheConstants.IOT_ALL_PRODUCT_LIST);
        //2.判断是否存在，如不存在，返回一个空集合
        if(StringUtils.isEmpty(jsonStr)){
            return Collections.emptyList();
        }

        //3.有值，转换list返回
        return JSONUtil.toList(jsonStr, ProductVo.class);
    }

    /**
     * 同步iot产品数据到redis
     */
    @Override
    public void syncProductList() {
        //1.从iot平台中获取所有的产品
        QueryProductListRequest request = new QueryProductListRequest();
        request.setCurrentPage(1);
        request.setPageSize(200);
        request.setIotInstanceId(aliIoTConfigProperties.getIotInstanceId());
        QueryProductListResponse response;
        try {
            response = client.queryProductList(request);
        } catch (Exception e) {
            throw new BaseException("IOT调用，查询产品失败");
        }
        //判断接口调通了
        if(!response.getBody().success){
            throw new BaseException(response.getBody().getErrorMessage());
        }

        //2.存储到redis
        redisTemplate.opsForValue().set(CacheConstants.IOT_ALL_PRODUCT_LIST, JSONUtil.toJsonStr(response.getBody().getData().getList().getProductInfo()));
    }

    /**
     * 查询设备
     * 
     * @param id 设备主键
     * @return 设备
     */
    @Override
    public Device selectDeviceById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询设备列表
     * 
     * @param device 设备
     * @return 设备
     */
    @Override
    public List<Device> selectDeviceList(Device device)
    {
        return deviceMapper.selectDeviceList(device);
    }

    /**
     * 新增设备
     * 
     * @param device 设备
     * @return 结果
     */
    @Override
    public int insertDevice(Device device)
    {
        return save(device)?1:0;
    }

    /**
     * 修改设备
     * 
     * @param device 设备
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDevice(Device device)
    {
        //查询设备是否存在
        Device dbDevice = getById(device.getId());
        if(ObjectUtil.isEmpty(dbDevice)){
            throw new BaseException("设备不存在");
        }

        //先更新设备
        //如果是随身设备，物理位置设为-1
        if (device.getLocationType().equals(0)) {
            device.setPhysicalLocationType(-1);
        }

        //更新数据库中设备信息
        try {
            deviceMapper.updateDevice(device);
        } catch (Exception e) {
            throw new BaseException("该老人/位置已绑定该产品，请重新选择");
        }

        //批量更新设备昵称
        BatchUpdateDeviceNicknameRequest request = new BatchUpdateDeviceNicknameRequest();
        request.setIotInstanceId(aliIoTConfigProperties.getIotInstanceId());
        //更新设备昵称（批量）
        //集合主要作用是装入多个设备
        List<BatchUpdateDeviceNicknameRequest.BatchUpdateDeviceNicknameRequestDeviceNicknameInfo>
                list = new ArrayList<>();
        BatchUpdateDeviceNicknameRequest.BatchUpdateDeviceNicknameRequestDeviceNicknameInfo
                info = new BatchUpdateDeviceNicknameRequest.BatchUpdateDeviceNicknameRequestDeviceNicknameInfo();
        //属性拷贝
        BeanUtil.copyProperties(device, info);
        list.add(info);
        request.setDeviceNicknameInfo(list);
        BatchUpdateDeviceNicknameResponse response;
        try {
            response = client.batchUpdateDeviceNickname(request);
        } catch (Exception e) {
            throw new BaseException("IOT调用，更新设备失败");
        }

        //判断接口调通了
        if(!response.getBody().success){
            throw new BaseException(response.getBody().getErrorMessage());
        }


        return 1;
    }

    /**
     * 批量删除设备
     * 
     * @param ids 需要删除的设备主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteDeviceByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids))?1:0;
    }

    /**
     * 删除设备信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteDeviceById(DeleteDeviceRequest request)
    {
        //通过设备id删除设备
        remove(Wrappers.<Device>lambdaQuery().eq(Device::getIotId,request.getIotId()));
        //删除物联网中的数据
        request.setIotInstanceId(aliIoTConfigProperties.getIotInstanceId());
        DeleteDeviceResponse response;
        try {
            response = client.deleteDevice(request);
        } catch (Exception e) {
            throw new BaseException("IOT调用，删除设备失败");
        }
        //判断接口调通了
        if(!response.getBody().success){
            throw new BaseException(response.getBody().getErrorMessage());
        }
        return 1;
    }

}
