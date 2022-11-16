package com.lm.admin.mapper.mysql.device;

import com.lm.admin.entity.bo.devicestrategy.DeviceStrategyInfoBo;
import com.lm.admin.entity.bo.devicestrategy.DeviceStrategyListPageBo;
import com.lm.admin.entity.dto.devicestrategy.DeviceStrategyDto;
import com.lm.admin.entity.vo.devicestrategy.DeviceStrategyPageVo;
import com.lm.admin.entity.vo.devicestrategy.DeviceStrategySaveVo;
import com.lm.admin.entity.vo.devicestrategy.DeviceStrategyUpdateVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设备策略 - mysql
 * @author Lm
 * @date 2022/11/6 15:02
 */
@Mapper
public interface DeviceStrategyMapper {
    /**
     * 查询所有策略 用于定时任务的
     * @return
     */
    List<DeviceStrategyDto> findAllDeviceStrategyDto();

    /**
     * 查询全部的策略数量
     * @return
     */
    Integer findDeviceStrategyCount();


    /**
     * 策略 分页 模糊 查询
     * @param pageIndex
     * @param pageSize
     * @param keyword
     * @return List<DeviceStrategyListPageBo>
     */
    List<DeviceStrategyListPageBo> findDeviceStrategyPage(
        @Param("pageIndex") Integer pageIndex ,
        @Param("pageSize") Integer pageSize,
        @Param("keyword") String keyword
    );

    /**
     * 根据策略id查询策略信息
     * @param Sid
     * @return
     */
    DeviceStrategyInfoBo findDeviceStrategyById(@Param("Sid") Long Sid);

    /**
     * 新建一条策略信息 不带表达式
     * @param deviceStrategySaveVo
     * @return
     */
    int addDeviceStrategy(@Param("dS") DeviceStrategySaveVo deviceStrategySaveVo);

    /**
     * 更新策略信息
     * @param deviceStrategyUpdateVo
     * @return
     */
    int updateDeviceStrategy(@Param("dS") DeviceStrategyUpdateVo deviceStrategyUpdateVo);
}
