package cn.edu.nwpu.homesphere;

import java.util.*;
import java.text.SimpleDateFormat;

/**
 * 命令行用户界面类，实现HomeSphere v2.0的所有交互功能
 */
public class CommandUI {
    // HomeSphereSystem的单例实例 - 使用volatile确保可见性
    private static volatile HomeSphereSystem systemInstance;
    private Scanner scanner;
    private boolean isRunning;

    public CommandUI() {
        this.scanner = new Scanner(System.in);
        this.isRunning = true;
        initializeData();
    }

    /**
     * 获取HomeSphereSystem的单例实例 - 双重检查锁定
     * @return HomeSphereSystem的单例实例
     */
    public static HomeSphereSystem getHomeSphereSystemInstance() {
        if (systemInstance == null) {
            synchronized (CommandUI.class) {
                if (systemInstance == null) {
                    systemInstance = new HomeSphereSystem();
                }
            }
        }
        return systemInstance;
    }

    /**
     * 获取当前系统实例（用于其他方法）
     */
    private HomeSphereSystem getSystem() {
        return getHomeSphereSystemInstance();
    }

    /**
     * 初始化数据
     */
    void initializeData() {
        try {
            // 获取单例实例
            HomeSphereSystem system = getHomeSphereSystemInstance();

            // 创建制造商 - 现在每个制造商可以生产多种设备
            Manufacturer acManufacturer = new Manufacturer(1, "空调厂家", "WiFi, ZigBee");
            Manufacturer generalManufacturer = new Manufacturer(2, "通用智能设备厂家", "WiFi, 蓝牙, ZigBee");
            Manufacturer securityManufacturer = new Manufacturer(3, "安防设备厂家", "蓝牙, WiFi");

            System.out.println("开始使用工厂模式创建设备...");

            // 使用工厂方法创建设备 - 空调厂家主要生产空调，但也生产其他设备
            AirConditioner livingRoomAC = (AirConditioner) acManufacturer.produceDevice(
                    Manufacturer.DEVICE_TYPE_AIR_CONDITIONER, 1, "客厅空调", 1500);

            // 通用厂家生产各种设备
            LightBulb livingRoomLight = (LightBulb) generalManufacturer.produceDevice(
                    Manufacturer.DEVICE_TYPE_LIGHT_BULB, 2, "客厅主灯", 20);

            AirConditioner masterBedroomAC = (AirConditioner) generalManufacturer.produceDevice(
                    Manufacturer.DEVICE_TYPE_AIR_CONDITIONER, 5, "主卧空调", 1200);

            // 安防厂家生产智能锁等设备
            SmartLock frontDoorLock = (SmartLock) securityManufacturer.produceDevice(
                    Manufacturer.DEVICE_TYPE_SMART_LOCK, 3, "前门智能锁");

            BathroomScale bathroomScale = (BathroomScale) generalManufacturer.produceDevice(
                    Manufacturer.DEVICE_TYPE_BATHROOM_SCALE, 4, "智能体重秤");

            // 设置设备在线状态
            livingRoomAC.setOnline(true);
            livingRoomLight.setOnline(true);
            frontDoorLock.setOnline(true);
            bathroomScale.setOnline(true);
            masterBedroomAC.setOnline(true);

            // 将设备添加到房间
            Room livingRoom = new Room(1, "客厅", 25.0);
            Room masterBedroom = new Room(2, "主卧", 18.0);
            Room secondBedroom = new Room(3, "次卧", 15.0);

            livingRoom.addDevice(livingRoomAC);
            livingRoom.addDevice(livingRoomLight);
            masterBedroom.addDevice(masterBedroomAC);
            secondBedroom.addDevice(bathroomScale);

            // 创建家庭并添加房间
            User admin = new User(1, "admin", "admin", "管理员", "admin@nwpu.edu.cn", true);
            Household household = new Household(1, "西安市长安区西北工业大学", admin);
            household.addRoom(livingRoom);
            household.addRoom(masterBedroom);
            household.addRoom(secondBedroom);

            // 设置系统家庭（使用单例模式）
            system.setHousehold(household);
            system.addUserToSystem(admin);

            // 创建设备并添加到系统
            system.addDevice(livingRoomAC);
            system.addDevice(livingRoomLight);
            system.addDevice(frontDoorLock);
            system.addDevice(bathroomScale);
            system.addDevice(masterBedroomAC);

            // 创建自动化场景
            AutomationScene morningScene = new AutomationScene(1, "早安场景", "早上起床时自动打开灯光和调整温度");
            morningScene.addAction(new DeviceAction(livingRoomLight, "power_on", ""));
            morningScene.addAction(new DeviceAction(livingRoomLight, "set_brightness", "50"));
            morningScene.addAction(new DeviceAction(livingRoomAC, "set_temperature", "24"));

            AutomationScene nightScene = new AutomationScene(2, "晚安场景", "晚上睡觉时关闭所有设备");
            nightScene.addAction(new DeviceAction(livingRoomLight, "power_off", ""));
            nightScene.addAction(new DeviceAction(livingRoomAC, "power_off", ""));
            nightScene.addAction(new DeviceAction(frontDoorLock, "lock", ""));

            system.addAutoScene(morningScene);
            system.addAutoScene(nightScene);

            // 显示制造商生产统计
            acManufacturer.displayProductionStatistics();
            generalManufacturer.displayProductionStatistics();
            securityManufacturer.displayProductionStatistics();

            System.out.println("数据初始化完成！");

        } catch (Exception e) {
            System.out.println("数据初始化错误: " + e.getMessage());
        }
    }

