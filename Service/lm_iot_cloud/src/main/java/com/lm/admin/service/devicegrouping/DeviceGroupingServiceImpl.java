package com.lm.admin.service.devicegrouping;

import com.lm.admin.entity.bo.device.DeviceBo;
import com.lm.admin.entity.bo.device.DeviceIdentifierAndNameDataBo;
import com.lm.admin.entity.dto.device.DeviceAuthDto;
import com.lm.admin.entity.pojo.devicegrouping.DeviceGrouping;
import com.lm.admin.entity.vo.device.DevicePageVo;
import com.lm.admin.entity.vo.devicegrouping.DeviceGroupingPageVo;
import com.lm.admin.utils.mybiats.Pager;

import java.util.List;
import java.util.Map;

/**
*  设备分组服务类
* @author Lm
* @since 2022-09-23
*/
public interface DeviceGroupingServiceImpl {
    /**
     * 分页查询
     * @param deviceGroupingPageVo 分页对象
     * @return
     */
    Pager<DeviceGrouping> getDeviceGroupingPager(DeviceGroupingPageVo deviceGroupingPageVo);


}
