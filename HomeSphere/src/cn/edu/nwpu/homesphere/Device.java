package cn.edu.nwpu.homesphere;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备抽象类，所有智能设备的基类
 */
public abstract class Device {
    private int deviceId;
    private String name;
    private boolean isOnline;
    private boolean powerStatus;
    private Manufacturer manufacturer;
    private List<RunningLog> runningLogs;

    // 设备类型常量 - 与Manufacturer中保持一致
    public static final String TYPE_AIR_CONDITIONER = "AIR_CONDITIONER";
    public static final String TYPE_LIGHT_BULB = "LIGHT_BULB";
    public static final String TYPE_SMART_LOCK = "SMART_LOCK";
    public static final String TYPE_BATHROOM_SCALE = "BATHROOM_SCALE";

    /**
     * 获取设备类型字符串
     * @return 设备类型
     */
    public abstract String getDeviceType();

    /**
     * 构造函数
     * @param deviceId 设备ID
     * @param name 设备名称
     * @param manufacturer 制造商
     */
    public Device(int deviceId, String name, Manufacturer manufacturer) {
        this.deviceId = deviceId;
        this.name = name;
        this.isOnline = false;
        this.powerStatus = false;
        this.manufacturer = manufacturer;
        this.runningLogs = new ArrayList<>();
    }

    /**
     * 简化构造函数
     * @param deviceId 设备ID
     * @param name 设备名称
     */
    public Device(int deviceId, String name) {
        this(deviceId, name, new Manufacturer(0, "默认厂商", "默认协议"));
    }

    /**
     * 打开设备电源
     */
    public void powerOn() {
        this.powerStatus = true;
        addRunningLog("设备开机", 0, "电源已开启");
    }

    /**
     * 关闭设备电源
     */
    public void powerOff() {
        this.powerStatus = false;
        addRunningLog("设备关机", 0, "电源已关闭");
    }

    /**
     * 添加运行日志
     * @param event 事件描述
     * @param type 事件类型
     * @param note 备注信息
     */
    protected void addRunningLog(String event, int type, String note) {
        RunningLog log = new RunningLog(new java.util.Date(), event, type, note);
        runningLogs.add(log);
    }

    /**
     * 设置设备在线状态
     * @param online 是否在线
     */
    public void setOnline(boolean online) {
        this.isOnline = online;
        String event = online ? "设备上线" : "设备离线";
        addRunningLog(event, online ? 0 : 1, event);
    }

    public String getDeviceName() {
        return getName();
    }
    /**
     * 获取设备ID
     * @return 设备ID
     */
    public int getDeviceId() {
        return deviceId;
    }

    /**
     * 获取设备名称
     * @return 设备名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置设备名称
     * @param name 设备名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 设备是否在线
     * @return 是否在线
     */
    public boolean isOnline() {
        return isOnline;
    }

    /**
     * 设备电源状态
     * @return 电源状态
     */
    public boolean getPowerStatus() {
        return powerStatus;
    }

    /**
     * 获取制造商
     * @return 制造商
     */
    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    /**
     * 获取运行日志
     * @return 运行日志列表
     */
    public List<RunningLog> getRunningLogs() {
        return new ArrayList<>(runningLogs);
    }

    /**
     * 比较两个设备对象是否相等
     * @param obj 要比较的对象
     * @return 如果ID相等则返回true，否则返回false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Device device = (Device) obj;
        return deviceId == device.deviceId;
    }

    /**
     * 返回设备对象的字符串表示
     * @return 设备信息字符串
     */
    @Override
    public String toString() {
        return "Device{deviceId=" + deviceId + ", name='" + name + "', isOnline=" + isOnline + ", powerStatus=" + powerStatus + ", manufacturer='" + manufacturer.getName() + "', type='" + getDeviceType() + "'}";
    }

    /**
     * 将设备实例转换为JSON字符串
     * @return JSON格式的字符串
     */
    public abstract String formatToJsonString();

    /**
     * 从JSON字符串解析为设备对象
     * @param jsonString JSON格式的字符串
     */
    public abstract void parseFromJsonString(String jsonString);
}