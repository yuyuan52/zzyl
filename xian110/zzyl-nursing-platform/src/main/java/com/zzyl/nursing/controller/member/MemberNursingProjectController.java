package com.zzyl.nursing.controller.member;

import com.zzyl.common.core.controller.BaseController;
import com.zzyl.common.core.domain.R;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.nursing.domain.NursingProject;
import com.zzyl.nursing.service.INursingProjectService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member/orders")
public class MemberNursingProjectController extends BaseController {

    @Autowired
    private INursingProjectService nursingProjectService;


    /**
     * 根据编号查询护理项目信息
     *
     * @param id 护理项目编号
     * @return 护理项目信息
     */
    @GetMapping("/project/{id}")
    @ApiOperation("根据编号查询护理项目信息")
    public R<NursingProject> getById(@PathVariable("id") Long id) {
        NursingProject nursingProject = nursingProjectService.getById(id);
        return R.ok(nursingProject);
    }

    /**
     * 分页查询护理项目列表
     * @return 护理项目列表
     */
    @GetMapping("/project/page")
    @ApiOperation("分页查询护理项目列表")
    public TableDataInfo getByPage(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        TableDataInfo tableDataInfo = nursingProjectService.selectPage(pageNum,pageSize);
        return tableDataInfo;
    }
}
