package com.zzyl.nursing.controller;

import com.aliyun.iot20180120.models.DeleteDeviceRequest;
import com.aliyun.iot20180120.models.QueryDevicePropertyStatusRequest;
import com.aliyun.iot20180120.models.QueryThingModelExtendConfigPublishedRequest;
import com.aliyun.iot20180120.models.QueryThingModelPublishedRequest;
import com.zzyl.common.core.controller.BaseController;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.nursing.domain.Device;
import com.zzyl.nursing.dto.DeviceDto;
import com.zzyl.nursing.service.IDeviceService;
import com.zzyl.nursing.vo.DeviceVo;
import com.zzyl.nursing.vo.ProductVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备Controller
 * 
 * @author ruoyi
 * @date 2024-09-21
 */
@RestController
@RequestMapping("/nursing/device")
@Api(tags = "设备的接口")
public class DeviceController extends BaseController
{
    @Autowired
    private IDeviceService deviceService;

    /**
     * 查询设备列表
     */
    @ApiOperation("查询设备列表")
    @PreAuthorize("@ss.hasPermi('nursing:device:list')")
    @GetMapping("/list")
    public TableDataInfo list(Device device)
    {
        startPage();
        List<Device> list = deviceService.selectDeviceList(device);
        return getDataTable(list);
    }

    @PostMapping("/syncProductList")
    public AjaxResult syncProductList(){
        deviceService.syncProductList();
        return success();
    }

    @GetMapping("/allProduct")
    public AjaxResult allProduct(){
        List<ProductVo> list = deviceService.allProduct();
        return success(list);
    }

    @PostMapping("/register")
    public AjaxResult registerDevice(@RequestBody DeviceDto deviceDto){
        deviceService.registerDevice(deviceDto);
        return success();
    }

    @PostMapping("/queryDeviceDetail")
    public AjaxResult queryDeviceDetail(@RequestBody DeviceDto deviceDto){
        DeviceVo deviceVo = deviceService.queryDeviceDetail(deviceDto);
        return success(deviceVo);
    }

    @PostMapping("/queryDevicePropertyStatus")
    public AjaxResult queryDevicePropertyStatus(@RequestBody QueryDevicePropertyStatusRequest request){
        return deviceService.queryDevicePropertyStatus(request);
    }

    @PostMapping("/queryThingModelPublished")
    public AjaxResult queryThingModelPublished(@RequestBody QueryThingModelPublishedRequest request){
        return deviceService.queryThingModelPublished(request);
    }

    @PutMapping
    public AjaxResult edit(@RequestBody Device device){
        return toAjax(deviceService.updateDevice(device));
    }

    @DeleteMapping
    public AjaxResult detele(@RequestBody DeleteDeviceRequest request){
        return toAjax(deviceService.deleteDeviceById(request));
    }

}
