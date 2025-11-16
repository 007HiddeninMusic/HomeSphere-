package cn.edu.nwpu.homesphere;

/**
 * 能源报告接口，用于能够提供能源消耗数据的设备
 */
public interface EnergyReporting {
    
    /**
     * 获取设备的当前功率
     * @return 功率值，单位瓦特
     */
    double getPower();
    
    /**
     * 获取指定时间范围内的能源消耗报告
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 能源消耗值，单位千瓦时(kWh)
     */
    double getReport(java.util.Date startTime, java.util.Date endTime);
}