package cn.edu.nwpu.homesphere;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 主程序入口
 */
public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // 测试JSON功能
        testJSONFunctionality();

        // 测试异常处理
        testExceptionHandling();
        
        // 测试三种设计模式
        testDesignPatterns();

        // 启动命令行界面
        CommandUI ui = new CommandUI();
        ui.start();
    }

    private static void testJSONFunctionality() {
        System.out.println("=== 测试JSON功能 ===");

        // 创建测试设备
        Manufacturer manufacturer = new Manufacturer(1, "测试厂家", "WiFi");
        AirConditioner ac = new AirConditioner(1, "测试空调", manufacturer, 1500);
        ac.setOnline(true);
        ac.powerOn();
        ac.setTargetTemp(25.5);

        // 测试JSON序列化
        String json = ac.formatToJsonString();
        System.out.println("空调JSON: " + json);

        // 测试JSON反序列化
        AirConditioner newAC = new AirConditioner(2, "新空调", manufacturer, 1500);
        newAC.parseFromJsonString(json);
        System.out.println("反序列化后的空调: " + newAC.getName());
    }

    private static void testExceptionHandling() {
        System.out.println("\n=== 测试异常处理 ===");

        HomeSphereSystem system = new HomeSphereSystem();

        try {
            // 测试无效用户登录
            system.login("nonexistent", "password");
        } catch (InvalidUserException e) {
            System.out.println("捕获到预期异常: " + e.getMessage());
        }
    }
    
    /**
     * 测试三种设计模式
     */
    private static void testDesignPatterns() {
        try {
            System.out.println("\n=== 测试三种设计模式 ===");
            
            // 创建CommandUI实例并初始化数据
            CommandUI commandUI = new CommandUI();
            commandUI.initializeData();
            
            // 获取系统单例实例
            HomeSphereSystem system = HomeSphereSystem.getInstance();
            
            // 测试工厂模式 - 创建设备
            Manufacturer manufacturer = new Manufacturer(1, "测试制造商", "WiFi");
            Device device = manufacturer.produceDevice("DEVICE_TEST", Integer.parseInt("测试设备"), "空调", "2200");
            System.out.println("工厂模式测试：成功创建设备 " + device.getName());
            
            // 测试策略模式 - 导出不同格式日志
            if (system.getHousehold() != null) {
                System.out.println("\n策略模式测试 - JSON格式:");
                String jsonLogs = system.exportLogsWithFormatter(new JsonRunningLogFormatter());
                System.out.println(jsonLogs.length() > 100 ? jsonLogs.substring(0, 100) + "..." : jsonLogs);
                
                System.out.println("\n策略模式测试 - HTML格式:");
                String htmlLogs = system.exportLogsWithFormatter(new HtmlRunningLogFormatter());
                System.out.println(htmlLogs.length() > 100 ? htmlLogs.substring(0, 100) + "..." : htmlLogs);
                
                System.out.println("\n策略模式测试 - XML格式:");
                String xmlLogs = system.exportLogsWithFormatter(new XmlRunningLogFormatter());
                System.out.println(xmlLogs.length() > 100 ? xmlLogs.substring(0, 100) + "..." : xmlLogs);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "测试设计模式出错", e);
        }
    }
}