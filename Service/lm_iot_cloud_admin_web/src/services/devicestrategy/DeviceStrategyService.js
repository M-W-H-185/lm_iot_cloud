import lm_request from '@/utils/device_request.js'
export default {
    /**
     * 获取设备分组分页查询
     * @returns 
     */
     page(params={}) {
        let temp = lm_request.post("/devicestrategy/page",params);
        return temp;
    },
}