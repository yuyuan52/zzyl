package com.zzyl.nursing.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.nursing.domain.*;
import com.zzyl.nursing.dto.CheckInApplyDto;
import com.zzyl.nursing.dto.CheckInContractDto;
import com.zzyl.nursing.dto.CheckInElderDto;
import com.zzyl.nursing.mapper.*;
import com.zzyl.nursing.util.CodeGenerator;
import com.zzyl.nursing.util.IDCardUtils;
import com.zzyl.nursing.vo.CheckInConfigVo;
import com.zzyl.nursing.vo.CheckInDetailVo;
import com.zzyl.nursing.vo.CheckInElderVo;
import com.zzyl.nursing.vo.ElderFamilyVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.service.ICheckInService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
/**
 * 入住Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-09-13
 */
@Service
public class CheckInServiceImpl extends ServiceImpl<CheckInMapper,CheckIn> implements ICheckInService
{
    @Autowired
    private CheckInMapper checkInMapper;

    @Autowired
    private ElderMapper elderMapper;

    @Autowired
    private BedMapper bedMapper;

    /**
     * 查看入住详情
     *
     * @param id
     * @return
     */
    @Override
    public CheckInDetailVo detail(Long id) {
        CheckInDetailVo vo = new CheckInDetailVo();
        //老人相关的
        //通过id找到入住配置   拿到老人id
        CheckIn checkIn = checkInMapper.selectCheckInById(id);

        Elder elder = elderMapper.selectById(checkIn.getElderId());
        if(ObjectUtil.isNotEmpty(elder)){
            CheckInElderVo checkInElderVo = new CheckInElderVo();
            BeanUtils.copyProperties(elder,checkInElderVo);
            //补全年龄属性,通过身份证号来计算老人的年龄，工具类可以使用AI协助创建
            checkInElderVo.setAge(IDCardUtils.getAgeByIdCard(elder.getIdCardNo()));
            vo.setCheckInElderVo(checkInElderVo);
        }


        //入住配置
        LambdaQueryWrapper<CheckInConfig> checkInConfigWrapper = new LambdaQueryWrapper<>();
        checkInConfigWrapper.eq(CheckInConfig::getCheckInId,checkIn.getId());
        CheckInConfig checkInConfig = checkInConfigMapper.selectOne(checkInConfigWrapper);
        //属性拷贝，BeanUtil.toBean：拷贝数据并创建对象（糊涂工具提供）
        CheckInConfigVo checkInConfigVo = BeanUtil.toBean(checkInConfig, CheckInConfigVo.class);

        /*CheckInConfigVo  cif = new CheckInConfigVo();
        BeanUtils.copyProperties(checkInConfig,cif);*/

        //补全其他属性
        checkInConfigVo.setBedNumber(checkIn.getBedNumber());
        checkInConfigVo.setStartDate(checkIn.getStartDate());
        checkInConfigVo.setEndDate(checkIn.getEndDate());

        vo.setCheckInConfigVo(checkInConfigVo);




        //合同
        LambdaQueryWrapper<Contract> contractWrapper = new LambdaQueryWrapper<>();
        contractWrapper.eq(Contract::getElderId,elder.getId());
        Contract contract = contractMapper.selectOne(contractWrapper);
        vo.setContract(contract);

        //家属列表
        String otherApplyInfo = checkIn.getRemark();
        List<ElderFamilyVo> elderFamilyVos = JSON.parseArray(otherApplyInfo, ElderFamilyVo.class);
        vo.setElderFamilyVoList(elderFamilyVos);

        return vo;
    }

    /**
     * 申请入住
     * @param checkInApplyDto
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void apply(CheckInApplyDto checkInApplyDto) {
        try {
            //判断老人是否入住，假如老人已经入住，提示：老人已入住  结束程序
            LambdaQueryWrapper<Elder> elderLambdaQueryWrapper = new LambdaQueryWrapper<>();
            //根据身份证号查询
            elderLambdaQueryWrapper.eq(Elder::getIdCardNo,checkInApplyDto.getCheckInElderDto().getIdCardNo());
            elderLambdaQueryWrapper.eq(Elder::getStatus,1);
            Elder elder = elderMapper.selectOne(elderLambdaQueryWrapper);
            if(ObjectUtil.isNotEmpty(elder)){
                throw new BaseException("老人已入住");
            }

            //更新老人选择的床位状态
            Bed bed = bedMapper.selectById(checkInApplyDto.getCheckInConfigDto().getBedId());
            bed.setBedStatus(1);
            bedMapper.updateById(bed);

            //更新或新增老人
            elder = insertOrUpdateElder(bed, checkInApplyDto.getCheckInElderDto());

            //新增签约办理信息  合同
            //生成一个合同编号
            String contractNo = "HT"+CodeGenerator.generateContractNumber();
            insertContract(elder,contractNo,checkInApplyDto);

            //新增入住信息  check_in
            CheckIn checkIn = inserCheckIn(elder, bed, checkInApplyDto);

            //新增入住配置信息  check_in_config
            insertCheckInConfig(checkIn,checkInApplyDto);
        } catch (BaseException e) {
            throw new BaseException("入住失败");
        }
    }

    @Autowired
    private CheckInConfigMapper checkInConfigMapper;

    /**
     * 新增入住配置信息
     * @param checkIn
     * @param checkInApplyDto
     */
    private void insertCheckInConfig(CheckIn checkIn, CheckInApplyDto checkInApplyDto) {
        //属性拷贝
        CheckInConfig checkInConfig = new CheckInConfig();
        BeanUtils.copyProperties(checkInApplyDto.getCheckInConfigDto(),checkInConfig);
        checkInConfig.setCheckInId(checkIn.getId());
        checkInConfigMapper.insert(checkInConfig);
    }

