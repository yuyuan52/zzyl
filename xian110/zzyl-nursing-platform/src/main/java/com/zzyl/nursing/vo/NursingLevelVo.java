package com.zzyl.nursing.vo;

import com.zzyl.nursing.domain.NursingLevel;
import lombok.Data;

@Data
public class NursingLevelVo extends NursingLevel {

    /**
     * 护理计划名称
     */
    private String planName;
}
