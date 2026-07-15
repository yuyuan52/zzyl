package com.zzyl.nursing.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.UserThreadLocal;
import com.zzyl.nursing.domain.Reservation;
import com.zzyl.nursing.dto.ReservationDto;
import com.zzyl.nursing.mapper.ReservationMapper;
import com.zzyl.nursing.service.IReservationService;
import com.zzyl.nursing.vo.TimeCountVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 预约信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-06-07
 */
@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, Reservation> implements IReservationService
{
    @Autowired
    private ReservationMapper reservationMapper;

    /**
     * 查询每个时间段剩余预约次数
     * @return
     */
    @Override
    public List<TimeCountVo> countReservationsForTime(Long time) {

        //2024-09-21
        LocalDateTime localDateTime = LocalDateTimeUtil.of(time);
        //2024-09-21 00:00:00
        LocalDateTime startTime = localDateTime.toLocalDate().atStartOfDay();
        //2024-09-22 00:00:00
        LocalDateTime endTime = startTime.plusHours(24);
        List<TimeCountVo> timeCountVoList = reservationMapper.countReservationsForTime(startTime,endTime);
        return timeCountVoList;
    }

    /**
     * 查询取消预约次数
     * @param userId
     * @return
     */
    @Override
    public int getCancelledCount(Long userId) {

        LocalDateTime startTime = LocalDate.now().atStartOfDay();

        LocalDateTime endTime = startTime.plusHours(24);

        LambdaQueryWrapper<Reservation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Reservation::getUpdateBy,userId);
        lambdaQueryWrapper.between(Reservation::getUpdateTime,startTime,endTime);

        return (int) count(lambdaQueryWrapper);
    }

    /**
     * 分页查询预约信息
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public TableDataInfo findByPage(int pageNum, int pageSize, Integer status) {

        Long userId = UserThreadLocal.getUserId();
        if(ObjectUtil.isEmpty(userId)){
            throw new BaseException("请先登录");
        }
        LambdaQueryWrapper<Reservation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Reservation::getCreateBy,userId);
        lambdaQueryWrapper.eq(ObjectUtil.isNotEmpty(status),Reservation::getStatus,status);
        lambdaQueryWrapper.orderByDesc(Reservation::getCreateTime);
        Page page = new Page<>(pageNum,pageSize);
        page = page(page,lambdaQueryWrapper);

        return createTableDataInfo(page);
    }

    private TableDataInfo createTableDataInfo(Page page) {

        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setRows(page.getRecords());
        tableDataInfo.setTotal(page.getTotal());
        tableDataInfo.setMsg("请求成功");
        tableDataInfo.setCode(200);
        return  tableDataInfo;
    }

    /**
     * 取消预约
     * @param id
     * @return
     */
    @Override
    public int cancelReservation(Long id) {

        Reservation reservation = getById(id);
        if(ObjectUtil.isEmpty(reservation)){
            throw new BaseException("预约不存在");
        }
        reservation.setStatus(2);
        reservation.setUpdateBy(UserThreadLocal.getUserId()+"");
        return updateById(reservation) ? 1 : 0;
    }

    /**
     * 查询预约信息
     * 
     * @param id 预约信息主键
     * @return 预约信息
     */
    @Override
    public Reservation selectReservationById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询预约信息列表
     * 
     * @param reservation 预约信息
     * @return 预约信息
     */
    @Override
    public List<Reservation> selectReservationList(Reservation reservation)
    {
        return reservationMapper.selectReservationList(reservation);
    }

    /**
     * 新增预约信息
     * 
     * @param reservationDto 预约信息
     * @return 结果
     */
    @Override
    public int insertReservation(ReservationDto reservationDto)
    {
        Long userId = UserThreadLocal.getUserId();
        Reservation reservation = BeanUtil.toBean(reservationDto, Reservation.class);
        reservation.setCreateBy(userId+"");
        reservation.setUpdateBy(userId+"");
        reservation.setStatus(0);
        return save(reservation) ? 1 : 0;
    }

    /**
     * 修改预约信息
     * 
     * @param reservation 预约信息
     * @return 结果
     */
    @Override
    public int updateReservation(Reservation reservation)
    {
        return updateById(reservation) ? 1 : 0;
    }

    /**
     * 批量删除预约信息
     * 
     * @param ids 需要删除的预约信息主键
     * @return 结果
     */
    @Override
    public int deleteReservationByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids)) ? 1 : 0;
    }

    /**
     * 删除预约信息信息
     * 
     * @param id 预约信息主键
     * @return 结果
     */
    @Override
    public int deleteReservationById(Long id)
    {
        return removeById(id) ? 1 : 0;
    }

    @Override
    public void updateReservationStatus() {
        //查询符合条件的预约信息，已经过期，还没有来的
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<Reservation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Reservation::getStatus,0);
        lambdaQueryWrapper.lt(Reservation::getTime,now);
        List<Reservation> list = list(lambdaQueryWrapper);
        if(CollUtil.isNotEmpty(list)){
            //获取所有的ID,并且转换为集合
            List<Long> ids = list.stream().map(Reservation::getId).collect(Collectors.toList());
            /*List<Long> list2 = new ArrayList();
            list.forEach(item->{
                list2.add(item.getId());
            });*/

            //批量更新
            reservationMapper.batchUpdateStatus(ids);
        }
    }
}
