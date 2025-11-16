package cn.edu.nwpu.homesphere;

import java.util.Date;

public class LogFormatterDemo {
    public static void main(String[] args) {
        // 创建测试数据
        Household household = createTestHousehold();

        // 测试三种格式化器
        RunningLogFormatter[] formatters = {
                new JsonRunningLogFormatter(),
                new HtmlRunningLogFormatter(),
                new XmlRunningLogFormatter()
        };

        for (RunningLogFormatter formatter : formatters) {
            System.out.println("\n=== " + formatter.getFormatterName() + " ===");
            String result = formatter.format(household);

            // 显示前200字符作为预览
            String preview = result.length() > 200 ?
                    result.substring(0, 200) + "..." : result;
            System.out.println(preview);

            // 保存到文件
            String filename = "runningLog";
            switch (formatter.getFormatterName()) {
                case "JSON格式": filename += ".json"; break;
                case "HTML格式": filename += ".html"; break;
                case "XML格式": filename += ".xml"; break;
            }

            try {
                java.nio.file.Files.write(
                        java.nio.file.Paths.get(filename),
                        result.getBytes()
                );
                System.out.println("已保存到: " + filename);
            } catch (Exception e) {
                System.out.println("保存文件失败: " + e.getMessage());
            }
        }

        System.out.println("\n测试完成！");
    }

    private static Household createTestHousehold() {
        // 创建测试家庭
        User admin = new User(1, "admin", "admin", "管理员", "admin@nwpu.edu.cn", true);
        Household household = new Household(1, "友谊西路127号", admin);

        // 创建房间
        Room livingRoom = new Room(1, "客厅", 25.0);
        Room masterBedroom = new Room(2, "主卧", 18.0);
        Room secondBedroom = new Room(3, "次卧", 15.0);

        // 创建设备
        Manufacturer manufacturer = new Manufacturer(1, "测试厂家", "WiFi");

        // 客厅设备
        AirConditioner livingAC = new AirConditioner(1205, "客厅空调", manufacturer, 1500);
        LightBulb livingLight = new LightBulb(2876, "客厅吸顶灯", manufacturer, 20);
        SmartLock frontLock = new SmartLock(2278, "智能门锁", manufacturer);

        // 添加运行日志
        livingAC.powerOn();
        livingAC.setTargetTemp(26.0);
        livingAC.powerOff();

        livingLight.powerOn();
        livingLight.setBrightness(20);
        livingLight.setColorTemp(3000);
        livingLight.powerOff();

        frontLock.lock();

        // 主卧设备
        AirConditioner masterAC = new AirConditioner(2929, "主卧空调", manufacturer, 1200);
        LightBulb masterLight = new LightBulb(8561, "主卧灯", manufacturer, 15);

        masterAC.powerOn();
        masterAC.setTargetTemp(26.0);
        masterAC.powerOff();

        masterLight.powerOn();
        masterLight.powerOff();

        // 次卧设备
        AirConditioner secondAC = new AirConditioner(2214, "次卧空调", manufacturer, 1200);
        LightBulb secondLight = new LightBulb(1637, "次卧灯", manufacturer, 15);

        secondAC.powerOn();
        secondAC.setTargetTemp(26.0);
        secondAC.powerOff();

        secondLight.powerOn();
        secondLight.powerOff();

        // 添加设备到房间
        livingRoom.addDevice(livingAC);
        livingRoom.addDevice(livingLight);
        livingRoom.addDevice(frontLock);

        masterBedroom.addDevice(masterAC);
        masterBedroom.addDevice(masterLight);

        secondBedroom.addDevice(secondAC);
        secondBedroom.addDevice(secondLight);

        // 添加房间到家庭
        household.addRoom(livingRoom);
        household.addRoom(masterBedroom);
        household.addRoom(secondBedroom);

        return household;
    }
}