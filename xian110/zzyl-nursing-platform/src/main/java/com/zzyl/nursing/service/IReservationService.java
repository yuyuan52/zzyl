package com.zzyl.nursing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.nursing.domain.Reservation;
import com.zzyl.nursing.dto.ReservationDto;
import com.zzyl.nursing.vo.TimeCountVo;

import java.util.List;

/**
 * 预约信息Service接口
 * 
 * @author ruoyi
 * @date 2024-06-07
 */
public interface IReservationService extends IService<Reservation>
{
    /**
     * 查询预约信息
     * 
     * @param id 预约信息主键
     * @return 预约信息
     */
    public Reservation selectReservationById(Long id);

    /**
     * 查询预约信息列表
     * 
     * @param reservation 预约信息
     * @return 预约信息集合
     */
    public List<Reservation> selectReservationList(Reservation reservation);

    /**
     * 新增预约信息
     * 
     * @param reservationDto 预约信息
     * @return 结果
     */
    public int insertReservation(ReservationDto reservationDto);

    /**
     * 修改预约信息
     * 
     * @param reservation 预约信息
     * @return 结果
     */
    public int updateReservation(Reservation reservation);

    /**
     * 批量删除预约信息
     * 
     * @param ids 需要删除的预约信息主键集合
     * @return 结果
     */
    public int deleteReservationByIds(Long[] ids);

    /**
     * 删除预约信息信息
     * 
     * @param id 预约信息主键
     * @return 结果
     */
    public int deleteReservationById(Long id);

    /**
     * 查询每个时间段剩余预约次数
     * @return
     */
    List<TimeCountVo> countReservationsForTime(Long time);

    /**
     * 查询取消预约次数
     * @param userId
     * @return
     */
    int getCancelledCount(Long userId);

    /**
     * 分页查询预约信息
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    TableDataInfo findByPage(int pageNum, int pageSize, Integer status);

    /**
     * 取消预约
     * @param id
     * @return
     */
    int cancelReservation(Long id);

    void updateReservationStatus();
}
