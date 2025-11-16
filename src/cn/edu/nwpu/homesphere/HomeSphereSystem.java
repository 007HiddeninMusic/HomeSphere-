package cn.edu.nwpu.homesphere;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * HomeSphere智能家居生态系统的主类，负责管理用户、设备、房间和自动化场景等核心功能
 */
public class HomeSphereSystem {
    private static final Logger LOGGER = Logger.getLogger(HomeSphereSystem.class.getName());
    private List<User> users;
    private List<Device> devices;
    private List<AutomationScene> autoScenes;
    private Household household;
    private User currentUser;
    private static volatile HomeSphereSystem instance;

    
    /**
     * 构造函数，初始化系统组件
     */
    public HomeSphereSystem() {
        this.users = new ArrayList<>();
        this.devices = new ArrayList<>();
        this.autoScenes = new ArrayList<>();
        this.household = null;
        this.currentUser = null;
    }

    public HomeSphereSystem(Household household) {
        this(); // 调用无参构造器初始化其他成员
        this.household = household; // 初始化household成员
        // 确保household与system关联
        if (household != null) {
            household.setSystem(this);
            // 将household中已有的用户同步到系统
            for (User user : household.getUsers()) {
                if (!users.contains(user)) {
                    users.add(user);
                }
            }
        }
    }


    /**
     * 获取单例实例
     * @return HomeSphereSystem单例
     */
    public static HomeSphereSystem getInstance() {
        if (instance == null) {
            synchronized (HomeSphereSystem.class) {
                if (instance == null) {
                    instance = new HomeSphereSystem();
                }
            }
        }
        return instance;
    }




    /**
     * 用户登录功能
     * @param loginName 登录名
     * @param loginPassword 登录密码
     * @return 是否登录成功
     */
    public User login(String loginName, String loginPassword) throws InvalidUserException {
        if (users.isEmpty()) {
            createDefaultAdminUser();
        }
        for (User user : users) {
            if (user.getLoginName().equals(loginName)) {
                if (user.getLoginPassword().equals(loginPassword)) {
                    currentUser = user;
                    LOGGER.info("用户登录成功: " + loginName);
                    return user;
                } else {
                    throw new InvalidUserException("密码错误");
                }
            }
        }
        throw new InvalidUserException("用户名不存在");
    }

    /**
     * 创建默认管理员用户
     */
    private void createDefaultAdminUser() {
        User admin = new User(1, "admin", "admin", "系统管理员", "admin@homesphere.com", true);
        users.add(admin);

        // 如果还没有家庭，创建默认家庭
        if (household == null) {
            household = new Household(1, "默认家庭地址", admin);
            household.setSystem(this);
        } else {
            // 确保家庭中有admin用户
            household.addUser(admin);
        }

        LOGGER.info("创建默认管理员用户: admin/admin");
    }
    /**
     * 用户登出功能
     */
    public void logoff() {
        if (currentUser != null) {
            LOGGER.info("用户登出: " + currentUser.getLoginName());
            currentUser = null;
        }
    }
    
    /**
     * 用户注册功能
     * @param loginName 登录名
     * @param loginPassword 登录密码
     * @param username 用户姓名
     * @param email 电子邮件
     * @return 注册的用户对象
     */
    public User register(String loginName, String loginPassword, String username, String email) {
        // 检查用户名是否已存在
        for (User user : users) {
            if (user.getLoginName().equals(loginName)) {
                LOGGER.warning("注册失败: 用户名已存在");
                return null;
            }
        }

        // 创建新用户 - 确保ID不冲突
        int userId = 1;
        if (!users.isEmpty()) {
            // 找到最大的用户ID并加1
            int maxId = 0;
            for (User user : users) {
                if (user.getUserId() > maxId) {
                    maxId = user.getUserId();
                }
            }
            userId = maxId + 1;
        }

        // 同时检查household中的用户ID
        if (household != null) {
            for (User user : household.getUsers()) {
                if (user.getUserId() >= userId) {
                    userId = user.getUserId() + 1;
                }
            }
        }

        User newUser = new User(userId, loginName, loginPassword, username, email, false);
        users.add(newUser);

        // 如果是第一个用户，设为管理员
        if (users.size() == 1) {
            newUser.setAdmin(true);
            // 如果还没有家庭，创建家庭
            if (household == null) {
                household = new Household(1, "默认家庭地址", newUser);
                household.setSystem(this);
            }
        }

        // 确保用户被添加到家庭
        if (household != null) {
            household.addUser(newUser);
        }

        LOGGER.info("用户注册成功: " + loginName);
        return newUser;
    }
    
    /**
     * 显示所有用户
     */
    public void displayUsers() {
        if (!checkAdmin()) return;
        
        System.out.println("所有用户列表:");
        for (User user : users) {
            System.out.println(user);
        }
    }
    
    /**
     * 显示所有设备
     */
    public void displayDevices() {
        if (currentUser == null) {
            System.out.println("请先登录");
            return;
        }
        
        System.out.println("所有设备列表:");
        for (Device device : devices) {
            System.out.println(device);
        }
    }
    
