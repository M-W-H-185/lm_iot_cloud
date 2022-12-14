package com.lm.admin.controller.device_api.device;

import com.lm.admin.common.r.DeviceResultEnum;
import com.lm.admin.controller.device_api.DeviceBaseController;
import com.lm.admin.entity.bo.device.DeviceSelectBo;
import com.lm.admin.entity.dto.user.UserHeader;
import com.lm.admin.entity.vo.device.*;
import com.lm.admin.entity.bo.device.DeviceBo;
import com.lm.admin.entity.bo.device.DeviceModelAndNewDataBo;
import com.lm.admin.service.device.DeviceServiceImpl;
import com.lm.admin.service.device.IDeviceService;
import com.lm.admin.service.devicedata.DeviceDataServiceImpl;
import com.lm.admin.utils.LmAssert;
import com.lm.admin.utils.lmthreadlocal.RoleThreadLocal;
import com.lm.admin.utils.mybiats.Pager;
import com.lm.cloud.tcp.service.utils.DeviceCmdUtils;
import com.lm.cloud.tcp.service.utils.RedisDeviceUtils;
import com.lm.common.redis.devicekey.CloudRedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    private DeviceServiceImpl deviceService;
    @Autowired
    private DeviceDataServiceImpl deviceDataService;

    private UserHeader userHeader;

    // 按照名字去匹配 不能用Autowired因为用类型匹配的
    @Resource(name = "fastjson2RedisTemplate")
    private RedisTemplate redisTemplate;  // 操作Redis
    /**
     * 获取该设备的最新数据
     * path : /api/device/newData/{sn}
     * return DeviceDataTdBo
     **/
    @PostMapping("/newdata/{sn}")
    public List<DeviceModelAndNewDataBo> newData(@PathVariable("sn") String sn){
//        log.info("----->{}",sn);
        return deviceDataService.getDeviceNewDataRedis(sn);
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
     * path: /api/device/list
     * @return 分页数据
     */
    @PostMapping("/list")
    public List<DeviceBo> list(){
        return deviceService.getDeviceList();
    }

    /**
     * 根据设备sn查询到设备信息
     * @param sn
     * @return
     */
    @PostMapping("/queryById/{sn}")
    public DeviceBo getDeviceById(@PathVariable("sn") String sn){
        return deviceService.getDeviceBoBySn(sn);
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
     * 添加一个设备
     * path: /api/device/add
     * @param deviceSaveVo
     * @return
     */
    @PostMapping("/add")
    public int addDevice(@RequestBody DeviceSaveVo deviceSaveVo){
        return deviceService.addDevice(deviceSaveVo);
    }

    /**
     * 修改设备信息
     * path: /api/device/upd
     * @param deviceUpdateVo
     * @return
     */
    @PostMapping("/upd")
    public int updateDevice(@RequestBody DeviceUpdateVo deviceUpdateVo){
        return deviceService.updateDevice(deviceUpdateVo);
    }

    /**
     * 删除设备信息根据设备id
     * path: /api/device/del/{did}
     * @param did
     * @return
     */
    @PostMapping("/del/{did}")
    public int delDevice(@PathVariable Long did){
        return deviceService.delDeviceById(did);
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

    /**
     * 获取设备sn和设备名称列表
     * @return
     */
    @PostMapping("/snname")
    public List<DeviceSelectBo> getSnName(){
        return deviceService.getDeviceIdSnName();
    }

    @PostMapping("/allupcount")
    public Long getDeviceDataUpCount(@RequestBody DeviceIdSnVo deviceIdSnVo){
        return deviceDataService.getDeviceDataUpCount(deviceIdSnVo.getSn());
    }

    @PostMapping("/thisdayupcount")
    public Long getThisDayDeviceDataUpCount(@RequestBody DeviceIdSnVo deviceIdSnVo){
        return deviceDataService.getThisDayDeviceDataUpCount(deviceIdSnVo.getSn());
    }

    @PostMapping("/devicecount")
    public Integer getDeviceCount(){
        return  deviceService.getDeviceCount();
    }
    // 查询设备上线数量
    @PostMapping("/deviceupcount")
    public Integer getDeviceUpCount(){
        //
        Set<String> keys = redisTemplate.keys(CloudRedisKey.DeviceSnToChannelIdKey + "*");
        List<String> keyList = new ArrayList<>();

        keys.forEach(item->{
            String a = item.split(":")[5];
            keyList.add(a);
        });

        log.info("---->{}",keyList.toString());
        userHeader = RoleThreadLocal.get();
        int isUpCount = 0;

        List<DeviceSelectBo> deviceIdSnNameList = deviceService.getDeviceIdSnName();

        if(userHeader.getUserRoleCode()!=null && userHeader.getUserRoleCode().equals("CLOUD_USER")){
            for (DeviceSelectBo deviceSelectBo : deviceIdSnNameList) {
                if(keyList.contains(deviceSelectBo.getSn()) == true){
                    isUpCount++;
                }
            }
            return isUpCount;
        }
        else if(userHeader.getUserRoleCode()!=null && userHeader.getUserRoleCode().equals("CLOUD_ADMIN")){
            return keys.size();
        }
        return 0;
    }
}
