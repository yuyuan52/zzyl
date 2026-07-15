package com.zzyl.nursing.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zzyl.common.annotation.Log;
import com.zzyl.common.core.controller.BaseController;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.common.enums.BusinessType;
import com.zzyl.nursing.domain.AlertData;
import com.zzyl.nursing.service.IAlertDataService;
import com.zzyl.common.utils.poi.ExcelUtil;
import com.zzyl.common.core.page.TableDataInfo;

/**
 * 报警数据Controller
 * 
 * @author ruoyi
 * @date 2024-09-25
 */
@RestController
@RequestMapping("/nursing/alertData")
@Api(tags = "报警数据的接口")
public class AlertDataController extends BaseController
{
    @Autowired
    private IAlertDataService alertDataService;

    /**
     * 查询报警数据列表
     */
    @ApiOperation("查询报警数据列表")
    @GetMapping("/list")
    public TableDataInfo list(AlertData alertData)
    {
        startPage();
        List<AlertData> list = alertDataService.selectAlertDataList(alertData);
        return getDataTable(list);
    }

    /**
     * 获取报警数据详细信息
     */
    @ApiOperation("获取报警数据详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@ApiParam(value = "报警数据ID", required = true)
            @PathVariable("id") Long id)
    {
        return success(alertDataService.selectAlertDataById(id));
    }

    /**
     * 新增报警数据
     */
    @ApiOperation("新增报警数据")
    @Log(title = "报警数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@ApiParam(value = "报警数据实体")
            @RequestBody AlertData alertData)
    {
        return toAjax(alertDataService.insertAlertData(alertData));
    }

    /**
     * 修改报警数据
     */
    @ApiOperation("修改报警数据")
    @Log(title = "报警数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@ApiParam(value = "报警数据实体")
            @RequestBody AlertData alertData)
    {
        return toAjax(alertDataService.updateAlertData(alertData));
    }

    /**
     * 删除报警数据
     */
    @ApiOperation("删除报警数据")
    @Log(title = "报警数据", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(alertDataService.deleteAlertDataByIds(ids));
    }
}
