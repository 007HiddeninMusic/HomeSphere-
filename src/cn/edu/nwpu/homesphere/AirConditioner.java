package cn.edu.nwpu.homesphere;

/**
 * 空调类，继承自Device类并实现EnergyReporting接口
 */
public class AirConditioner extends Device implements EnergyReporting {
    private double currTemp; // 当前温度
    private double targetTemp; // 目标温度
    private final double power; // 功率，单位瓦特
    private boolean powerOn;
    // 能耗相关常量
    private static final double POWER_CONSUMPTION = 1500.0; // 瓦特
    private long lastPowerOnTime;
    /**
     * 构造函数
     * @param deviceId 设备ID
     * @param name 设备名称
     * @param manufacturer 制造商
     * @param power 功率
     */
    public AirConditioner(int deviceId, String name, Manufacturer manufacturer, double power) {
        super(deviceId, name, manufacturer);
        this.currTemp = 25.0; // 默认当前温度25℃
        this.targetTemp = 25.0; // 默认目标温度25℃
        this.power = power;
        this.powerOn = false; // 初始状态为关机
        this.lastPowerOnTime = 0; // 初始化为0
    }

    public AirConditioner(int deviceId, String name, Manufacturer manufacturer) {
        // 调用原构造器，给power设一个默认值
        this(deviceId, name, manufacturer, 1.5);
    }

    public void powerOn() {
        this.powerOn = true;
        this.lastPowerOnTime = System.currentTimeMillis();
        addRunningLog("开机", 0, "空调已开机");
    }

    public void powerOff() {
        this.powerOn = false;
        addRunningLog("关机", 0, "空调已关机");
    }


    @Override
    public String getDeviceType() {
        return TYPE_AIR_CONDITIONER;
    }

    /**
     * 设置目标温度
     * @param targetTemp 目标温度值
     */
    public void setTargetTemp(double targetTemp) {
        this.targetTemp = targetTemp;
        System.out.println(getName() + " 目标温度设置为: " + targetTemp + "°C");
    }

    /**
     * 更新当前温度
     * @param currTemp 当前温度值
     */
    public void setCurrTemp(double currTemp) {
        this.currTemp = currTemp;
    }

    /**
     * 获取当前温度
     * @return 当前温度值
     */
    public double getCurrTemp() {
        return currTemp;
    }

    /**
     * 获取目标温度
     * @return 目标温度值
     */
    public double getTargetTemp() {
        return targetTemp;
    }

    /**
     * 获取功率（考虑温度差因素）
     * @return 实际功率，单位瓦特
     */
    @Override
    public double getPower() {
        return isPowerStatus() ? POWER_CONSUMPTION : 0.0;
    }

    /**
     * 获取电源状态
     * @return 电源开启返回true，否则返回false
     */
    public boolean isPowerStatus() {
        return powerOn;
    }

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
        if (!isPowerStatus()) {
            return 0.0;
        }

        long start = startTime.getTime();
        long end = endTime.getTime();
        long powerOnTime = this.lastPowerOnTime;

        // 如果开机时间在查询时间之后，则没有能耗
        if (powerOnTime > end) {
            return 0.0;
        }

        // 计算实际运行时间（小时）
        long actualStart = Math.max(start, powerOnTime);
        long runningTime = Math.max(0, end - actualStart);
        double hours = runningTime / (1000.0 * 60 * 60);

        // 计算能耗：功率 × 时间
        return (POWER_CONSUMPTION * hours) / 1000.0; // 转换为千瓦时
    }

    /**
     * 返回空调对象的字符串表示
     * @return 空调信息字符串
     */
    @Override
    public String toString() {
        return "AirConditioner{deviceId=" + getDeviceId() + ", name='" + getName() + "', currTemp=" + currTemp + ", targetTemp=" + targetTemp + ", power=" + power + "W}";
    }

    // 在AirConditioner.java中添加JSON方法实现
    @Override
    public String formatToJsonString() {
        com.alibaba.fastjson2.JSONObject json = new com.alibaba.fastjson2.JSONObject();
        json.put("deviceId", getDeviceId());
        json.put("name", getName());
        json.put("online", isOnline());
        json.put("powerStatus", isPowerStatus());
        json.put("currTemp", currTemp);
        json.put("targetTemp", targetTemp);
        json.put("power", getPower());

        // 序列化制造商
        com.alibaba.fastjson2.JSONObject manufacturerJson = new com.alibaba.fastjson2.JSONObject();
        manufacturerJson.put("manufacturerId", getManufacturer().getManufacturerId());
        manufacturerJson.put("name", getManufacturer().getName());
        manufacturerJson.put("protocols", getManufacturer().getProtocols());
        json.put("manufacturer", manufacturerJson);

        // 序列化运行日志
        com.alibaba.fastjson2.JSONArray logsArray = new com.alibaba.fastjson2.JSONArray();
        for (RunningLog log : getRunningLogs()) {
            com.alibaba.fastjson2.JSONObject logJson = new com.alibaba.fastjson2.JSONObject();
            logJson.put("dateTime", log.getDateTime().toString());
            logJson.put("event", log.getEvent());
            logJson.put("type", log.getType().name());
            logJson.put("note", log.getNote());
            logsArray.add(logJson);
        }
        json.put("runningLog", logsArray);

        return json.toString();
    }

    @Override
    public void parseFromJsonString(String jsonString) {
        com.alibaba.fastjson2.JSONObject json = com.alibaba.fastjson2.JSONObject.parse(jsonString);
        setName(json.getString("name"));
        setOnline(json.getBooleanValue("online"));
        this.powerOn = json.getBooleanValue("powerStatus");
        this.currTemp = json.getDoubleValue("currTemp");
        this.targetTemp = json.getDoubleValue("targetTemp");

        // 解析运行日志
        com.alibaba.fastjson2.JSONArray logsArray = json.getJSONArray("runningLog");
        if (logsArray != null) {
            for (int i = 0; i < logsArray.size(); i++) {
                com.alibaba.fastjson2.JSONObject logJson = logsArray.getJSONObject(i);
            }
        }
    }


}