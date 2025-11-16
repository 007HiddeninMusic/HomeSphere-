package cn.edu.nwpu.homesphere;

/**
 * 智能体重秤类，继承自Device类
 */
public class BathroomScale extends Device {
    private double bodyMass;
    private int batteryLevel;
    
    /**
     * 构造函数
     * @param deviceId 设备ID
     * @param name 设备名称
     * @param manufacturer 制造商
     */
    public BathroomScale(int deviceId, String name, Manufacturer manufacturer) {
        super(deviceId, name, manufacturer);
        this.bodyMass = 0.0;
        this.batteryLevel = 100; // 初始电池电量100%
    }

    @Override
    public String getDeviceType() {
        return TYPE_BATHROOM_SCALE;
    }

    /**
     * 测量体重
     * @param bodyMass 体重值
     */
    public void measureWeight(double bodyMass) {
        if (bodyMass > 0) {
            this.bodyMass = bodyMass;
            decreaseBatteryLevel(2); // 每次测量消耗2%电量
            addRunningLog("体重测量", 0, "体重测量值: " + bodyMass + "kg");
        }
    }
    
    /**
     * 减少电池电量
     * @param amount 减少的电量
     */
    private void decreaseBatteryLevel(int amount) {
        batteryLevel = Math.max(0, batteryLevel - amount);
        if (batteryLevel <= 20) {
            addRunningLog("电池电量低", 1, "电池电量剩余" + batteryLevel + "%");
        }
    }
    
    /**
     * 获取体重值
     * @return 体重值
     */
    public double getBodyMass() {
        return bodyMass;
    }
    
    /**
     * 获取电池电量
     * @return 电池电量百分比
     */
    public int getBatteryLevel() {
        return batteryLevel;
    }
    
    /**
     * 设置电池电量
     * @param batteryLevel 电池电量百分比
     */
    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = Math.max(0, Math.min(100, batteryLevel));
    }

    /**
     * 设置体重
     * @param bodyMass 体重值
     */
    public void setBodyMass(double bodyMass) {
        this.bodyMass = bodyMass;
    }

    /**
     * 返回智能体重秤对象的字符串表示
     * @return 智能体重秤信息字符串
     */
    @Override
    public String toString() {
        return "BathroomScale{deviceId=" + getDeviceId() + ", name='" + getName() + "', bodyMass=" + bodyMass + "kg, batteryLevel=" + batteryLevel + "%}";
    }

    // 添加JSON方法实现
    @Override
    public String formatToJsonString() {
        com.alibaba.fastjson2.JSONObject json = new com.alibaba.fastjson2.JSONObject();
        json.put("deviceId", getDeviceId());
        json.put("name", getName());
        json.put("online", isOnline());
        json.put("powerStatus", getPowerStatus());
        json.put("bodyMass", bodyMass);
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
        this.bodyMass = json.getDoubleValue("bodyMass");
        this.batteryLevel = json.getIntValue("batteryLevel");
    }
}