    /**
     * 显示所有自动化场景
     */
    public void displayAutoScenes() {
        if (currentUser == null) {
            System.out.println("请先登录");
            return;
        }
        
        System.out.println("自动化场景列表:");
        for (AutomationScene scene : autoScenes) {
            System.out.println(scene);
        }
    }

    public void displayRooms() {
        if (currentUser == null) {
            System.out.println("请先登录");
            return;
        }
        if (household == null) {
            System.out.println("当前无家庭信息");
            return;
        }
        System.out.println("所有房间列表:");
        for (Room room : household.getRooms()) { // 依赖Household类的getRooms()方法
            System.out.println(room);
        }
    }

    /**
     * 显示能源报告
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    public void displayEnergyReportings(Date startTime, Date endTime) {
        if (currentUser == null) {
            System.out.println("请先登录");
            return;
        }
        
        System.out.println("能源消耗报告 (" + startTime + " 至 " + endTime + "):");
        double totalEnergy = 0;
        
        for (Device device : devices) {
            if (device instanceof EnergyReporting) {
                EnergyReporting energyDevice = (EnergyReporting) device;
                double deviceEnergy = energyDevice.getReport(startTime, endTime);
                System.out.println(device.getName() + ": " + deviceEnergy + " kWh");
                totalEnergy += deviceEnergy;
            }
        }
        
        System.out.println("总能耗: " + totalEnergy + " kWh");
    }
    
    /**
     * 手动触发场景
     * @param sceneId 场景ID
     * @return 是否触发成功
     */
    public boolean manualTrigSceneById(int sceneId) {
        if (currentUser == null) {
            System.out.println("请先登录");
            return false;
        }
        
        for (AutomationScene scene : autoScenes) {
            if (scene.getSceneld() == sceneId) {
                scene.manualTrig();
                LOGGER.info("手动触发场景: " + scene.getName());
                return true;
            }
        }
        
        LOGGER.warning("场景不存在: ID = " + sceneId);
        return false;
    }
    
    /**
     * 添加设备
     * @param device 设备对象
     */
    public void addDevice(Device device) {
        if (!checkAdmin()) return;
        devices.add(device);
        LOGGER.info("添加设备成功: " + device.getName());
    }
    
    /**
     * 添加自动化场景
     * @param scene 场景对象
     */
    public void addAutoScene(AutomationScene scene) {
        autoScenes.add(scene);
        LOGGER.info("添加自动化场景成功: " + scene.getName());
    }
    
    /**
     * 检查当前用户是否为管理员
     * @return 是否为管理员
     */
    private boolean checkAdmin() {
        if (currentUser == null) {
            System.out.println("请先登录");
            return false;
        }
        if (!currentUser.isAdmin()) {
            System.out.println("权限不足: 仅管理员可执行此操作");
            return false;
        }
        return true;
    }
    
    /**
     * 获取当前用户
     * @return 当前用户
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * 获取家庭信息
     * @return 家庭对象
     */
    public Household getHousehold() {
        return household;
    }
    
    /**
     * 设置家庭信息
     * @param household 家庭对象
     */
    public void setHousehold(Household household) {
        this.household = household;
        // 确保household与system关联
        if (household != null) {
            household.setSystem(this);
            // 将household中已有的用户同步到系统
            for (User user : household.getUsers()) {
                if (!users.contains(user)) {
                    users.add(user);
                }
            }
        }
    }
    
    /**
     * 获取所有设备
     * @return 设备列表
     */
    public List<Device> getAllDevices() {
        return new ArrayList<>(devices);
    }
    

    /**
     * 使用指定格式导出运行日志
     * @param formatter 日志格式化器
     * @return 格式化后的日志字符串
     */
    public String exportLogsWithFormatter(RunningLogFormatter formatter) {
        if (household == null) {
            return "错误：系统中没有设置家庭信息";
        }
        return formatter.format(household);
    }

    /**
     * 获取所有可用的日志格式化器
     * @return 格式化器数组
     */
    public RunningLogFormatter[] getAvailableLogFormatters() {
        return new RunningLogFormatter[] {
                new JsonRunningLogFormatter(),
                new HtmlRunningLogFormatter(),
                new XmlRunningLogFormatter()
        };
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    /**
     * 将用户添加到系统
     * @param user 用户对象
     */
    public void addUserToSystem(User user) {
        if (user != null) {
            boolean userExists = false;
            for (User existingUser : users) {
                if (existingUser.getUserId() == user.getUserId()) {
                    userExists = true;
                    break;
                }
            }
            if (!userExists) {
                users.add(user);
            }

        }
    }


    public void autoLoginAdmin() {
        try {
            // 查找管理员用户
            for (User user : users) {
                if (user.isAdmin()) {
                    currentUser = user;
                    System.out.println("自动登录成功: " + user.getUserName());
                    break;
                }
            }

            // 如果没有找到管理员，创建默认管理员
            if (currentUser == null && !users.isEmpty()) {
                currentUser = users.get(0);
                System.out.println("使用第一个用户自动登录: " + currentUser.getUserName());
            }
        } catch (Exception e) {
            System.out.println("自动登录失败: " + e.getMessage());
        }
    }
}
