package com.zzyl.nursing.service.impl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zzyl.common.constant.CacheConstants;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.common.utils.StringUtils;
import com.zzyl.nursing.config.WebSocketServer;
import com.zzyl.nursing.domain.AlertData;
import com.zzyl.nursing.domain.DeviceData;
import com.zzyl.nursing.mapper.DeviceMapper;
import com.zzyl.nursing.service.IAlertDataService;
import com.zzyl.nursing.vo.AlertNotifyVo;
import com.zzyl.system.mapper.SysUserRoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.AlertRuleMapper;
import com.zzyl.nursing.domain.AlertRule;
import com.zzyl.nursing.service.IAlertRuleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 报警规则Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-09-25
 */
@Service
@Slf4j
public class AlertRuleServiceImpl extends ServiceImpl<AlertRuleMapper,AlertRule> implements IAlertRuleService
{
    @Autowired
    private AlertRuleMapper alertRuleMapper;

    /**
     * 查询报警规则
     * 
     * @param id 报警规则主键
     * @return 报警规则
     */
    @Override
    public AlertRule selectAlertRuleById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询报警规则列表
     * 
     * @param alertRule 报警规则
     * @return 报警规则
     */
    @Override
    public List<AlertRule> selectAlertRuleList(AlertRule alertRule)
    {
        return alertRuleMapper.selectAlertRuleList(alertRule);
    }

    /**
     * 新增报警规则
     * 
     * @param alertRule 报警规则
     * @return 结果
     */
    @Override
    public int insertAlertRule(AlertRule alertRule)
    {
        return save(alertRule)?1:0;
    }

    /**
     * 修改报警规则
     * 
     * @param alertRule 报警规则
     * @return 结果
     */
    @Override
    public int updateAlertRule(AlertRule alertRule)
    {
        return updateById(alertRule)?1:0;
    }

