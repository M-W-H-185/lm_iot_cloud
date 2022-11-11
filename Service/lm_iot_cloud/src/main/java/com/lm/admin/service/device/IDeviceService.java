package com.lm.admin.service.device;

import com.lm.admin.entity.bo.device.DeviceBo;
import com.lm.admin.entity.bo.device.DeviceIdentifierAndNameDataBo;
import com.lm.admin.entity.bo.device.DeviceModelAndNewDataBo;
import com.lm.admin.entity.dto.device.DeviceAuthDto;
import com.lm.admin.entity.dto.device.DeviceNewDataDto;
import com.lm.admin.entity.vo.device.DevicePageVo;
import com.lm.admin.utils.mybiats.Pager;

import java.util.List;
import java.util.Map;

/**
*  设备信息服务类
* @author Lm
* @since 2022-09-23
*/
public interface IDeviceService {
    /**
     * 根据sn码查询到设备信息 - 用于鉴权
     * @param sn
     * @return
     */
    DeviceAuthDto getDeviceBySn(String sn);
    /**
     * 根据sn码查询到设备信息
     * @param sn
     * @return
     */
    DeviceBo getDeviceBoBySn(String sn);

    /**
     * 保存设备数据
     * @param sn sn码
     * @param dataMap 设备数据集合
     * @return
     */
    int saveDeviceData(String sn, Map<String, String> dataMap);

    /**
     * 保存设备最新数据使用redis
     *
     * @param deviceNewDataDto
     * @return
     */
    boolean saveDeviceDataRedis(DeviceNewDataDto deviceNewDataDto);

    // 获取设备最新数据 td
    List<DeviceIdentifierAndNameDataBo> getDeviceNewData(String sn);

    // 获取设备最新数据 redis
    List<DeviceModelAndNewDataBo> getDeviceNewDataRedis(String sn);

    /**
     * 分页查询
     * @param devicePageVo 分页对象
     * @return
     */
    Pager<DeviceBo> getDevicePager(DevicePageVo devicePageVo);

    /**
     * 根据设备分组id查询到对应的设备信息列表
     * @param gid 分组id
     * @return
     */
    List<DeviceBo> getDevicesByGroupingId(Long gid);

}
