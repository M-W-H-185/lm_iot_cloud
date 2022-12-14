package com.lm.admin.service.devicedata;


import com.lm.admin.entity.bo.device.*;
import com.lm.admin.entity.dto.device.DeviceNewDataDto;
import com.lm.admin.mapper.tdengine.DeviceDataMapper;
import com.lm.admin.service.devicemodel.DeviceModelServiceImpl;
import com.lm.common.redis.devicekey.CloudRedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
*  服务实现类
*
* @author Lm
* @since 2022-09-23
*/
@Service
@Slf4j
public class DeviceDataServiceImpl implements IDeviceDataService {

    // tde的设备数据库
    @Autowired
    private DeviceDataMapper deviceDataMapper;

    @Autowired
    private DeviceModelServiceImpl deviceModelService;

    // 按照名字去匹配 不能用Autowired因为用类型匹配的
    @Resource(name = "fastjson2RedisTemplate")
    private RedisTemplate redisTemplate;  // 操作Redis

    // 设置日期格式
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    /**
     * 保存设备数据
     *
     * @param sn sn码
     * @param dataMap 设备数据集合
     * @return
     */
    @Override
    public int saveDeviceData(String sn, Map<String, String> dataMap) {
        // 因为这个方法是在异步里面执行的，所以插入数据库需要同步线程
        synchronized(this) {
            AtomicReference<Integer> saveCount = new AtomicReference<>(0);
            dataMap.forEach((k, v) -> {
                saveCount.set(
                        saveCount.get() +
                                // 拼接 表名 sn_yyyyMMdd
                                deviceDataMapper.saveDeviceData(sn + "_" + simpleDateFormat.format(new Date()), sn, k, v)
                );
            });
            return saveCount.get();
        }
    }

    /**
     * 保存设备最新数据使用redis
     *
     * @param deviceNewDataDto
     * @return
     */
    public boolean saveDeviceDataRedis(DeviceNewDataDto deviceNewDataDto){
        deviceNewDataDto.setTs(System.currentTimeMillis());
        redisTemplate.opsForValue().set(CloudRedisKey.DeviceNewDataKey + deviceNewDataDto.getSn(),deviceNewDataDto);
        return true;
    }


    /**
     * @description: 获取设备最新数据 td
     * @author: Lm
     * @date: 2022/10/8 15:25
     * @param  sn, identifierMap
     * @return Map<String, String>  标识符对应的设备数据
     **/
    @Override
    public  List<DeviceIdentifierAndNameDataBo> getDeviceNewData(String sn) {

        // 1、先查询设备对应的物模型
        List<DeviceModelAndNewDataBo> deiceModelBySn = deviceModelService.getDeiceModelBySn(sn);

        // 如果该设备没有创建物模型
        if(deiceModelBySn.size() <= 0 ){
            // TODO 统一处理一下抛出异常
            return null;
        }

        // 2、将标识符转换为字符串 'tag1','tag2'.... 用于查询时序数据库的数据 这样做的话如果tag3没有的话就会查询出来多的一条数据
        String identifierStr= "";
        for (int i = 0; i <deiceModelBySn.size() ; i++) {
            // 拼接物模型的标识符
            if(i<deiceModelBySn.size()-1){
                identifierStr += "'" + deiceModelBySn.get(i).getIdentifier() + "'," ;
            }else{
                identifierStr += "'" + deiceModelBySn.get(i).getIdentifier() + "'" ;
            }
        }

        // 3、根据标识符字符串 sn 查询到对应的设备数据
        List<DeviceDataTdBo> deviceDataTdBos = deviceDataMapper.fineDeviceDatas(sn, identifierStr, deiceModelBySn.size());



        // 4、因为有些数据是没有上报到时序数据库里面的 但是查询时序数据库的时候查询会多出来数据 所以要去除重复的标识符
        deviceDataTdBos = deviceDataTdBos.stream().filter(
                distinctByKey(DeviceDataTdBo::getIdentifier)
        ).collect(Collectors.toList());

        // 拼接 标识符和数据
        Map<String, String> identifierToValMap = deviceDataTdBos.stream().collect(
                Collectors.toMap(DeviceDataTdBo::getIdentifier , DeviceDataTdBo::getVal)
        );
        // 拼接 标识符和时间
        Map<String, Date> identifierToTsMap = deviceDataTdBos.stream().collect(
                Collectors.toMap(DeviceDataTdBo::getIdentifier , DeviceDataTdBo::getTs)
        );


        // 返回标识符、名称和值
        List<DeviceIdentifierAndNameDataBo> deviceIdentifierAndNameDataList = new ArrayList<>();

        // 物模型数据作为循环 把数据都加载到list里面
        deiceModelBySn.stream().forEach(item->{
            DeviceIdentifierAndNameDataBo deviceIdentifierAndNameData = new DeviceIdentifierAndNameDataBo();
            deviceIdentifierAndNameData.setTs(identifierToTsMap.get(item.getIdentifier()));
            deviceIdentifierAndNameData.setIdentifier(item.getIdentifier());
            deviceIdentifierAndNameData.setName(item.getName());
            deviceIdentifierAndNameData.setVal(identifierToValMap.get(item.getIdentifier()));
            deviceIdentifierAndNameData.setUnit(item.getUnit());
            deviceIdentifierAndNameDataList.add(deviceIdentifierAndNameData);
        });


//        log.info("--->{}",deviceIdentifierAndNameDataList);
        return deviceIdentifierAndNameDataList;
    }

