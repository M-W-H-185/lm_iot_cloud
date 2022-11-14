import lm_request from '@/utils/device_request.js'
export default {

    /**
     * 获取设备 信息 分页 查询指定设备
     * @returns 
     */
    listPage(params={}) {
        let temp = lm_request.post("/page",params);
        return temp;
    },
    deviceModel(params={}){
        let temp = lm_request.post("/devicemodel",params);
        return temp;
    },
    getNewData(sn){
        console.log("snsnsnsnnsns",sn);
        let temp = lm_request.post(`/newdata/${sn}`);
        return temp;
    },
    /**
     * 获取设备是否在线
     * @param {} sn 
     */
    isOnLineBySn(sn){
        let temp = lm_request.post(`/online/${sn}`);
        return temp;
    },
    /**
     * 根据设备分组id查询到设备的信息列表
     * @returns params 分组id
     */
    deviceByGroupingId(params) {
        let temp = lm_request.post("/devices/groupingid/"+params);
        return temp;
    },
    /**
     * 发送命令到设备
     * @param {*} 
     * @returns 
     */
    cmd(params={}){
        let temp = lm_request.post(`/cmd`,params);
        return temp;
    },
}