    /**
     * 启动主菜单
     */
    public void start() {
        while (isRunning) {
            showMainMenu();
            try {
                int choice = readIntInput("请选择操作: ");
                switch (choice) {
                    case 1:
                        login();
                        break;
                    case 2:
                        System.out.println("感谢使用HomeSphere系统，再见！");
                        isRunning = false;
                        break;
                    default:
                        System.out.println("无效选择，请重新输入");
                }
            } catch (InputMismatchException e) {
                System.out.println("输入格式错误，请输入数字");
                scanner.nextLine(); // 清除错误输入
            } catch (Exception e) {
                System.out.println("发生错误: " + e.getMessage());
            }
        }
    }

    /**
     * 显示主菜单
     */
    private void showMainMenu() {
        System.out.println("\n========== HomeSphere 智能家居系统 v2.0 ==========");
        System.out.println("1. 用户登录");
        System.out.println("2. 退出系统");
        System.out.println("=============================================");
    }

    /**
     * 用户登录
     */
    private void login() {
        try {
            System.out.print("请输入用户名: ");
            String username = scanner.nextLine();
            System.out.print("请输入密码: ");
            String password = scanner.nextLine();

            User user = getSystem().login(username, password);
            System.out.println("登录成功！欢迎 " + user.getUserName());
            showFamilyManagementMenu();

        } catch (InvalidUserException e) {
            System.out.println("登录失败: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("登录过程中发生错误: " + e.getMessage());
        }
    }

    /**
     * 显示家庭管理菜单
     */
    private void showFamilyManagementMenu() {
        boolean backToMain = false;

        while (!backToMain) {
            System.out.println("\n========== 家庭管理菜单 ==========");
            System.out.println("1. 成员管理");
            System.out.println("2. 设备管理");
            System.out.println("3. 智能场景管理");
            System.out.println("4. 日志能耗管理");
            System.out.println("5. 返回主菜单");
            System.out.println("=================================");

            try {
                int choice = readIntInput("请选择操作: ");
                switch (choice) {
                    case 1:
                        showMemberManagementMenu();
                        break;
                    case 2:
                        showDeviceManagementMenu();
                        break;
                    case 3:
                        showSceneManagementMenu();
                        break;
                    case 4:
                        showLogEnergyManagementMenu();
                        break;
                    case 5:
                        getSystem().logoff();
                        backToMain = true;
                        System.out.println("已返回主菜单");
                        break;
                    default:
                        System.out.println("无效选择，请重新输入");
                }
            } catch (InputMismatchException e) {
                System.out.println("输入格式错误，请输入数字");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("发生错误: " + e.getMessage());
            }
        }
    }

    /**
     * 显示成员管理菜单
     */
    private void showMemberManagementMenu() {
        boolean backToFamily = false;

        while (!backToFamily) {
            System.out.println("\n========== 成员管理菜单 ==========");
            System.out.println("1. 添加成员");
            System.out.println("2. 删除成员");
            System.out.println("3. 列出所有成员");
            System.out.println("4. 返回上级菜单");
            System.out.println("================================");

            try {
                int choice = readIntInput("请选择操作: ");
                switch (choice) {
                    case 1:
                        addMember();
                        break;
                    case 2:
                        deleteMember();
                        break;
                    case 3:
                        listAllMembers();
                        break;
                    case 4:
                        backToFamily = true;
                        break;
                    default:
                        System.out.println("无效选择，请重新输入");
                }
            } catch (InputMismatchException e) {
                System.out.println("输入格式错误，请输入数字");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("发生错误: " + e.getMessage());
            }
        }
    }

    /**
     * 添加成员
     */
    private void addMember() {
        try {
            System.out.print("请输入用户名: ");
            String username = scanner.nextLine();
            System.out.print("请输入密码: ");
            String password = scanner.nextLine();
            System.out.print("请输入邮箱: ");
            String email = scanner.nextLine();

            // 生成用户ID（简单实现）
            int newUserId = getSystem().getUsers().size() + 1;
            User newUser = new User(newUserId, username, password, username, email, false);
            getSystem().getHousehold().addUser(newUser);
            getSystem().addUserToSystem(newUser);

            System.out.println("成员添加成功！用户ID: " + newUserId);

        } catch (Exception e) {
            System.out.println("添加成员失败: " + e.getMessage());
        }
    }

    /**
     * 删除成员
     */
    private void deleteMember() {
        try {
            int userId = readIntInput("请输入要删除的成员ID: ");

            if (userId == 1) {
                System.out.println("初始admin用户不允许删除");
                return;
            }

            boolean found = false;
            Iterator<User> iterator = getSystem().getUsers().iterator();
            while (iterator.hasNext()) {
                User user = iterator.next();
                if (user.getUserId() == userId) {
                    iterator.remove();
                    getSystem().getHousehold().removeUser(userId);
                    found = true;
                    break;
                }
            }

            if (found) {
                System.out.println("成员删除成功！");
            } else {
                System.out.println("无此成员");
            }

        } catch (InputMismatchException e) {
            System.out.println("输入格式错误，请输入数字");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("删除成员失败: " + e.getMessage());
        }
    }

    /**
     * 列出所有成员
     */
    private void listAllMembers() {
        System.out.println("\n=== 所有成员列表 ===");
        System.out.println("ID\t用户名\t邮箱\t\t管理员");
        System.out.println("----------------------------------------");
        for (User user : getSystem().getUsers()) {
            System.out.printf("%d\t%s\t%s\t%s%n",
                    user.getUserId(),
                    user.getLoginName(),
                    user.getEmail(),
                    user.isAdmin() ? "是" : "否");
        }
    }

    /**
     * 显示设备管理菜单
     */
    private void showDeviceManagementMenu() {
        boolean backToFamily = false;

        while (!backToFamily) {
            System.out.println("\n========== 设备管理菜单 ==========");
            System.out.println("1. 添加设备");
            System.out.println("2. 移除设备");
            System.out.println("3. 列出所有设备");
            System.out.println("4. 返回上级菜单");
            System.out.println("================================");

            try {
                int choice = readIntInput("请选择操作: ");
                switch (choice) {
                    case 1:
                        addDevice();
                        break;
                    case 2:
                        removeDevice();
                        break;
                    case 3:
                        listAllDevices();
                        break;
                    case 4:
                        backToFamily = true;
                        break;
                    default:
                        System.out.println("无效选择，请重新输入");
                }
            } catch (InputMismatchException e) {
                System.out.println("输入格式错误，请输入数字");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("发生错误: " + e.getMessage());
            }
        }
    }

    /**
     * 添加设备
     */
    private void addDevice() {
        try {
            System.out.print("请输入设备名称: ");
            String name = scanner.nextLine();
            System.out.print("请输入设备类型(1-空调 2-灯泡 3-智能锁 4-体重秤): ");
            int type = readIntInput("");

            // 选择制造商
            System.out.println("请选择制造商: ");
            System.out.println("1. 空调厂家 (专精空调)");
            System.out.println("2. 通用智能设备厂家 (生产各种设备)");
            System.out.println("3. 安防设备厂家 (专精安防设备)");
            int manufacturerChoice = readIntInput("选择制造商: ");

            Manufacturer selectedManufacturer = null;
            String deviceType = "";

            switch (manufacturerChoice) {
                case 1:
                    selectedManufacturer = new Manufacturer(1, "空调厂家", "WiFi, ZigBee");
                    break;
                case 2:
                    selectedManufacturer = new Manufacturer(2, "通用智能设备厂家", "WiFi, 蓝牙, ZigBee");
                    break;
                case 3:
                    selectedManufacturer = new Manufacturer(3, "安防设备厂家", "蓝牙, WiFi");
                    break;
                default:
                    System.out.println("无效的制造商选择，使用默认通用厂家");
                    selectedManufacturer = new Manufacturer(2, "通用智能设备厂家", "WiFi, 蓝牙, ZigBee");
            }

            System.out.println("请选择房间: ");
            List<Room> rooms = getSystem().getHousehold().getRooms();
            for (int i = 0; i < rooms.size(); i++) {
                System.out.println((i + 1) + ". " + rooms.get(i).getName());
            }
            int roomChoice = readIntInput("") - 1;

            if (roomChoice < 0 || roomChoice >= rooms.size()) {
                System.out.println("无效的房间选择");
                return;
            }

            Room selectedRoom = rooms.get(roomChoice);
            Device newDevice = null;
            int newDeviceId = getSystem().getAllDevices().size() + 1;

            // 使用工厂方法创建设备
            switch (type) {
                case 1: // 空调
                    deviceType = Manufacturer.DEVICE_TYPE_AIR_CONDITIONER;
                    newDevice = selectedManufacturer.produceDevice(deviceType, newDeviceId, name, 1500);
                    break;
                case 2: // 灯泡
                    deviceType = Manufacturer.DEVICE_TYPE_LIGHT_BULB;
                    newDevice = selectedManufacturer.produceDevice(deviceType, newDeviceId, name, 20);
                    break;
                case 3: // 智能锁
                    deviceType = Manufacturer.DEVICE_TYPE_SMART_LOCK;
                    newDevice = selectedManufacturer.produceDevice(deviceType, newDeviceId, name);
                    break;
                case 4: // 体重秤
                    deviceType = Manufacturer.DEVICE_TYPE_BATHROOM_SCALE;
                    newDevice = selectedManufacturer.produceDevice(deviceType, newDeviceId, name);
                    break;
                default:
                    System.out.println("无效的设备类型");
                    return;
            }

            if (newDevice != null) {
                newDevice.setOnline(true);
                getSystem().addDevice(newDevice);
                selectedRoom.addDevice(newDevice);
                System.out.println("设备添加成功！设备ID: " + newDeviceId);
                System.out.println("由 " + selectedManufacturer.getName() + " 生产");
            } else {
                System.out.println("设备创建失败");
            }

        } catch (InputMismatchException e) {
            System.out.println("输入格式错误，请输入数字");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("添加设备失败: " + e.getMessage());
        }
    }

    /**
     * 移除设备
     */
    private void removeDevice() {
        try {
            int deviceId = readIntInput("请输入要移除的设备ID: ");

            boolean found = false;
            Iterator<Device> iterator = getSystem().getAllDevices().iterator();
            while (iterator.hasNext()) {
                Device device = iterator.next();
                if (device.getDeviceId() == deviceId) {
                    iterator.remove();
                    // 从所有房间中移除该设备
                    for (Room room : getSystem().getHousehold().getRooms()) {
                        room.removeDevice(deviceId);
                    }
                    found = true;
                    break;
                }
            }

            if (found) {
                System.out.println("设备移除成功！");
            } else {
                System.out.println("无此设备");
            }

        } catch (InputMismatchException e) {
            System.out.println("输入格式错误，请输入数字");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("移除设备失败: " + e.getMessage());
        }
    }

    /**
     * 列出所有设备
     */
    private void listAllDevices() {
        System.out.println("\n=== 所有设备列表（按房间显示）===");
        for (Room room : getSystem().getHousehold().getRooms()) {
            System.out.println("\n房间: " + room.getName() + " (面积: " + room.getArea() + "㎡)");
            System.out.println("设备列表:");
            System.out.println("ID\t名称\t\t类型\t状态");
            System.out.println("----------------------------------------");

            for (Device device : room.getDevices()) {
                String type = getDeviceType(device);
                String status = device.isOnline() ? "在线" : "离线";
                System.out.printf("%d\t%s\t%s\t%s%n",
                        device.getDeviceId(),
                        device.getName(),
                        type,
                        status);
            }
        }
    }

    /**
     * 获取设备类型
     */
    private String getDeviceType(Device device) {
        if (device instanceof AirConditioner) return "空调";
        if (device instanceof LightBulb) return "灯泡";
        if (device instanceof SmartLock) return "智能锁";
        if (device instanceof BathroomScale) return "体重秤";
        return "未知设备";
    }

    /**
     * 显示场景管理菜单
     */
    private void showSceneManagementMenu() {
        boolean backToFamily = false;

        while (!backToFamily) {
            System.out.println("\n========== 智能场景管理菜单 ==========");
            System.out.println("1. 创建场景");
            System.out.println("2. 触发场景");
            System.out.println("3. 列出所有场景");
            System.out.println("4. 返回上级菜单");
            System.out.println("===================================");

            try {
                int choice = readIntInput("请选择操作: ");
                switch (choice) {
                    case 1:
                        createScene();
                        break;
                    case 2:
                        triggerScene();
                        break;
                    case 3:
                        listAllScenes();
                        break;
                    case 4:
                        backToFamily = true;
                        break;
                    default:
                        System.out.println("无效选择，请重新输入");
                }
            } catch (InputMismatchException e) {
                System.out.println("输入格式错误，请输入数字");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("发生错误: " + e.getMessage());
            }
        }
    }


    /**
     * 显示设备支持的命令
     */
    private void displayDeviceCommands(Device device) {
        if (device instanceof LightBulb) {
            System.out.println("  - power_on: 打开电源");
            System.out.println("  - power_off: 关闭电源");
            System.out.println("  - set_brightness: 设置亮度(0-100)");
            System.out.println("  - set_colortemp: 设置色温(2700-6500)");
        } else if (device instanceof AirConditioner) {
            System.out.println("  - power_on: 打开电源");
            System.out.println("  - power_off: 关闭电源");
            System.out.println("  - set_temperature: 设置温度(16.0-32.0)");
        } else if (device instanceof SmartLock) {
            System.out.println("  - lock: 锁定");
            System.out.println("  - unlock: 解锁");
        } else if (device instanceof BathroomScale) {
            System.out.println("  - measure_weight: 测量体重");
        }
    }


    /**
     * 读取亮度输入
     */
    private String readBrightnessInput() {
        while (true) {
            try {
                System.out.print("请输入亮度值(0-100): ");
                int brightness = scanner.nextInt();
                scanner.nextLine(); // 消耗换行符

                if (brightness >= 0 && brightness <= 100) {
                    return String.valueOf(brightness);
                } else {
                    System.out.println("亮度值必须在0-100之间，请重新输入");
                }
            } catch (InputMismatchException e) {
                System.out.println("输入格式错误，请输入整数");
                scanner.nextLine();
            }
        }
    }



    /**
     * 读取温度输入
     */
    private String readTemperatureInput() {
        while (true) {
            try {
                System.out.print("请输入温度值(16.0-32.0): ");
                double temperature = scanner.nextDouble();
                scanner.nextLine(); // 消耗换行符

                if (temperature >= 16.0 && temperature <= 32.0) {
                    return String.valueOf(temperature);
                } else {
                    System.out.println("温度值必须在16.0-32.0之间，请重新输入");
                }
            } catch (InputMismatchException e) {
                System.out.println("输入格式错误，请输入数字");
                scanner.nextLine();
            }
        }
    }


    /**
     * 读取色温输入
     */
    private String readColorTempInput() {
        while (true) {
            try {
                System.out.print("请输入色温值(2300-7000): ");
                int colorTemp = scanner.nextInt();
                scanner.nextLine(); // 消耗换行符

                if (colorTemp >= 2300 && colorTemp <= 7000) {
                    return String.valueOf(colorTemp);
                } else {
                    System.out.println("色温值必须在2300-7000之间，请重新输入");
                }
            } catch (InputMismatchException e) {
                System.out.println("输入格式错误，请输入整数");
                scanner.nextLine();
            }
        }
    }

    /**
     * 读取体重输入
     */
    private String readWeightInput() {
        while (true) {
            try {
                System.out.print("请输入体重值(kg): ");
                double weight = scanner.nextDouble();
                scanner.nextLine(); // 消耗换行符

                if (weight > 0) {
                    return String.valueOf(weight);
                } else {
                    System.out.println("体重值必须大于0，请重新输入");
                }
            } catch (InputMismatchException e) {
                System.out.println("输入格式错误，请输入数字");
                scanner.nextLine();
            }
        }
    }


    /**
     * 创建场景
     */
    private void createScene() {
        try {
            System.out.print("请输入场景名称: ");
            String name = scanner.nextLine();
            System.out.print("请输入场景描述: ");
            String description = scanner.nextLine();
            int newSceneId = getSystem().getHousehold().getAutoScenes().size() + 1;
            AutomationScene newScene = new AutomationScene(newSceneId, name, description);

            // 交互优化：先询问是否添加设备操作（y/n）
            boolean addingActions = true;
            while (addingActions) {
                System.out.print("\n添加设备操作?(y/n): ");
                String addChoice = scanner.nextLine().trim().toLowerCase();

                if (!"y".equals(addChoice)) {
                    // 若选择"否"，检查是否已有操作（至少1个操作）
                    if (newScene.getActions().isEmpty()) {
                        System.out.println("警告：场景至少需要一个设备操作！");
                        continue;
                    }
                    addingActions = false;
                    continue;
                }

                // 选择"是"，显示可用设备列表并输入设备ID
                System.out.println("\n--- 可用设备列表 ---");
                listAllDevicesForScene(); // 复用原设备列表显示方法
                System.out.print("请输入设备ID: ");
                int deviceId = readIntInput(""); // 复用原整数输入方法

                // 校验设备是否存在
                Device selectedDevice = findDeviceById(deviceId);
                if (selectedDevice == null) {
                    System.out.println("设备不存在，请重新输入");
                    continue;
                }

                // 显示设备支持的命令并输入
                System.out.println("设备 " + selectedDevice.getName() + " 支持的操作:");
                displayDeviceCommands(selectedDevice); // 复用原命令显示方法
                System.out.print("请输入操作命令: ");
                String command = scanner.nextLine().trim();

                // 根据命令类型获取参数（复用原参数输入方法）
                String parameters = "";
                if (command.toLowerCase().contains("brightness")) {
                    parameters = readBrightnessInput();
                } else if (command.toLowerCase().contains("temperature")) {
                    parameters = readTemperatureInput();
                } else if (command.toLowerCase().contains("colortemp")) {
                    parameters = readColorTempInput();
                } else if (command.toLowerCase().contains("weight")) {
                    parameters = readWeightInput();
                }

                // 添加操作到场景
                DeviceAction action = new DeviceAction(selectedDevice, command, parameters);
                newScene.addAction(action);
                System.out.println("✓ 操作添加成功！当前场景中的设备操作:");
                // 显示当前场景已添加的操作（符合文档"当前场景中的设备操作"展示要求）
                for (DeviceAction act : newScene.getActions()) {
                    System.out.println("  - " + act.getDevice().getName() + "-" + act.getCommand() +
                            (act.getParameters().isEmpty() ? "" : " " + act.getParameters()));
                }
            }

            // 保存场景到系统
            getSystem().addAutoScene(newScene);
            getSystem().getHousehold().addAutoScene(newScene);
            System.out.println("\n✓ 场景创建成功！场景ID: " + newSceneId);
        } catch (InputMismatchException e) {
            System.out.println("输入格式错误，请输入数字");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("创建场景失败: " + e.getMessage());
        }
    }

    /**
     * 列出场景可用设备
     */
    private void listAllDevicesForScene() {
        System.out.println("ID\t名称\t\t类型");
        System.out.println("----------------------------------------");
        for (Device device : getSystem().getAllDevices()) {
            String type = getDeviceType(device);
            System.out.printf("%d\t%s\t%s%n",
                    device.getDeviceId(),
                    device.getName(),
                    type);
        }
    }

    /**
     * 根据ID查找设备
     */
    private Device findDeviceById(int deviceId) {
        for (Device device : getSystem().getAllDevices()) {
            if (device.getDeviceId() == deviceId) {
                return device;
            }
        }
        return null;
    }

    /**
     * 触发场景
     */
    private void triggerScene() {
        try {
            listAllScenes(); // 先显示所有场景

            int sceneId = readIntInput("\n请输入要触发的场景ID: ");

            boolean found = false;
            for (AutomationScene scene : getSystem().getHousehold().getAutoScenes()) {
                if (scene.getSceneld() == sceneId) {
                    System.out.println("正在触发场景: " + scene.getName());
                    scene.manualTrig();
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("场景不存在，请检查场景ID");
            }

        } catch (InputMismatchException e) {
            System.out.println("输入格式错误，请输入数字");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("触发场景失败: " + e.getMessage());
        }
    }

    /**
     * 列出所有场景
     */
    private void listAllScenes() {
        List<AutomationScene> scenes = getSystem().getHousehold().getAutoScenes();

        if (scenes.isEmpty()) {
            System.out.println("\n当前没有场景");
            return;
        }

        System.out.println("\n=== 所有场景列表 ===");
        System.out.println("+----+------------+----------------------+----------+");
        System.out.println("| ID |    名称    |        描述          | 操作数量 |");
        System.out.println("+----+------------+----------------------+----------+");

        for (AutomationScene scene : scenes) {
            String name = scene.getName().length() > 10 ? scene.getName().substring(0, 7) + "..." : scene.getName();
            String desc = scene.getDescription().length() > 20 ? scene.getDescription().substring(0, 17) + "..." : scene.getDescription();

            System.out.printf("| %2d | %-10s | %-20s | %8d |\n",
                    scene.getSceneld(),
                    name,
                    desc,
                    scene.getActions().size());
        }
        System.out.println("+----+------------+----------------------+----------+");

        // 显示场景详情
        System.out.println("\n场景详情:");
        for (AutomationScene scene : scenes) {
            System.out.println("\n场景 " + scene.getSceneld() + ": " + scene.getName());
            System.out.println("描述: " + scene.getDescription());
            System.out.println("包含操作:");
            for (DeviceAction action : scene.getActions()) {
                System.out.println("  - " + action.getDevice().getName() + ": " +
                        action.getCommand() + " " + action.getParameters());
            }
        }
    }

    /**
     * 显示日志能耗管理菜单
     */
    private void showLogEnergyManagementMenu() {
        boolean backToFamily = false;

        while (!backToFamily) {
            System.out.println("\n========== 日志能耗管理菜单 ==========");
            System.out.println("1. 查看设备运行日志");
            System.out.println("2. 查看能耗报告");
            System.out.println("3. 导出运行日志");
            System.out.println("4. 返回上级菜单");
            System.out.println("===================================");

            try {
                int choice = readIntInput("请选择操作: ");
                switch (choice) {
                    case 1:
                        viewDeviceLogs();
                        break;
                    case 2:
                        viewEnergyReport();
                        break;
                    case 3:
                        exportRunningLogs();
                        break;
                    case 4:
                        backToFamily = true;
                        break;
                    default:
                        System.out.println("无效选择，请重新输入");
                }
            } catch (InputMismatchException e) {
                System.out.println("输入格式错误，请输入数字");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("发生错误: " + e.getMessage());
            }
        }
    }

    /**
     * 导出运行日志
     */
    private void exportRunningLogs() {
        try {
            System.out.println("\n=== 导出运行日志 ===");
            System.out.println("请选择导出格式:");

            RunningLogFormatter[] formatters = getSystem().getAvailableLogFormatters();
            for (int i = 0; i < formatters.length; i++) {
                System.out.println((i + 1) + ". " + formatters[i].getFormatterName());
            }

            int choice = readIntInput("请选择格式: ") - 1;
            if (choice < 0 || choice >= formatters.length) {
                System.out.println("无效的选择");
                return;
            }

            RunningLogFormatter formatter = formatters[choice];
            String formattedLogs = getSystem().exportLogsWithFormatter(formatter);

            // 显示部分内容预览
            System.out.println("\n=== 预览 (前500字符) ===");
            String preview = formattedLogs.length() > 500 ?
                    formattedLogs.substring(0, 500) + "..." : formattedLogs;
            System.out.println(preview);

            // 询问是否保存到文件
            System.out.print("\n是否保存到文件? (y/n): ");
            String saveChoice = scanner.nextLine().trim().toLowerCase();
            if ("y".equals(saveChoice)) {
                System.out.print("请输入文件名: ");
                String filename = scanner.nextLine().trim();
                if (!filename.contains(".")) {
                    // 根据格式添加扩展名
                    switch (formatter.getFormatterName()) {
                        case "JSON格式": filename += ".json"; break;
                        case "HTML格式": filename += ".html"; break;
                        case "XML格式": filename += ".xml"; break;
                    }
                }

                try {
                    java.nio.file.Files.write(
                            java.nio.file.Paths.get(filename),
                            formattedLogs.getBytes()
                    );
                    System.out.println("文件保存成功: " + filename);

                    // 如果是HTML文件，提示可以在浏览器中打开
                    if (filename.endsWith(".html")) {
                        System.out.println("提示: 您可以在浏览器中打开 " + filename + " 文件查看格式化的日志");
                    }
                } catch (Exception e) {
                    System.out.println("文件保存失败: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.out.println("导出运行日志失败: " + e.getMessage());
        }
    }

    /**
     * 查看设备运行日志
     */
    private void viewDeviceLogs() {
        System.out.println("\n=== 设备运行日志 ===");

        boolean hasLogs = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 按设备分组显示日志
        for (Room room : getSystem().getHousehold().getRooms()) {
            for (Device device : room.getDevices()) {
                List<RunningLog> logs = device.getRunningLogs();
                if (!logs.isEmpty()) {
                    hasLogs = true;
                    System.out.println("\n设备: " + device.getName() + " (房间: " + room.getName() + ")");
                    System.out.println("+---------------------+------------------+----------+----------------------+");
                    System.out.println("|        时间         |      事件        |   类型   |         备注         |");
                    System.out.println("+---------------------+------------------+----------+----------------------+");

                    for (RunningLog log : logs) {
                        String event = log.getEvent().length() > 16 ? log.getEvent().substring(0, 13) + "..." : log.getEvent();
                        String note = log.getNote().length() > 20 ? log.getNote().substring(0, 17) + "..." : log.getNote();

                        System.out.printf("| %-19s | %-16s | %-8s | %-20s |\n",
                                sdf.format(log.getDateTime()),
                                event,
                                log.getType().name(),
                                note);
                    }
                    System.out.println("+---------------------+------------------+----------+----------------------+");
                }
            }
        }

        if (!hasLogs) {
            System.out.println("暂无设备运行日志");
        }
    }

    /**
     * 查看能耗报告
     */
    /**
     * 查看能耗报告（按实验文档格式优化）
     */
    private void viewEnergyReport() {
        try {
            System.out.println("\n=== 能耗报告查询 ===");
            System.out.print("请输入起始时间 (格式: yyyy-MM-dd，例如: 2024-01-01): ");
            String startDateStr = scanner.nextLine();
            System.out.print("请输入结束时间 (格式: yyyy-MM-dd，例如: 2024-01-31): ");
            String endDateStr = scanner.nextLine();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);

            // 验证日期范围
            if (endDate.before(startDate)) {
                System.out.println("错误：结束时间不能早于开始时间");
                return;
            }

            // 按实验文档格式输出：先显示时间范围
            System.out.println("\n=== 能耗报告 ===");
            System.out.println("时间范围: " + startDateStr + " 至 " + endDateStr);
            double totalEnergy = 0;

            // 遍历房间，按房间分组显示设备能耗
            for (Room room : getSystem().getHousehold().getRooms()) {
                System.out.println("房间: " + room.getName());
                boolean hasDeviceEnergy = false;

                // 遍历房间内设备，仅显示有能耗的设备（或无能耗时显示"无设备能耗数据"）
                for (Device device : room.getDevices()) {
                    if (device instanceof EnergyReporting) {
                        EnergyReporting energyDevice = (EnergyReporting) device;
                        double energy = energyDevice.getReport(startDate, endDate);
                        System.out.printf("  设备: %s - %.1f kWh%n", device.getName(), energy);
                        totalEnergy += energy;
                        hasDeviceEnergy = true;
                    }
                }

                // 若房间内无能耗设备，提示"无设备能耗数据"
                if (!hasDeviceEnergy) {
                    System.out.println("  无设备能耗数据");
                }
            }

            // 输出总能耗（按文档格式简化统计信息）
            System.out.println("总能耗: " + totalEnergy + " kWh");

        } catch (java.text.ParseException e) {
            System.out.println("日期格式错误，请使用 yyyy-MM-dd 格式");
        } catch (Exception e) {
            System.out.println("查看能耗报告失败: " + e.getMessage());
        }
    }

    /**
     * 读取整数输入
     */
    private int readIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int input = scanner.nextInt();
                scanner.nextLine(); // 消耗换行符
                return input;
            } catch (InputMismatchException e) {
                System.out.println("输入格式错误，请输入数字");
                scanner.nextLine(); // 清除错误输入
            }
        }
    }

    /**
     * 主方法
     */
    public static void main(String[] args) {
        CommandUI ui = new CommandUI();
        ui.start();
    }
}
