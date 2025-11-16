package cn.edu.nwpu.homesphere;

/**
 * 智能锁类，继承自Device类
 * 根据UML图实现智能锁的功能
 */
public class SmartLock extends Device {
    private boolean isLocked; // 锁的状态
    private int batteryLevel; // 电池电量

    /**
     * 构造函数
     * @param deviceId 设备ID
     * @param name 设备名称
     * @param manufacturer 制造商
     */
    public SmartLock(int deviceId, String name, Manufacturer manufacturer) {
        super(deviceId, name, manufacturer);
        this.isLocked = true; // 默认锁定状态
        this.batteryLevel = 0; // 默认电池电量为0
    }

    /**
     * 获取锁的状态
     * @return 是否锁定
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * 设置锁的状态
     * @param locked 是否锁定
     */
    public void setLocked(boolean locked) {
        this.isLocked = locked;
    }
    
    /**
     * 锁定智能锁
     */
    public void lock() {
        this.isLocked = true;
    }
    
    /**
     * 解锁智能锁
     */
    public void unlock() {
        this.isLocked = false;
    }

    /**
     * 获取电池电量
     * @return 电池电量
     */
    public int getBatteryLevel() {
        return batteryLevel;
    }

    /**
     * 设置电池电量
     * @param batteryLevel 电池电量
     */
    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    /**
     * 重写toString方法
     * @return 智能锁的字符串表示
     */
    @Override
    public String toString() {
        return "SmartLock{" +
                "deviceId=" + getDeviceId() +
                ", name='" + getName() + '\'' +
                ", locked=" + isLocked +
                ", batteryLevel=" + batteryLevel +
                "}";
    }


    @Override
    public String getDeviceType() {
        return TYPE_SMART_LOCK;
    }

    // 在SmartLock.java中添加JSON方法实现
    @Override
    public String formatToJsonString() {
        com.alibaba.fastjson2.JSONObject json = new com.alibaba.fastjson2.JSONObject();
        json.put("deviceId", getDeviceId());
        json.put("name", getName());
        json.put("online", isOnline());
        json.put("powerStatus", getPowerStatus());
        json.put("isLocked", isLocked);
        json.put("batteryLevel", batteryLevel);

        // 序列化制造商
        com.alibaba.fastjson2.JSONObject manufacturerJson = new com.alibaba.fastjson2.JSONObject();
        manufacturerJson.put("manufacturerId", getManufacturer().getManufacturerId());
        manufacturerJson.put("name", getManufacturer().getName());
        manufacturerJson.put("protocols", getManufacturer().getProtocols());
        json.put("manufacturer", manufacturerJson);

        return json.toString();
    }

    @Override
    public void parseFromJsonString(String jsonString) {
        com.alibaba.fastjson2.JSONObject json = com.alibaba.fastjson2.JSONObject.parse(jsonString);
        setName(json.getString("name"));
        setOnline(json.getBooleanValue("online"));
        this.isLocked = json.getBooleanValue("isLocked");
        this.batteryLevel = json.getIntValue("batteryLevel");
    }
}