    /**
     * @description: 获取设备最新数据 redis
     *
     * @param  sn, identifierMap
     * @return DeviceIdentifierAndNameDataBo  标识符对应的设备数据
     **/
    @Override
    public List<DeviceModelAndNewDataBo> getDeviceNewDataRedis(String sn) {
        // 1、先查询设备对应的物模型
        List<DeviceModelAndNewDataBo> deiceModelBySn = deviceModelService.getDeiceModelBySn(sn);

        // 如果该设备没有创建物模型
        if(deiceModelBySn.size() <= 0 ){
            // TODO 统一处理一下抛出异常
            return null;
        }
        // 查询redis设备数据
        DeviceNewDataDto deviceNewDataDto =(DeviceNewDataDto) redisTemplate.opsForValue().get(CloudRedisKey.DeviceNewDataKey + sn);

        // 如果查询redis的设备数据为null 就获取td的数据
        if(deviceNewDataDto == null){
            // 获取td的数据
            List<DeviceIdentifierAndNameDataBo> deviceTdData = getDeviceNewData(sn);
            // 遍历物模型的数据
            deiceModelBySn.forEach(modelItem->{
                // 遍历td的数据
                deviceTdData.forEach(tdData->{
                    // 如果标识符相同
                    if(modelItem.getIdentifier().equals(tdData.getIdentifier())){
                        // 拼接数据
                        modelItem.setVal(tdData.getVal());
                        modelItem.setTs(tdData.getTs());
                    }
                });
            });
            return deiceModelBySn;
        }


        // 如果redis有数据就处理redis的数据
        deiceModelBySn.stream().forEach(modelItem->{
            // 通过物模型的标识符 获取 redis的 dataMap的val
            String val = deviceNewDataDto.getData().get(modelItem.getIdentifier());
            if(val!=null){
                modelItem.setVal(val);
            }
            modelItem.setTs(new java.sql.Date(deviceNewDataDto.getTs()));

        });
        return deiceModelBySn;
    }





    /**
     * 根据设备sn码查询设备上报数 可以null
     *
     * @param sn
     * @return
     */
    @Override
    public Long getDeviceDataUpCount(String sn) {
        return deviceDataMapper.findDeviceDataUpCount(sn);
    }

    /**
     * 根据设备sn码查询到当天设备上报数 如果sn为null就查询全部的数据
     *
     * @param sn
     * @return
     */
    @Override
    public Long getThisDayDeviceDataUpCount(String sn) {
        return deviceDataMapper.findThisDayDeviceDataUpCount(sn);
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
