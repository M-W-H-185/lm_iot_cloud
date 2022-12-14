package com.lm.admin.service.devicegrouping;

import com.lm.admin.entity.bo.device.DeviceBo;
import com.lm.admin.entity.bo.device.DeviceIdentifierAndNameDataBo;
import com.lm.admin.entity.dto.device.DeviceAuthDto;
import com.lm.admin.entity.pojo.devicegrouping.DeviceGrouping;
import com.lm.admin.entity.vo.device.DevicePageVo;
import com.lm.admin.entity.vo.devicegrouping.DeviceGroupingPageVo;
import com.lm.admin.entity.vo.devicegrouping.DeviceGroupingUpdateAndSaveVo;
import com.lm.admin.utils.mybiats.Pager;

import java.util.List;
import java.util.Map;

/**
*  设备分组服务类
* @author Lm
* @since 2022-09-23
*/
public interface IDeviceGroupingService {
    /**
     * 查询全部分组列表
     * @return
     */
    List<DeviceGrouping> getDeviceGroupingList();

    /**
     * 添加一个设备分组
     * @param deviceGrouping
     * @return
     */
    Integer addDeviceGrouping(DeviceGroupingUpdateAndSaveVo deviceGrouping);

    /**
     * 修改设备分组信息 并 修改分组拥有的设备
     * @param deviceGrouping
     * @return
     */
    Integer updDeviceGrouping(DeviceGroupingUpdateAndSaveVo deviceGrouping);

    /**
     * 删除一个设备分组
     * @param groupingId 分组id
     * @return
     */
    Integer delDeviceGrouping(Long groupingId);
    /**
     * 查询设备分组拥有的设备
     * @param groupingId  设备分组id
     * @return
     */
    List<Long> getGroupingOwnerDeviceById(Long groupingId);


    /**
     * 根据分组id查询到分组
     * @param groupingId  分组id
     * @return
     */
    DeviceGrouping getDeviceGroupingById(Long groupingId);
}
