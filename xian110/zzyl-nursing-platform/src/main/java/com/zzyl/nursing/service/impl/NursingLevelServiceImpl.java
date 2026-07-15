package com.zzyl.nursing.service.impl;

import java.util.List;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.nursing.vo.NursingLevelVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.NursingLevelMapper;
import com.zzyl.nursing.domain.NursingLevel;
import com.zzyl.nursing.service.INursingLevelService;

/**
 * 护理等级Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-09-07
 */
@Service
public class NursingLevelServiceImpl implements INursingLevelService 
{
    @Autowired
    private NursingLevelMapper nursingLevelMapper;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    private static String KEY = "nursing:all";

    /**
     * 查询护理等级
     * 
     * @param id 护理等级主键
     * @return 护理等级
     */
    @Override
    public NursingLevel selectNursingLevelById(Long id)
    {
        return nursingLevelMapper.selectNursingLevelById(id);
    }

    /**
     * 查询护理等级列表
     * 
     * @param nursingLevel 护理等级
     * @return 护理等级
     */
    @Override
    public List<NursingLevelVo> selectNursingLevelList(NursingLevel nursingLevel)
    {
        return nursingLevelMapper.selectNursingLevelList(nursingLevel);
    }

    /**
     * 新增护理等级
     * 
     * @param nursingLevel 护理等级
     * @return 结果
     */
    @Override
    public int insertNursingLevel(NursingLevel nursingLevel)
    {
        nursingLevel.setCreateTime(DateUtils.getNowDate());
        int flag = nursingLevelMapper.insertNursingLevel(nursingLevel);
        //删除缓存
        deleteCache();

        return flag;
    }

    /**
     * 删除缓存
     */
    private void deleteCache(){
        redisTemplate.delete(KEY);
    }

    /**
     * 修改护理等级
     * 
     * @param nursingLevel 护理等级
     * @return 结果
     */
    @Override
    public int updateNursingLevel(NursingLevel nursingLevel)
    {
        nursingLevel.setUpdateTime(DateUtils.getNowDate());
        int flag = nursingLevelMapper.updateNursingLevel(nursingLevel);
        //删除缓存
        deleteCache();
        return flag;
    }

    /**
     * 批量删除护理等级
     * 
     * @param ids 需要删除的护理等级主键
     * @return 结果
     */
    @Override
    public int deleteNursingLevelByIds(Long[] ids)
    {
        int flag = nursingLevelMapper.deleteNursingLevelByIds(ids);
        //删除缓存
        deleteCache();
        return flag;
    }

    /**
     * 删除护理等级信息
     * 
     * @param id 护理等级主键
     * @return 结果
     */
    @Override
    public int deleteNursingLevelById(Long id)
    {
        int flag = nursingLevelMapper.deleteNursingLevelById(id);
        //删除缓存
        deleteCache();
        return flag;
    }

    /**
     * 查询所有护理等级
     * @return
     */
    @Override
    public List<NursingLevel> listAll() {
        //从缓存中获取数据
        List<NursingLevel> list = (List<NursingLevel>) redisTemplate.opsForValue().get(KEY);

        //判断缓存中是否存在数据，如果存在，则直接返回
        if(list != null && list.size() > 0){
            return list;
        }

        //缓存中不存在，查询数据库，同时把数据放到缓存中
        list = nursingLevelMapper.listAll();

        //把数据放到缓存中
        redisTemplate.opsForValue().set(KEY,list);
        return list;
    }
}
