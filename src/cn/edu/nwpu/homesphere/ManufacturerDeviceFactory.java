package cn.edu.nwpu.homesphere;

/**
 * 制造商设备工厂，实现DeviceFactory接口
 */
public class ManufacturerDeviceFactory implements DeviceFactory {
    private Manufacturer manufacturer;
    
    public ManufacturerDeviceFactory(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }
    
    @Override
    public Device createDevice(int deviceId, String name, String type, Object... params) {
        Device device = null;
        
        switch (type.toLowerCase()) {
            case "airconditioner":
            case "空调":
                double powerAC = params.length > 0 ? (double) params[0] : 1500; // 默认功率
                device = new AirConditioner(deviceId, name, manufacturer, powerAC);
                break;
            case "lightbulb":
            case "灯泡":
                double powerLight = params.length > 0 ? (double) params[0] : 20; // 默认功率
                device = new LightBulb(deviceId, name, manufacturer, powerLight);
                break;
            case "smartlock":
            case "智能锁":
                device = new SmartLock(deviceId, name, manufacturer);
                break;
            case "bathroomscale":
            case "体重秤":
                device = new BathroomScale(deviceId, name, manufacturer);
                break;
            default:
                throw new IllegalArgumentException("不支持的设备类型: " + type);
        }
        
        // 将创建的设备添加到制造商的设备列表中
        if (device != null) {
            manufacturer.addDevice(device);
        }
        
        return device;
    }
}