    /**
     * 新增入住信息
     * @param elder
     * @param bed
     * @param checkInApplyDto
     */
    private CheckIn inserCheckIn(Elder elder, Bed bed, CheckInApplyDto checkInApplyDto) {
        CheckIn checkIn = new CheckIn();
        checkIn.setElderName(elder.getName());
        checkIn.setElderId(elder.getId());
        checkIn.setIdCardNo(elder.getIdCardNo());
        checkIn.setStartDate(checkInApplyDto.getCheckInConfigDto().getStartDate());
        checkIn.setEndDate(checkInApplyDto.getCheckInConfigDto().getEndDate());
        checkIn.setNursingLevelName(checkInApplyDto.getCheckInConfigDto().getNursingLevelName());
        checkIn.setStatus(0);
        checkIn.setBedNumber(bed.getBedNumber());
        //家属列表，，转换为json存在remark字段
        checkIn.setRemark(JSONUtil.toJsonStr(checkInApplyDto.getElderFamilyDtoList()));

        checkInMapper.insert(checkIn);
        return checkIn;
    }

    @Autowired
    private ContractMapper contractMapper;

    /**
     * 保存合同数据
     * @param elder
     * @param contractNo
     * @param checkInApplyDto
     */
    private void insertContract(Elder elder, String contractNo, CheckInApplyDto checkInApplyDto) {

        //属性拷贝
        CheckInContractDto checkInContractDto = checkInApplyDto.getCheckInContractDto();
        Contract contract = new Contract();
        BeanUtils.copyProperties(checkInContractDto,contract);
        //老人相关的，合同编号、  时间  状态
        contract.setElderId(elder.getId());
        contract.setElderName(elder.getName());
        contract.setContractNumber(contractNo);
        LocalDateTime startDate = checkInApplyDto.getCheckInConfigDto().getStartDate();
        LocalDateTime endDate = checkInApplyDto.getCheckInConfigDto().getEndDate();
        contract.setStartDate(startDate);
        contract.setEndDate(endDate);

        //设置状态
        Integer status = startDate.isAfter(LocalDateTime.now())? 1 : 0;
        contract.setStatus(status);
        contractMapper.insert(contract);

    }

    /**
     * 新增或更新老人
     * @param bed
     * @param checkInElderDto
     */
    private Elder insertOrUpdateElder(Bed bed, CheckInElderDto checkInElderDto) {
        //属性拷贝
        Elder elder = new Elder();
        BeanUtils.copyProperties(checkInElderDto, elder);
        elder.setBedNumber(bed.getBedNumber());
        elder.setBedId(bed.getId());
        elder.setStatus(1);

        //保存吗
        LambdaQueryWrapper<Elder> elderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据身份证号查询
        elderLambdaQueryWrapper.eq(Elder::getIdCardNo,checkInElderDto.getIdCardNo());
        elderLambdaQueryWrapper.eq(Elder::getStatus,0);
        Elder elderDb = elderMapper.selectOne(elderLambdaQueryWrapper);
        if(ObjectUtil.isNotEmpty(elderDb)){
            elder.setId(elderDb.getId());
            //更新
            elderMapper.updateById(elder);
        }else {
            //新增
            elderMapper.insert(elder);
        }

        return elder;

    }

    /**
     * 查询入住
     * 
     * @param id 入住主键
     * @return 入住
     */
    @Override
    public CheckIn selectCheckInById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询入住列表
     * 
     * @param checkIn 入住
     * @return 入住
     */
    @Override
    public List<CheckIn> selectCheckInList(CheckIn checkIn)
    {
        return checkInMapper.selectCheckInList(checkIn);
    }

    /**
     * 新增入住
     * 
     * @param checkIn 入住
     * @return 结果
     */
    @Override
    public int insertCheckIn(CheckIn checkIn)
    {
        return save(checkIn)?1:0;
    }

    /**
     * 修改入住
     * 
     * @param checkIn 入住
     * @return 结果
     */
    @Override
    public int updateCheckIn(CheckIn checkIn)
    {
        return updateById(checkIn)?1:0;
    }

    /**
     * 批量删除入住
     * 
     * @param ids 需要删除的入住主键
     * @return 结果
     */
    @Override
    public int deleteCheckInByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids))?1:0;
    }

    /**
     * 删除入住信息
     * 
     * @param id 入住主键
     * @return 结果
     */
    @Override
    public int deleteCheckInById(Long id)
    {
        return removeById(id)?1:0;
    }

}
