package com.zzyl.nursing.service.impl;

import java.util.List;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzyl.common.constant.HttpStatus;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.ElderMapper;
import com.zzyl.nursing.domain.Elder;
import com.zzyl.nursing.service.IElderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Arrays;
/**
 * 老人Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-09-13
 */
@Service
public class ElderServiceImpl extends ServiceImpl<ElderMapper,Elder> implements IElderService
{
    @Autowired
    private ElderMapper elderMapper;

    /**
     * 分页条件查询老人列表
     * @param status
     * @param pageNum
     * @param pageSize
     * @param name
     * @param idCardNo
     * @return
     */
    @Override
    public TableDataInfo pageQuery(Integer status, Integer pageNum, Integer pageSize, String name, String idCardNo) {
        //使用MP 分页条件查询
        IPage<Elder> page = new Page<>(pageNum,pageSize);
        //构建条件
        LambdaQueryWrapper<Elder> queryWrapper = new LambdaQueryWrapper<>();
        //按照状态精确查询
        if(ObjectUtil.isNotEmpty(status)){
            queryWrapper.eq(Elder::getStatus,status);
        }
        //按照姓名模糊查询
        if(ObjectUtil.isNotEmpty(name)){
            queryWrapper.like(Elder::getName,name);
        }
        //按照身份证号精确查询
        if(ObjectUtil.isNotEmpty(idCardNo)){
            queryWrapper.eq(Elder::getIdCardNo,idCardNo);
        }
        //只展示select id,name,idCardNo,bedNuber from elder
        queryWrapper.select(Elder::getId,Elder::getName,Elder::getIdCardNo,Elder::getBedNumber);
        //查询结果
        page = page(page, queryWrapper);

        return getTableDataInfo(page);
    }

    /**
     * 封装返回结果
     * @param page
     * @return
     */
    private TableDataInfo getTableDataInfo(IPage<Elder> page) {
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(HttpStatus.SUCCESS);
        tableDataInfo.setMsg("查询成功");
        tableDataInfo.setRows(page.getRecords());
        tableDataInfo.setTotal(page.getTotal());
        return tableDataInfo;
    }

    /**
     * 查询老人
     * 
     * @param id 老人主键
     * @return 老人
     */
    @Override
    public Elder selectElderById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询老人列表
     * 
     * @param elder 老人
     * @return 老人
     */
    @Override
    public List<Elder> selectElderList(Elder elder)
    {
        return elderMapper.selectElderList(elder);
    }

    /**
     * 新增老人
     * 
     * @param elder 老人
     * @return 结果
     */
    @Override
    public int insertElder(Elder elder)
    {
        return save(elder)?1:0;
    }

    /**
     * 修改老人
     * 
     * @param elder 老人
     * @return 结果
     */
    @Override
    public int updateElder(Elder elder)
    {
        return updateById(elder)?1:0;
    }

    /**
     * 批量删除老人
     * 
     * @param ids 需要删除的老人主键
     * @return 结果
     */
    @Override
    public int deleteElderByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids))?1:0;
    }

    /**
     * 删除老人信息
     * 
     * @param id 老人主键
     * @return 结果
     */
    @Override
    public int deleteElderById(Long id)
    {
        return removeById(id)?1:0;
    }

}
