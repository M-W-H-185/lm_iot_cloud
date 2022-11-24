package com.lm.admin.service.device;


import com.lm.admin.common.ex.lthrow.ValidatorExceptionThrow;
import com.lm.admin.common.r.UserResultEnum;
import com.lm.admin.entity.bo.device.*;
import com.lm.admin.entity.dto.device.DeviceAuthDto;
import com.lm.admin.entity.dto.user.UserHeader;
import com.lm.admin.entity.pojo.device.Device;
import com.lm.admin.entity.vo.device.DevicePageVo;
import com.lm.admin.mapper.mysql.device.BaseDeviceMapper;
import com.lm.admin.mapper.mysql.device.RoleAdminDeviceMapper;
import com.lm.admin.mapper.mysql.device.RoleUserDeviceMapper;
import com.lm.admin.utils.lmthreadlocal.RoleThreadLocal;
import com.lm.admin.utils.mybiats.Pager;
import com.lm.cloud.tcp.service.utils.RedisDeviceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
*  服务实现类
*
* @author Lm
* @since 2022-09-23
*/
@Service
@Slf4j
public class DeviceServiceImpl implements IDeviceService {

    // 设备信息
    @Autowired
    private RoleAdminDeviceMapper roleAdminDeviceMapper ;

    // 设备信息
    @Autowired
    private RoleUserDeviceMapper roleUserDeviceMapper;

    private UserHeader userHeader;

    // 根据角色判断使用那个mapper 普通用户的会查询中间表
    private BaseDeviceMapper getBaseDeviceMapper(){
        userHeader = RoleThreadLocal.get();
        if(userHeader.getUserRoleCode()!=null && userHeader.getUserRoleCode().equals("CLOUD_USER")){
            return roleUserDeviceMapper;
        }
        else if(userHeader.getUserRoleCode()!=null && userHeader.getUserRoleCode().equals("CLOUD_ADMIN")){
            return roleAdminDeviceMapper;
        }
        // 如果不是管理员又不是普通用户的情况下 抛出 权限异常
        throw new ValidatorExceptionThrow(UserResultEnum.USER_ROLE_EXCEPTION);
    }

    /**
     * 根据sn码查询到设备信息  这个方法是提供给netty的设备鉴权用的，直接赋予最高权限就行
     * @param sn
     * @return
     */
    @Override
    public DeviceAuthDto getDeviceBySn(String sn) {
        DeviceAuthDto deviceAuthDto = new DeviceAuthDto();
        // 这里的给netty用的直接赋予最大权限 然后随便填个值
        Device deviceBySn = roleAdminDeviceMapper.findDeviceBySn(0L, sn);
        // TODO 加null
        BeanUtils.copyProperties(deviceBySn,deviceAuthDto);
        return deviceAuthDto;
    }
    /**
     * 根据sn码查询到设备信息
     * @param sn
     * @return
     */
    @Override
    public DeviceBo getDeviceBoBySn(String sn) {
        DeviceBo deviceBo = new DeviceBo();
        Device deviceBySn = getBaseDeviceMapper().findDeviceBySn(userHeader.getUserId(), sn);
        // TODO 加null
        BeanUtils.copyProperties(deviceBySn,deviceBo);
        return deviceBo;
    }




    /**
     * 查询所有设备
     * @return
     */
    @Override
    public List<DeviceBo> getDeviceList() {

        List<Device> deiceList = getBaseDeviceMapper().findDeiceList(userHeader.getUserId());

        List<DeviceBo> deviceBoList = new ArrayList<>();
        deiceList.forEach(item->{
            DeviceBo deviceBo = new DeviceBo();
            BeanUtils.copyProperties(item,deviceBo);
            // 查询设备是否上线
            deviceBo.setIsOnLine(RedisDeviceUtils.getDeviceIsOnLienBySn(item.getSn()));
            deviceBoList.add(deviceBo);

        });
        return deviceBoList;
    }

    /**
     * 根据设备分组id查询到对应的设备信息列表
     *
     * @param gid 分组id
     * @return
     */
    @Override
    public List<DeviceBo> getDevicesByGroupingId(Long gid) {
        // 返回Bo
        List<DeviceBo> deviceBoList = new ArrayList<>();
        // pojo 转bo
        List<Device> devicesByGroupingId = getBaseDeviceMapper().findDevicesByGroupingId(gid);
        devicesByGroupingId.forEach(item->{
            DeviceBo deviceBo = new DeviceBo();
            BeanUtils.copyProperties(item,deviceBo);
            // 顺便查询设备是否在线
            deviceBo.setIsOnLine(RedisDeviceUtils.getDeviceIsOnLienBySn(deviceBo.getSn()));
            deviceBoList.add(deviceBo);
        });
        return deviceBoList;
    }

    /**
     * 返回设备sn和设备名称列表
     *
     * @return
     */
    @Override
    public List<DeviceSelectBo> getDeviceSnName() {
        return getBaseDeviceMapper().findDeviceSnName(userHeader.getUserId());
    }


    @Override
    public Integer getDeviceCount() {
        return getBaseDeviceMapper().findDeviceCount();
    }

    /**
     * 去重
     * @param keyExtractor
     * @param <T>
     * @return
     */
    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
