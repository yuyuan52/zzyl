package com.zzyl.nursing.service.impl;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zzyl.common.constant.CacheConstants;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.nursing.domain.Device;
import com.zzyl.nursing.job.Content;
import com.zzyl.nursing.mapper.DeviceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.DeviceDataMapper;
import com.zzyl.nursing.domain.DeviceData;
import com.zzyl.nursing.service.IDeviceDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Arrays;
/**
 * 设备数据Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-09-22
 */
@Service
@Slf4j
public class DeviceDataServiceImpl extends ServiceImpl<DeviceDataMapper,DeviceData> implements IDeviceDataService
{
    @Autowired
    private DeviceDataMapper deviceDataMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 批量保存设备数据
     *
     * @param content
     */
    @Override
    public void batchInsertDeviceData(Content content) {
        //1.根据iotId查询设备
        Device device = deviceMapper.selectOne(Wrappers.<Device>lambdaQuery().eq(Device::getIotId, content.getIotId()));

        //2.如果设备不存在，结束程序
        if(ObjectUtil.isEmpty(device)){
            log.info("设备不存在,iotId:{}",content.getIotId());
            return;
        }

        //3.循环遍历content.items，构建集合
        //key:物模型的标识
        //value:item(value,time)
        List<DeviceData> list = new ArrayList<>();
        content.getItems().forEach((key,item)->{
            DeviceData deviceData = DeviceData.builder()
                    .deviceName(device.getDeviceName())
                    .iotId(device.getIotId())
                    .nickname(device.getNickname())
                    .productKey(device.getProductKey())
                    .productName(device.getProductName())
                    .functionId(key)
                    .accessLocation(device.getRemark())
                    .locationType(device.getLocationType())
                    .physicalLocationType(device.getPhysicalLocationType())
                    .deviceDescription(device.getDeviceDescription())
                    .dataValue(item.getValue()+"")
                    .alarmTime(LocalDateTimeUtil.of(item.getTime()))
                    .build();
            list.add(deviceData);
        });

        //4.批量插入数据
        saveBatch(list);

        //存入到redis  key：hash数据类型（大key，小key,  value）iot:device_last_data   iot_id
        redisTemplate.opsForHash().put(CacheConstants.IOT_DEVICE_LAST_DATA,device.getIotId(), JSONUtil.toJsonStr(list));
    }

    /**
     * 查询设备数据
     * 
     * @param id 设备数据主键
     * @return 设备数据
     */
    @Override
    public DeviceData selectDeviceDataById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询设备数据列表
     * 
     * @param deviceData 设备数据
     * @return 设备数据
     */
    @Override
    public List<DeviceData> selectDeviceDataList(DeviceData deviceData)
    {
        return deviceDataMapper.selectDeviceDataList(deviceData);
    }

    /**
     * 新增设备数据
     * 
     * @param deviceData 设备数据
     * @return 结果
     */
    @Override
    public int insertDeviceData(DeviceData deviceData)
    {
        return save(deviceData)?1:0;
    }

    /**
     * 修改设备数据
     * 
     * @param deviceData 设备数据
     * @return 结果
     */
    @Override
    public int updateDeviceData(DeviceData deviceData)
    {
        return updateById(deviceData)?1:0;
    }

    /**
     * 批量删除设备数据
     * 
     * @param ids 需要删除的设备数据主键
     * @return 结果
     */
    @Override
    public int deleteDeviceDataByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids))?1:0;
    }

    /**
     * 删除设备数据信息
     * 
     * @param id 设备数据主键
     * @return 结果
     */
    @Override
    public int deleteDeviceDataById(Long id)
    {
        return removeById(id)?1:0;
    }

}
