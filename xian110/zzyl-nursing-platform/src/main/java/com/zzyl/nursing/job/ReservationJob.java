package com.zzyl.nursing.job;

import com.zzyl.nursing.service.IReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReservationJob {


    @Autowired
    private IReservationService reservationService;

    public void updateReservationStatus(){
        reservationService.updateReservationStatus();
        log.info("预约状态更新成功");
    }
}
