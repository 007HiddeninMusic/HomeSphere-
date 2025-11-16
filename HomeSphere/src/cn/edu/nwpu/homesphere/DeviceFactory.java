package cn.edu.nwpu.homesphere;

/**
 * 设备工厂接口，使用工厂方法模式创建设备
 */
public interface DeviceFactory {
    /**
     * 创建设备
     * @param deviceId 设备ID
     * @param name 设备名称
     * @param type 设备类型
     * @param params 额外参数
     * @return 创建的设备对象
     */
    Device createDevice(int deviceId, String name, String type, Object... params);
}