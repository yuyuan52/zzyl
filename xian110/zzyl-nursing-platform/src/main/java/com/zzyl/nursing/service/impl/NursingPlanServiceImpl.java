package com.zzyl.nursing.service.impl;

import java.util.List;

import com.zzyl.common.utils.DateUtils;
import com.zzyl.nursing.dto.NursingPlanDto;
import com.zzyl.nursing.mapper.NursingProjectPlanMapper;
import com.zzyl.nursing.vo.NursingPlanVo;
import com.zzyl.nursing.vo.NursingProjectPlanVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.NursingPlanMapper;
import com.zzyl.nursing.domain.NursingPlan;
import com.zzyl.nursing.service.INursingPlanService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 护理计划Service业务层处理
 *
 * @author ruoyi
 * @date 2024-09-07
 */
@Transactional
@Service
public class NursingPlanServiceImpl implements INursingPlanService {
    @Autowired
    private NursingPlanMapper nursingPlanMapper;

    /**
     * 查询护理计划
     *
     * @param id 护理计划主键
     * @return 护理计划
     */
    @Override
    public NursingPlanVo selectNursingPlanById(Long id) {
        //查询护理计划
        NursingPlan nursingPlan = nursingPlanMapper.selectNursingPlanById(id);
        NursingPlanVo nursingPlanVo = new NursingPlanVo();
        BeanUtils.copyProperties(nursingPlan, nursingPlanVo);

        //根据护理计划的id查询对应的护理项目列表
        List<NursingProjectPlanVo> list = nursingProjectPlanMapper.selectByPlanId(id);
        nursingPlanVo.setProjectPlans(list);

        return nursingPlanVo;
    }

    /**
     * 查询护理计划列表
     *
     * @param nursingPlan 护理计划
     * @return 护理计划
     */
    @Override
    public List<NursingPlan> selectNursingPlanList(NursingPlan nursingPlan) {
        return nursingPlanMapper.selectNursingPlanList(nursingPlan);
    }

    @Autowired
    private NursingProjectPlanMapper nursingProjectPlanMapper;

    /**
     * 新增护理计划
     *
     * @param dto 护理计划
     * @return 结果
     */

    @Override
    public int insertNursingPlan(NursingPlanDto dto) {
        try {
            //保存护理计划  属性拷贝(属性名称和类型一致，就可以进行拷贝)
            NursingPlan nursingPlan = new NursingPlan();
            BeanUtils.copyProperties(dto, nursingPlan);

            nursingPlan.setCreateTime(DateUtils.getNowDate());
            nursingPlanMapper.insertNursingPlan(nursingPlan);

            //保存护理计划与护理项目的中间表  批量
            int count = nursingProjectPlanMapper.batchInsert(dto.getProjectPlans(), nursingPlan.getId());
            /*dto.getProjectPlans().forEach(x->{
                nursingProjectPlanMapper.insertNursingProjectPlan(x);
            });
*/
            return count == 0 ? 0 : 1;
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改护理计划
     *
     * @param dto 护理计划
     * @return 结果
     */
    @Override
    public int updateNursingPlan(NursingPlanDto dto) {
        try {
            // 属性拷贝
            NursingPlan nursingPlan = new NursingPlan();
            BeanUtils.copyProperties(dto, nursingPlan);

            //判断dto的list（护理计划与护理项目的关联关系）是否为空，不为空，删除之前的关联关系，再重新批量添加
            if (dto.getProjectPlans() != null && dto.getProjectPlans().size() > 0) {
                //删除之前的关联关系  根据护理计划ID删除
                nursingProjectPlanMapper.deleteByPlanId(nursingPlan.getId());
                //批量添加
                nursingProjectPlanMapper.batchInsert(dto.getProjectPlans(), nursingPlan.getId());
            }

            //不管dto的list空不空，都要修改护理计划
            return nursingPlanMapper.updateNursingPlan(nursingPlan);
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量删除护理计划
     *
     * @param ids 需要删除的护理计划主键
     * @return 结果
     */
    @Override
    public int deleteNursingPlanByIds(Long[] ids) {
        return nursingPlanMapper.deleteNursingPlanByIds(ids);
    }

    /**
     * 删除护理计划信息
     *
     * @param id 护理计划主键
     * @return 结果
     */
    @Override
    public int deleteNursingPlanById(Long id) {
        return nursingPlanMapper.deleteNursingPlanById(id);
    }

    /**
     * 查询所有护理计划
     * @return
     */
    @Override
    public List<NursingPlan> listAll() {
        return nursingPlanMapper.listAll();
    }
}
