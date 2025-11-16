package cn.edu.nwpu.homesphere;

/**
 * 设备动作类，代表对设备执行的特定操作
 */
public class DeviceAction {
    private Device device;
    private String command;
    private String parameters;
    
    /**
     * 构造函数
     * @param device 目标设备
     * @param command 命令名称
     * @param parameters 命令参数
     */
    public DeviceAction(Device device, String command, String parameters) {
        this.device = device;
        this.command = command;
        this.parameters = parameters;
    }

    public DeviceAction(String command, String parameters, Device device) {
        this.device = device;
        this.command = command;
        this.parameters = parameters;
    }



    /**
     * 执行设备动作
     */
    public void execute() {
        try {
            System.out.println("执行动作: " + command + " on " + device.getName() + " with params: " + parameters);

            // 根据设备类型和命令执行相应操作
            switch (command.toLowerCase()) {
                case "power_on":
                    device.powerOn();
                    break;
                case "power_off":
                    device.powerOff();
                    break;
                case "set_brightness":
                    if (device instanceof LightBulb) {
                        try {
                            int brightness = Integer.parseInt(parameters);
                            if (brightness >= 0 && brightness <= 100) {
                                ((LightBulb) device).setBrightness(brightness);
                            } else {
                                System.out.println("亮度值必须在0-100之间");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("参数格式错误: " + parameters);
                        }
                    }
                    break;
                case "set_colortemp":
                    if (device instanceof LightBulb) {
                        try {
                            int colorTemp = Integer.parseInt(parameters);
                            if (colorTemp >= 2300 && colorTemp <= 7000) {
                                ((LightBulb) device).setColorTemp(colorTemp);
                            } else {
                                System.out.println("色温值必须在2300-7000之间");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("参数格式错误: " + parameters);
                        }
                    }
                    break;
                case "set_temperature":
                    if (device instanceof AirConditioner) {
                        try {
                            double temperature = Double.parseDouble(parameters);
                            if (temperature >= 16.0 && temperature <= 32.0) {
                                ((AirConditioner) device).setTargetTemp(temperature);
                            } else {
                                System.out.println("温度值必须在16.0-32.0之间");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("参数格式错误: " + parameters);
                        }
                    }
                    break;
                case "lock":
                    if (device instanceof SmartLock) {
                        ((SmartLock) device).lock();
                        System.out.println("智能锁已锁定");
                    }
                    break;
                case "unlock":
                    if (device instanceof SmartLock) {
                        ((SmartLock) device).unlock();
                        System.out.println("智能锁已解锁");
                    }
                    break;
                case "measure_weight":
                    if (device instanceof BathroomScale) {
                        try {
                            double weight = Double.parseDouble(parameters);
                            if (weight > 0) {
                                ((BathroomScale) device).measureWeight(weight);
                            } else {
                                System.out.println("体重值必须大于0");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("参数格式错误: " + parameters);
                        }
                    }
                    break;
                default:
                    System.out.println("未知命令: " + command);
            }
        } catch (Exception e) {
            System.out.println("执行设备动作时发生错误: " + e.getMessage());
        }
    }
    
    /**
     * 获取目标设备
     * @return 设备对象
     */
    public Device getDevice() {
        return device;
    }
    
    /**
     * 获取命令名称
     * @return 命令名称
     */
    public String getCommand() {
        return command;
    }
    
    /**
     * 获取命令参数
     * @return 命令参数
     */
    public String getParameters() {
        return parameters;
    }
    
    /**
     * 返回设备动作对象的字符串表示
     * @return 设备动作信息字符串
     */
    @Override
    public String toString() {
        return "DeviceAction{deviceId=" + device.getDeviceId() + ", command='" + command + "', parameters='" + parameters + "'}";
    }
}