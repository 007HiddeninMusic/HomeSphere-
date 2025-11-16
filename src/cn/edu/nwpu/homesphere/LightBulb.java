package cn.edu.nwpu.homesphere;

/**
 * 智能灯泡类，继承自Device类并实现EnergyReporting接口
 */
public class LightBulb extends Device implements EnergyReporting {
    private int brightness; // 亮度，范围0-100
    private int colorTemp; // 色温，范围2700K-6500K
    private final double power; // 功率，单位瓦特
    private boolean powerOn;
    /**
     * 构造函数
     * @param deviceId 设备ID
     * @param name 设备名称
     * @param manufacturer 制造商
     * @param power 功率
     */
    public LightBulb(int deviceId, String name, Manufacturer manufacturer, double power) {
        super(deviceId, name, manufacturer);
        this.brightness = 50; // 默认亮度50%
        this.colorTemp = 4000; // 默认色温4000K
        this.power = power;
        this.powerOn = false; // 初始状态为关机
    }

    public LightBulb(int deviceId, String name, Manufacturer manufacturer) {
        // 调用原构造器，给power默认值
        this(deviceId, name, manufacturer, 5.0);
    }

    // 开机方法
    public void powerOn() {
        this.powerOn = true;
        addRunningLog("开机", 0, "灯泡已开机");
    }

    // 关机方法
    public void powerOff() {
        this.powerOn = false;
        addRunningLog("关机", 0, "灯泡已关机");
    }

    /**
     * 设置亮度
     * @param brightness 亮度值，范围0-100
     */
    public void setBrightness(int brightness) {
        if (brightness >= 0 && brightness <= 100) {
            this.brightness = brightness;
            addRunningLog("设置亮度", 0, "亮度设置为" + brightness + "%");
        }
    }
    
    /**
     * 设置色温
     * @param colorTemp 色温值，范围2700-6500
     */
    public void setColorTemp(int colorTemp) {
        if (colorTemp >= 2700 && colorTemp <= 6500) {
            this.colorTemp = colorTemp;
            addRunningLog("设置色温", 0, "色温设置为" + colorTemp + "K");
        }
    }
    
    /**
     * 获取亮度
     * @return 亮度值
     */
    public int getBrightness() {
        return brightness;
    }
    
    /**
     * 获取色温
     * @return 色温值
     */
    public int getColorTemp() {
        return colorTemp;
    }
    
    /**
     * 获取功率（考虑亮度因素）
     * @return 实际功率，单位瓦特
     */
    @Override
    public double getPower() {
        if (getPowerStatus() && isOnline()) {
            return power * (brightness / 100.0);
        }
        return 0;
    }

    /**
     * @return 电源开启返回true，否则返回false
     */
    public boolean getPowerStatus() {
        return powerOn;
    }

    /**
     * @return 始终返回true
     */
    @Override
    public boolean isOnline() {
        return true;
    }

    /**
     * 获取指定时间范围内的能耗报告
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 能耗值，单位kWh
     */
    @Override
    public double getReport(java.util.Date startTime, java.util.Date endTime) {
        if (!getPowerStatus()) {
            return 0.0;
        }

        // 计算时间差（小时）
        long diffInMillis = endTime.getTime() - startTime.getTime();
        double totalHours = diffInMillis / (1000.0 * 60 * 60);

        // 考虑亮度因素的实际功率
        double actualPower = getPower();

        // 计算能耗：实际功率 × 时间
        return (actualPower * totalHours) / 1000.0;
    }
    
    /**
     * 返回智能灯泡对象的字符串表示
     * @return 智能灯泡信息字符串
     */
    @Override
    public String toString() {
        return "LightBulb{deviceId=" + getDeviceId() + ", name='" + getName() + "', brightness=" + brightness + ", colorTemp=" + colorTemp + ", power=" + power + "W}";
    }

    @Override
    public String getDeviceType() {
        return TYPE_LIGHT_BULB;
    }

    // 添加JSON方法实现
    @Override
    public String formatToJsonString() {
        com.alibaba.fastjson2.JSONObject json = new com.alibaba.fastjson2.JSONObject();
        json.put("deviceId", getDeviceId());
        json.put("name", getName());
        json.put("online", isOnline());
        json.put("powerStatus", getPowerStatus());
        json.put("brightness", brightness);
        json.put("colorTemp", colorTemp);
        json.put("power", getPower());

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
        this.powerOn = json.getBooleanValue("powerStatus");
        this.brightness = json.getIntValue("brightness");
        this.colorTemp = json.getIntValue("colorTemp");
    }

}