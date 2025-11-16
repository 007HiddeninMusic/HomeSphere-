package cn.edu.nwpu.homesphere;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动化场景类，代表一系列设备动作的组合
 */
public class AutomationScene {
    private int sceneId;
    private String name;
    private String description;
    private List<DeviceAction> actions;
    
    /**
     * 构造函数
     * @param sceneId 场景ID
     * @param name 场景名称
     * @param description 场景描述
     */
    public AutomationScene(int sceneId, String name, String description) {
        this.sceneId = sceneId;
        this.name = name;
        this.description = description;
        this.actions = new ArrayList<>();
    }
    
    /**
     * 添加设备动作
     * @param action 设备动作对象
     */
    public void addAction(DeviceAction action) {
        actions.add(action);
    }
    
    /**
     * 移除设备动作
     * @param action 设备动作对象
     * @return 是否移除成功
     */
    public boolean removeAction(DeviceAction action) {
        return actions.remove(action);
    }
    
    /**
     * 获取所有动作
     * @return 动作列表
     */
    public List<DeviceAction> getActions() {
        return new ArrayList<>(actions);
    }
    
    /**
     * 手动触发场景
     */
    public void manualTrig() {
        System.out.println("触发场景: " + name);
        for (DeviceAction action : actions) {
            action.execute();
        }
    }

    /**
     * 获取场景ID
     * @return 场景ID
     */
    public int getSceneld() {
        return sceneId;
    }

    /**
     * 获取场景名称
     * @return 场景名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 获取场景描述
     * @return 场景描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 设置场景名称
     * @param name 场景名称
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * 设置场景描述
     * @param description 场景描述
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * 返回自动化场景对象的字符串表示
     * @return 自动化场景信息字符串
     */
    @Override
    public String toString() {
        return "AutomationScene{sceneId=" + sceneId + ", name='" + name + "', description='" + description + "', actionCount=" + actions.size() + "}";
    }
}