    /**
     * 批量删除报警规则
     * 
     * @param ids 需要删除的报警规则主键
     * @return 结果
     */
    @Override
    public int deleteAlertRuleByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids))?1:0;
    }

    /**
     * 删除报警规则信息
     * 
     * @param id 报警规则主键
     * @return 结果
     */
    @Override
    public int deleteAlertRuleById(Long id)
    {
        return removeById(id)?1:0;
    }

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 报警过滤
     */
    @Override
    public void alertFilter() {
        //查询所有的规则，只查询启用状态的规则
        long count = count(Wrappers.<AlertRule>lambdaQuery().eq(AlertRule::getStatus,1));
        if(count <= 0){
            return;
        }

        //从redis中获取上报的最新数据
        List<Object> values = redisTemplate.opsForHash().values(CacheConstants.IOT_DEVICE_LAST_DATA);
        //如果集合为空，结束请求
        if(CollUtil.isEmpty(values)){
            return;
        }

        //整理数据
        List<DeviceData> deviceDataList = new ArrayList<>();
        values.forEach(v-> deviceDataList.addAll(JSONUtil.toList(v.toString(), DeviceData.class)));

        //逐条过滤
        deviceDataList.forEach(d->alertFilter(d));
    }

    /**
     * 上报的物模型数据，逐条过滤
     * @param deviceData
     */
    private void alertFilter(DeviceData deviceData) {
        //比如，上报的时间超过了1分钟，还需要过滤吗？
        long between = LocalDateTimeUtil.between(deviceData.getAlarmTime(), LocalDateTime.now(), ChronoUnit.SECONDS);
        if(between > 60){
            return;
        }

        //查询所有的规则，条件是什么：状态为1，产品的key,物模型，iotid
        //全部设备的规则
        List<AlertRule> alertRules = list(Wrappers.<AlertRule>lambdaQuery()
                .eq(AlertRule::getStatus,1)
                .eq(AlertRule::getProductKey,deviceData.getProductKey())
                .eq(AlertRule::getFunctionId,deviceData.getFunctionId())
                .eq(AlertRule::getIotId,"-1"));
        //指定的设备规则
        List<AlertRule> iotIdRules = list(Wrappers.<AlertRule>lambdaQuery()
                .eq(AlertRule::getStatus,1)
                .eq(AlertRule::getProductKey,deviceData.getProductKey())
                .eq(AlertRule::getFunctionId,deviceData.getFunctionId())
                .eq(AlertRule::getIotId,deviceData.getIotId()));

        //合并
//        alertRules.addAll(iotIdRules);
        Collection<AlertRule> allRules = CollUtil.addAll(alertRules, iotIdRules);
        if(CollUtil.isEmpty(allRules)){
            return;
        }

        allRules.forEach(rule-> deviceDataAlarmHandler(deviceData,rule));
    }

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    /**
     * 设备数据匹配过滤规则
     * @param deviceData
     * @param rule
     */
    private void deviceDataAlarmHandler(DeviceData deviceData, AlertRule rule) {

        //判断上报的数据是否在生效时段内  00:00:00~23:59:59    00:05:00~22:59:59
        String[] split = rule.getAlertEffectivePeriod().split("~");
        LocalTime startTime = LocalTime.parse(split[0]);
        LocalTime endTime = LocalTime.parse(split[1]);
        //数据上报的时间
        LocalTime localTime = deviceData.getAlarmTime().toLocalTime();
        //如果上报的时间不在生效时段内，则结束请求
        if(localTime.isBefore(startTime) || localTime.isAfter(endTime)){
            return;
        }

        //统计次数的key
        String aggCountKey = CacheConstants.IOT_COUNT_ALERT+deviceData.getIotId()+":"+deviceData.getFunctionId()+":"+rule.getId();

        //判断上报的数据是否达到了规则的阈值
        Double dataValue = Double.valueOf(deviceData.getDataValue());
        Double value = rule.getValue();
        //工具类x,y（顺序有要求，左边是上报的数据，后边是规则定义的数据）  x等于y 返回0  x>y  返回大于0的值   x<y  返回小于0的值
        int compare = NumberUtil.compare(dataValue, value);
        if((rule.getOperator().equals(">=") && compare >= 0) || (rule.getOperator().equals("<") && compare < 0)){
            //符合上报的规则，产生了异常数据
            log.info("当前数据符合报警规则");
        }else {
            log.info("正常上报的数据");
            redisTemplate.delete(aggCountKey);
            return;
        }

        //沉默周期  持续周期
        //设计一个redis的可以，必须唯一，代表的当前的设备、物模型、规则ID
        String silentKey = CacheConstants.IOT_SILENT_ALERT+deviceData.getIotId()+":"+deviceData.getFunctionId()+":"+rule.getId();
        //获取沉默周期
        String silentData = redisTemplate.opsForValue().get(silentKey);
        if(StringUtils.isNotEmpty(silentData)){
            return;
        }
        //持续周期

        String aggCountData = redisTemplate.opsForValue().get(aggCountKey);
        Integer count = StringUtils.isEmpty(aggCountData)? 1 : (Integer.parseInt(aggCountData) + 1);
        //当前count不等于持续周期，就累加数据，并且结束请求
        if(ObjectUtil.notEqual(count,rule.getDuration())){
            //累加数据
            redisTemplate.opsForValue().set(aggCountKey,count+"");
            return;
        }
        //到了报警的条件了，保存一份沉默周期
        redisTemplate.opsForValue().set(silentKey,"1",rule.getAlertSilentPeriod(), TimeUnit.MINUTES);
        //删除统计的次数
        redisTemplate.delete(aggCountKey);

        //保存异常数据
        // 判断上报数据的设备的类型，如果老人的异常数据（手表、睡眠检测带） 设备异常（烟雾报警）
        List<Long> userIds = new ArrayList<>();
        if(rule.getAlertDataType().equals(0)){
            //老人异常（手表、睡眠检测带）  设备id-->设备-->老人id--->护理员
            if(deviceData.getLocationType().equals(0)){
                //随身设备
                userIds = deviceMapper.selectNursingIdsByIotIdWithElder(deviceData.getIotId());
            }else if(deviceData.getLocationType().equals(1) && deviceData.getPhysicalLocationType().equals(2)){
                //床位设备  设备id-->设备-->床位-->老人id--->护理员
                userIds = deviceMapper.selectNursingIdsByIotIdWithBed(deviceData.getIotId());
            }
        }else {
            //设备异常  找维修人员   通过角色名称（维修工）   用户  角色  用户角色中间表
            userIds = sysUserRoleMapper.selectByRoleName("维修工");
        }
        //找到超级管理员
        List<Long> managerIds = sysUserRoleMapper.selectByRoleName("超级管理员");
        //合并两份用户id
        Collection<Long> allUserIds = CollUtil.addAll(userIds, managerIds);
        //去重
        allUserIds = CollUtil.distinct(allUserIds);

        //批量保存异常数据
        List<AlertData> alertDataList = insertAlertData(allUserIds, deviceData, rule);

        //使用websocket通知对应的人
        sendWebsocketAlertNotifyVO(allUserIds,alertDataList.get(0),rule);


    }

    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 发送消息给指定的人
     * @param allUserIds
     * @param alertData
     * @param rule
     */
    private void sendWebsocketAlertNotifyVO(Collection<Long> allUserIds, AlertData alertData, AlertRule rule) {
        //属性拷贝，创建AlertNotifyVo
        AlertNotifyVo alertNotifyVo = BeanUtil.toBean(alertData, AlertNotifyVo.class);
        //物模型名称、通知的状态、报警数据类型
        alertNotifyVo.setFunctionName(rule.getFunctionName());
        alertNotifyVo.setAlertDataType(rule.getAlertDataType());
        alertNotifyVo.setNotifyType(1);
        //发送消息给指定的人
        webSocketServer.sendMessageToConsumer(alertNotifyVo,allUserIds);
    }

    @Autowired
    private IAlertDataService alertDataService;

    /**
     * 保存异常数据
     * @param allUserIds
     * @param deviceData
     * @param rule
     */
    private List<AlertData> insertAlertData(Collection<Long> allUserIds, DeviceData deviceData, AlertRule rule) {

        //属性拷贝，从deviceData拷贝到alertData
        AlertData alertData = BeanUtil.toBean(deviceData, AlertData.class);
        //关于规则的数据都拷贝不过去
        alertData.setAlertRuleId(rule.getId());
        //功能名称+运算符+阈值+持续周期+聚合周期
        String reason = CharSequenceUtil.format("{}{}{},持续了{}周期，就报警",rule.getFunctionId(),
                rule.getOperator(),rule.getValue(),rule.getDuration());
        alertData.setAlertReason(reason);
        //报警状态
        alertData.setStatus(0);
        alertData.setType(rule.getAlertDataType());
        //批量保存数据了，由于多个人
        List<AlertData> list = allUserIds.stream().map(userId -> {
            //再次拷贝
            AlertData dBalertData = BeanUtil.toBean(alertData, AlertData.class);
            dBalertData.setUserId(userId);
            dBalertData.setId(null);
            return dBalertData;

        }).collect(Collectors.toList());
        //批量保存
        alertDataService.saveBatch(list);
        return list;
    }
}
