package com.zzyl.nursing.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzyl.common.constant.HttpStatus;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.nursing.vo.NursingProjectVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.NursingProjectMapper;
import com.zzyl.nursing.domain.NursingProject;
import com.zzyl.nursing.service.INursingProjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Arrays;
/**
 * 护理项目Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-09-12
 */
@Service
public class NursingProjectServiceImpl extends ServiceImpl<NursingProjectMapper,NursingProject> implements INursingProjectService
{
    @Autowired
    private NursingProjectMapper nursingProjectMapper;

    /**
     * 查询护理项目
     * 
     * @param id 护理项目主键
     * @return 护理项目
     */
    @Override
    public NursingProject selectNursingProjectById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询护理项目列表
     * 
     * @param nursingProject 护理项目
     * @return 护理项目
     */
    @Override
    public List<NursingProject> selectNursingProjectList(NursingProject nursingProject)
    {
        return nursingProjectMapper.selectNursingProjectList(nursingProject);
    }

    /**
     * 新增护理项目
     * 
     * @param nursingProject 护理项目
     * @return 结果
     */
    @Override
    public int insertNursingProject(NursingProject nursingProject)
    {
//        nursingProject.setCreateTime(DateUtils.getNowDate());
        return save(nursingProject)?1:0;
    }

    /**
     * 修改护理项目
     * 
     * @param nursingProject 护理项目
     * @return 结果
     */
    @Override
    public int updateNursingProject(NursingProject nursingProject)
    {
//        nursingProject.setUpdateTime(DateUtils.getNowDate());
        return updateById(nursingProject)?1:0;
    }

    /**
     * 批量删除护理项目
     * 
     * @param ids 需要删除的护理项目主键
     * @return 结果
     */
    @Override
    public int deleteNursingProjectByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids))?1:0;
    }

    /**
     * 删除护理项目信息
     * 
     * @param id 护理项目主键
     * @return 结果
     */
    @Override
    public int deleteNursingProjectById(Long id)
    {
        return removeById(id)?1:0;
    }

    /**
     * 查询所有的护理项目
     * @return
     */
    @Override
    public List<NursingProjectVo> listAll() {
        return nursingProjectMapper.listAll();
    }

    /**
     * 分页查询护理项目
     * @return
     */
    @Override
    public TableDataInfo selectPage(Integer pageNum, Integer pageSize) {

        //分页查询数据
        IPage page = new Page(pageNum,pageSize);

        page = page(page, null);
        return getTableDataInfo(page);
    }

    /**
     * 封装分页信息
     * @param page
     * @return
     */
    private TableDataInfo getTableDataInfo(IPage page) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("请求成功");
        rspData.setRows(page.getRecords());
        rspData.setTotal(page.getTotal());
        return rspData;
    }
}
