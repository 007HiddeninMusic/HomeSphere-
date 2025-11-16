package cn.edu.nwpu.homesphere;

import java.util.ArrayList;
import java.util.List;

/**
 * 房间类，代表家庭中的一个房间
 */
public class Room {
    private int roomId;
    private String name;
    private double area;
    private List<Device> devices;
    
    /**
     * 构造函数
     * @param roomId 房间ID
     * @param name 房间名称
     * @param area 房间面积
     */
    public Room(int roomId, String name, double area) {
        this.roomId = roomId;
        this.name = name;
        this.area = area;
        this.devices = new ArrayList<>();
    }
    
    /**
     * 添加设备到房间
     * @param device 设备对象
     */
    public void addDevice(Device device) {
        devices.add(device);
    }
    
    /**
     * 从房间移除设备
     * @param deviceId 设备ID
     * @return 是否移除成功
     */
    public boolean removeDevice(int deviceId) {
        return devices.removeIf(device -> device.getDeviceId() == deviceId);
    }
    
    /**
     * 获取房间中的所有设备
     * @return 设备列表
     */
    public List<Device> getDevices() {
        return new ArrayList<>(devices);
    }
    
    /**
     * 获取房间ID
     * @return 房间ID
     */
    public int getRoomId() {
        return roomId;
    }
    
    /**
     * 获取房间名称
     * @return 房间名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 获取房间面积
     * @return 房间面积
     */
    public double getArea() {
        return area;
    }
    
    /**
     * 比较两个房间对象是否相等
     * @param obj 要比较的对象
     * @return 如果ID相等则返回true，否则返回false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Room room = (Room) obj;
        return roomId == room.roomId;
    }

    public String getRoomName() {
        return name; // 或者直接 return getName();
    }

    /**
     * 返回房间对象的字符串表示
     * @return 房间信息字符串
     */
    @Override
    public String toString() {
        return "Room{roomId=" + roomId + ", name='" + name + "', area=" + area + ", deviceCount=" + devices.size() + "}";
    }
}