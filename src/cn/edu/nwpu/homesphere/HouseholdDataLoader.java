package cn.edu.nwpu.homesphere;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * 家庭数据加载器
 */
public class HouseholdDataLoader {
    private HomeSphereSystem system;

    public HouseholdDataLoader(HomeSphereSystem system) {
        this.system = system;
    }

    /**
     * 从文件加载数据，按照顺序解析
     */
    public void loadFromFile(String filename) throws IOException {
        System.out.println("开始加载文件: " + filename);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filename), "UTF-8"))) {
            String line;
            int lineCount = 0;

            while ((line = reader.readLine()) != null) {
                lineCount++;
                line = line.trim();
                if (line.isEmpty()) continue;

                try {
                    parseLineInOrder(line, lineCount);
                } catch (Exception e) {
                    System.err.println("第 " + lineCount + " 行解析失败: " + line);
                    System.err.println("错误详情: " + e.getMessage());
                }
            }

            // 验证加载结果
            validateLoadedData();

        } catch (FileNotFoundException e) {
            System.err.println("文件未找到: " + filename);
            throw e;
        }
    }

    /**
     * 按照固定顺序解析每一行
     */
    private void parseLineInOrder(String line, int lineCount) {
        // 根据行的开头内容判断类型
        if (line.startsWith("Household{")) {
            parseHousehold(line);
        } else if (line.startsWith("Room{")) {
            parseRoom(line);
        } else if (line.startsWith("User{")) {
            parseUser(line);
        } else if (line.startsWith("Manufacturer{")) {
            parseManufacturer(line);
        } else if (line.startsWith("AirConditioner{")) {
            parseAirConditioner(line);
        } else if (line.startsWith("LightBulb{")) {
            parseLightBulb(line);
        } else if (line.startsWith("SmartLock{")) {
            parseSmartLock(line);
        } else if (line.startsWith("AutomationScene{")) {
            parseAutomationSceneSimple(line);
        } else {
            System.err.println("未知的数据类型: " + line);
        }

        System.out.println("第 " + lineCount + " 行解析成功: " + line);
    }

    private void parseHousehold(String line) {
        // 提取属性部分
        String properties = line.substring("Household{".length(), line.length() - 1);
        Map<String, String> props = parseProperties(properties);

        int householdId = Integer.parseInt(props.get("householdId"));
        String address = removeQuotes(props.get("address"));
        int adminId = Integer.parseInt(props.get("adminId"));

        User tempAdmin = new User(adminId, "temp_admin", "temp", "临时管理员", "temp@temp.com", true);

        Household household = new Household(householdId, address, tempAdmin);
        system.setHousehold(household);
        system.addUserToSystem(tempAdmin);

        System.out.println("家庭创建成功: " + address);
    }

    private void parseRoom(String line) {
        // 提取属性部分
        String properties = line.substring("Room{".length(), line.length() - 1);
        Map<String, String> props = parseProperties(properties);

        int roomId = Integer.parseInt(props.get("roomId"));
        String name = removeQuotes(props.get("name"));
        double area = Double.parseDouble(props.get("area"));

        Room room = new Room(roomId, name, area);

        // 添加到家庭
        if (system.getHousehold() != null) {
            system.getHousehold().addRoom(room);
            System.out.println("房间添加成功: " + name);
        } else {
            System.err.println("错误: 家庭不存在，无法添加房间");
        }
    }

    private void parseUser(String line) {
        // 提取属性部分
        String properties = line.substring("User{".length(), line.length() - 1);
        Map<String, String> props = parseProperties(properties);

        int userId = Integer.parseInt(props.get("userId"));
        String username = removeQuotes(props.get("username"));
        String email = removeQuotes(props.get("email"));
        boolean isAdmin = Boolean.parseBoolean(props.get("isAdmin"));

        // 用户
        User user = new User(userId, username, "default", username, email, isAdmin);

        // 更新系统中的用户和家庭管理员
        system.addUserToSystem(user);
        if (system.getHousehold() != null) {
            system.getHousehold().setAdmin(user);
        }

        System.out.println("用户创建成功: " + username);
    }

    private void parseManufacturer(String line) {
        // 提取属性部分
        String properties = line.substring("Manufacturer{".length(), line.length() - 1);
        Map<String, String> props = parseProperties(properties);

        int manufacturerId = Integer.parseInt(props.get("manufacturerId"));
        String name = removeQuotes(props.get("name"));
        String protocols = removeQuotes(props.get("protocols"));

        Manufacturer manufacturer = new Manufacturer(manufacturerId, name, protocols);

        // 保存制造商到系统
        System.out.println("制造商创建成功: " + name);
    }

    private void parseAirConditioner(String line) {
        // 提取属性部分
        String properties = line.substring("AirConditioner{".length(), line.length() - 1);
        Map<String, String> props = parseProperties(properties);

        int deviceId = Integer.parseInt(props.get("deviceId"));
        String name = removeQuotes(props.get("name"));
        int manufacturerId = Integer.parseInt(props.get("manufacturerId"));
        double currTemp = Double.parseDouble(props.get("currTemp"));
        double targetTemp = Double.parseDouble(props.get("targetTemp"));
        int roomId = Integer.parseInt(props.get("roomId"));

        // 制造商
        Manufacturer manufacturer = new Manufacturer(manufacturerId,
                manufacturerId == 1 ? "米家智能家居有限公司" : "格林智能家居设备制造厂",
                manufacturerId == 1 ? "ZigBee" : "WiFi");

        AirConditioner ac = new AirConditioner(deviceId, name, manufacturer, 1500);
        ac.setCurrTemp(currTemp);
        ac.setTargetTemp(targetTemp);

        // 添加到系统和房间
        system.addDevice(ac);
        addDeviceToRoom(ac, roomId, name);
    }

    private void parseLightBulb(String line) {
        // 提取属性部分
        String properties = line.substring("LightBulb{".length(), line.length() - 1);
        Map<String, String> props = parseProperties(properties);

        int deviceId = Integer.parseInt(props.get("deviceId"));
        String name = removeQuotes(props.get("name"));
        int manufacturerId = Integer.parseInt(props.get("manufacturerId"));
        int brightness = Integer.parseInt(props.get("brightness"));
        int colorTemp = Integer.parseInt(props.get("colorTemp"));
        int roomId = Integer.parseInt(props.get("roomId"));

        // 创建制造商
        Manufacturer manufacturer = new Manufacturer(manufacturerId,
                manufacturerId == 1 ? "米家智能家居有限公司" : "格林智能家居设备制造厂",
                manufacturerId == 1 ? "ZigBee" : "WiFi");

        LightBulb light = new LightBulb(deviceId, name, manufacturer, 20);
        light.setBrightness(brightness);
        light.setColorTemp(colorTemp);

        // 添加到系统和房间
        system.addDevice(light);
        addDeviceToRoom(light, roomId, name);
    }

    private void parseSmartLock(String line) {
        // 提取属性部分
        String properties = line.substring("SmartLock{".length(), line.length() - 1);
        Map<String, String> props = parseProperties(properties);

        int deviceId = Integer.parseInt(props.get("deviceId"));
        String name = removeQuotes(props.get("name"));
        int manufacturerId = Integer.parseInt(props.get("manufacturerId"));
        boolean isLocked = Boolean.parseBoolean(props.get("isLocked"));
        int batteryLevel = Integer.parseInt(props.get("batteryLevel"));
        int roomId = Integer.parseInt(props.get("roomId"));

        // 创建制造商
        Manufacturer manufacturer = new Manufacturer(manufacturerId,
                manufacturerId == 1 ? "米家智能家居有限公司" : "格林智能家居设备制造厂",
                manufacturerId == 1 ? "ZigBee" : "WiFi");

        SmartLock lock = new SmartLock(deviceId, name, manufacturer);
        lock.setLocked(isLocked);
        lock.setBatteryLevel(batteryLevel);

        // 添加到系统和房间
        system.addDevice(lock);
        addDeviceToRoom(lock, roomId, name);
    }

    private void parseAutomationSceneSimple(String line) {

        String properties = line.substring("AutomationScene{".length(), line.length() - 1);
        Map<String, String> props = parseProperties(properties);

        int sceneId = Integer.parseInt(props.get("sceneId"));
        String name = removeQuotes(props.get("name"));
        String description = removeQuotes(props.get("description"));

        // 创建空场景
        AutomationScene scene = new AutomationScene(sceneId, name, description);

        system.addAutoScene(scene);
        if (system.getHousehold() != null) {
            system.getHousehold().addAutoScene(scene);
        }

        System.out.println("场景创建成功: " + name + " (动作解析已跳过)");
    }

    /**
     * 将设备添加到指定房间
     */
    private void addDeviceToRoom(Device device, int roomId, String deviceName) {
        if (system.getHousehold() != null) {
            for (Room room : system.getHousehold().getRooms()) {
                if (room.getRoomId() == roomId) {
                    room.addDevice(device);
                    System.out.println("设备添加成功: " + deviceName + " -> " + room.getName());
                    return;
                }
            }
            System.err.println("错误: 房间ID " + roomId + " 不存在，设备 " + deviceName + " 无法添加");
        } else {
            System.err.println("错误: 家庭不存在，设备 " + deviceName + " 无法添加到房间");
        }
    }

    /**
     * 解析属性键值对
     */
    private Map<String, String> parseProperties(String properties) {
        Map<String, String> result = new HashMap<>();

        // 使用正则表达式匹配键值对
        Pattern pattern = Pattern.compile("(\\w+)=([^,]+(?:'[^']*'[^,]*)?)");
        Matcher matcher = pattern.matcher(properties);

        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2).trim();
            result.put(key, value);
        }

        return result;
    }

    /**
     * 移除字符串值的引号
     */
    private String removeQuotes(String value) {
        if (value == null) return "";
        value = value.trim();
        if (value.startsWith("'") && value.endsWith("'")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private void validateLoadedData() {
        System.out.println("\n=== 数据加载验证 ===");
        System.out.println("家庭: " + (system.getHousehold() != null ? system.getHousehold().getAddress() : "null"));

        if (system.getHousehold() != null) {
            System.out.println("房间数量: " + system.getHousehold().getRooms().size());
            System.out.println("场景数量: " + system.getHousehold().getAutoScenes().size());

            // 计算设备总数
            int totalDevices = 0;
            for (Room room : system.getHousehold().getRooms()) {
                totalDevices += room.getDevices().size();
            }
            System.out.println("设备数量: " + totalDevices);
        }

        if (system.getHousehold() == null) {
            System.err.println("警告: 家庭数据加载失败！");
        } else {
            System.out.println("数据加载完成！");
        }
    }
}