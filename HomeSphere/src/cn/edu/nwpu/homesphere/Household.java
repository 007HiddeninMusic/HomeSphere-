package cn.edu.nwpu.homesphere;

import java.util.ArrayList;
import java.util.List;

/**
 * 家庭类，代表HomeSphere系统中的一个家庭
 */
public class Household {
    private int householdId;
    private String address;
    private User admin;
    private List<User> users;
    private List<Room> rooms;
    private List<AutomationScene> autoScenes;
    private HomeSphereSystem system;
    /**
     * 构造函数
     * @param householdId 家庭ID
     * @param address 家庭地址
     * @param admin 管理员用户
     */
    public Household(int householdId, String address, User admin) {
        this.householdId = householdId;
        this.address = address;
        this.users = new ArrayList<>();
        this.rooms = new ArrayList<>();
        this.autoScenes = new ArrayList<>();
        this.admin = admin;
        //只有当admin不为null时才添加到列表
        if (admin != null) {
            users.add(admin);
            admin.setAdmin(true);
        }
        syncSystemUsers();
    }

    public Household(int householdId, String address) {
        this(householdId, address, null);
    }




    public void setSystem(HomeSphereSystem system) {
        this.system = system;
        // 将家庭中已有的用户同步到系统
        if (system != null) {
            for (User user : users) {
                system.addUserToSystem(user);
            }
        }
        syncSystemUsers(); // 设置后立即同步用户
    }

    /**
     * 添加房间
     * @param room 房间对象
     */
    public void addRoom(Room room) {
        rooms.add(room);
    }

    /**
     * 获取所有房间
     * @return 房间列表
     */
    public List<Room> getRooms() {
        return new ArrayList<>(rooms);
    }

    /**
     * 根据房间ID移除房间
     * @param roomId 房间ID
     */
    public void removeRoom(int roomId) {
        rooms.removeIf(room -> room.getRoomId() == roomId);
    }

    /**
     * 添加用户
     * @param user 用户对象
     */
    public void addUser(User user) {
        if (user != null) {
            // 检查是否已存在相同ID的用户
            boolean userExists = false;
            for (User existingUser : users) {
                if (existingUser.getUserId() == user.getUserId()) {
                    userExists = true;
                    break;
                }
            }
            if (!userExists) {
                users.add(user);
                // 如果system存在，也将用户添加到system
                if (system != null) {
                    system.addUserToSystem(user);
                }
            }
        }
    }

    public void syncSystemUsers() {
        if (system == null) return;

        List<User> systemUsers = system.getUsers();
        // 添加系统中不在家庭用户列表的用户
        for (User user : systemUsers) {
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



    /**
     * 根据用户ID移除用户
     * @param userId 用户ID
     */
    public void removeUser(int userId) {
        users.removeIf(user -> user.getUserId() == userId);
    }

    /**
     * 获取所有用户
     * @return 用户列表
     */
    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    /**
     * 添加自动化场景
     * @param autoScene 自动化场景对象
     */
    public void addAutoScene(AutomationScene autoScene) {
        autoScenes.add(autoScene);
    }

    /**
     * 根据场景ID获取自动化场景
     * @param sceneId 场景ID
     * @return 自动化场景对象，如果不存在则返回null
     */


    /**
     * 根据场景ID移除自动化场景
     * @param sceneId 场景ID
     */
    public void removeAutoScene(int sceneId) {
        autoScenes.removeIf(scene -> scene.getSceneld() == sceneId);
    }

    /**
     * 获取所有自动化场景
     * @return 自动化场景列表
     */
    public List<AutomationScene> getAutoScenes() {
        return new ArrayList<>(autoScenes);
    }

    /**
     * 获取所有设备（遍历所有房间的设备）
     * @return 设备列表
     */
    public List<Device> listAllDevices() {
        List<Device> allDevices = new ArrayList<>();
        for (Room room : rooms) {
            allDevices.addAll(room.getDevices());
        }
        return allDevices;
    }

    /**
     * 获取家庭ID
     * @return 家庭ID
     */
    public int getHouseholdId() {
        return householdId;
    }

    /**
     * 获取家庭地址
     * @return 家庭地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 获取管理员
     * @return 管理员用户
     */
    public User getAdmin() {
        return admin;
    }

    /**
     * 设置家庭管理员
     * @param admin 管理员用户
     */
    public void setAdmin(User admin) {
        this.admin = admin;
        if (admin != null) {
            admin.setAdmin(true);
            // 确保管理员在用户列表中
            if (!users.contains(admin)) {
                users.add(admin);
            }
        }
    }

    /**
     * 返回家庭对象的字符串表示
     * @return 家庭信息字符串
     */
    @Override
    public String toString() {
        int adminId = (admin != null) ? admin.getUserId() : -1;
        return "Household{householdId=" + householdId + ", address='" + address + "', adminId=" + adminId + ", userCount=" + users.size() + ", roomCount=" + rooms.size() + "}";
    }
}