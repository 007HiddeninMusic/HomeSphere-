package cn.edu.nwpu.homesphere;

/**
 * 运行日志格式化接口，使用策略模式实现不同格式的日志输出
 */
public interface RunningLogFormatter {
    /**
     * 格式化家庭的所有设备运行日志
     * @param household 家庭对象
     * @return 格式化后的日志字符串
     */
    String format(Household household);
    
    /**
     * 获取格式化器的名称
     * @return 格式化器名称
     */
    String getFormatterName();
}