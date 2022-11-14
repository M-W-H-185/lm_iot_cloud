package com.lm.admin.controller.device_api.device;

import com.lm.admin.common.r.DeviceResultEnum;
import com.lm.admin.controller.device_api.DeviceBaseController;
import com.lm.admin.entity.vo.device.DeviceCmdVo;
import com.lm.admin.entity.bo.device.DeviceBo;
import com.lm.admin.entity.bo.device.DeviceModelAndNewDataBo;
import com.lm.admin.entity.vo.device.DevicePageVo;
import com.lm.admin.service.device.IDeviceService;
import com.lm.admin.utils.LmAssert;
import com.lm.admin.utils.mybiats.Pager;
import com.lm.cloud.tcp.service.utils.DeviceCmdUtils;
import com.lm.cloud.tcp.service.utils.RedisDeviceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备信息接口
 * api:/api/device/***
 * @author Lm
 * @date 2022/10/8 15:05
 */
@Slf4j
@RestController
public class DeviceController extends DeviceBaseController {
    @Autowired
    private IDeviceService deviceService;

    /**
     * 获取该设备的最新数据
     * path : /api/device/newData/{sn}
     * return DeviceDataTdBo
     **/
    @PostMapping("/newdata/{sn}")
    public List<DeviceModelAndNewDataBo> newData(@PathVariable("sn") String sn){
//        log.info("----->{}",sn);
        return deviceService.getDeviceNewDataRedis(sn);
    }

    /**
     * 获取设备在线的数量
     * path /api/device/onLineCount
     * @return
     */
    @PostMapping("/onLineCount")
    public Long onLineCount(){
        return RedisDeviceUtils.getDeviceOnLineCount();
    }

    /**
     * 查询根据sn查询设备是否在线
     * path /api/device/online/{sn}
     * @param sn
     * @return
     */
    @PostMapping("/online/{sn}")
    public Boolean isOnLineBySn(@PathVariable("sn") String sn){
        return RedisDeviceUtils.getDeviceIsOnLienBySn(sn);
    }
    /**
     * 分页查询设备列表
     * path: /api/device/page
     * @param pager
     * @return 分页数据
     */
    @PostMapping("/page")
    public Pager<DeviceBo> listPage(@RequestBody DevicePageVo pager){
        return deviceService.getDevicePager(pager);
    }



    /**
     * 根据设备分组id查询到设备的信息列表
     * path: /api/device/groupingid/devices/{gid}
     * @param gid 分组id
     * @return List<DeviceBo> 设备数据
     */
    @PostMapping("/devices/groupingid/{gid}")
    public List<DeviceBo> deviceByGroupingId(@PathVariable("gid") Long gid){
        return deviceService.getDevicesByGroupingId(gid);
    }

    /**
     * 下发设备命令
     * @param deviceCmdVo
     * @return
     */
    @PostMapping("/cmd")
    public String cmd(@RequestBody DeviceCmdVo deviceCmdVo){
        log.info("cmd------>{}", deviceCmdVo);
        // sn码判断是否为空  空抛出异常
        LmAssert.isEmptyEx(deviceCmdVo.getSn(), DeviceResultEnum.DEVICE_SN_NULL_ERROR);
        // Data参数必须携带，值可以为空
        LmAssert.isNotNull(deviceCmdVo.getData(), DeviceResultEnum.DEVICE_DATA_NULL_ERROR);
        // 请求设备命令
        DeviceCmdUtils.requestCmd(deviceCmdVo);
        return "命令发送成功!";
    }
}
