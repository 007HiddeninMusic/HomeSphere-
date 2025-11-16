package cn.edu.nwpu.homesphere;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 制造商类，代表智能设备的生产厂商
 */
public class Manufacturer {
    private int manufacturerId;
    private String name;
    private String protocols;
    private List<Device> devices = new ArrayList<>(); // 设备列表
    private DeviceFactory deviceFactory;

    // 设备类型常量
    public static final String DEVICE_TYPE_AIR_CONDITIONER = "AIR_CONDITIONER";
    public static final String DEVICE_TYPE_LIGHT_BULB = "LIGHT_BULB";
    public static final String DEVICE_TYPE_SMART_LOCK = "SMART_LOCK";
    public static final String DEVICE_TYPE_BATHROOM_SCALE = "BATHROOM_SCALE";
    
    /**
     * 构造函数
     * @param manufacturerId 制造商ID
     * @param name 制造商名称
     * @param protocols 支持的通信协议
     */
    public Manufacturer(int manufacturerId, String name, String protocols) {
        this.manufacturerId = manufacturerId;
        this.name = name;
        this.protocols = protocols;
        this.deviceFactory = new ManufacturerDeviceFactory(this);
    }
    
    /**
     * 获取制造商ID
     * @return 制造商ID
     */
    public int getManufacturerId() {
        return manufacturerId;
    }
    
    /**
     * 获取制造商名称
     * @return 制造商名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取该厂商生产的所有设备
     * @return 设备列表
     */
    public List<Device> getDevices() {
        return new ArrayList<>(devices);
    }

    /**
     * 添加设备
     * @param device 设备对象
     */
    public void addDevice(Device device) {
        devices.add(device);
    }

    /**
     * 移除设备
     * @param device 设备对象
     * @return 是否移除成功
     */
    public boolean removeDevice(Device device) {
        return devices.remove(device);
    }
    
    /**
     * 生产设备
     * @param deviceId 设备ID
     * @param name 设备名
     * @param params 额外参数
     * @return 生产的设备
     */
    public Device produceDevice(String deviceType, int deviceId, String name, Object... params) {
        Device device = deviceFactory.createDevice(deviceId, name, deviceType, params);
        if (device != null) {
            addDevice(device);
            System.out.println("制造商 " + this.name + " 生产了设备: " + device.getName());
        }
        return device;
    }

    /**
     * 显示生产统计
     */
    public void displayProductionStatistics() {
        System.out.println("\n=== " + name + " 生产统计 ===");
        System.out.println("总生产设备数: " + devices.size());

        Map<String, Long> typeCount = devices.stream()
                .collect(Collectors.groupingBy(Device::getDeviceType, Collectors.counting()));

        typeCount.forEach((type, count) -> {
            String typeName = getDeviceTypeName(type);
            System.out.println(typeName + ": " + count + "台");
        });
    }

    private String getDeviceTypeName(String deviceType) {
        switch (deviceType) {
            case DEVICE_TYPE_AIR_CONDITIONER: return "空调";
            case DEVICE_TYPE_LIGHT_BULB: return "智能灯泡";
            case DEVICE_TYPE_SMART_LOCK: return "智能锁";
            case DEVICE_TYPE_BATHROOM_SCALE: return "体重秤";
            default: return "未知设备";
        }
    }

    /**
     * 获取支持的设备类型列表
     * @return 支持的设备类型数组
     */
    public String[] getSupportedDeviceTypes() {
        return new String[] {"空调", "灯泡", "智能锁", "体重秤"};
    }

    /**
     * 获取支持的通信协议
     * @return 通信协议字符串
     */
    public String getProtocols() {
        return protocols;
    }
    
    /**
     * 设置支持的通信协议
     * @param protocols 通信协议字符串
     */
    public void setProtocols(String protocols) {
        this.protocols = protocols;
    }
    
    /**
     * 返回制造商对象的字符串表示
     * @return 制造商信息字符串
     */
    @Override
    public String toString() {
        return "Manufacturer{manufacturerId=" + manufacturerId + ", name='" + name + "', protocols='" + protocols + "'}";
    }
}