package cn.edu.nwpu.homesphere;

/**
 * 运行日志类，记录设备的运行状态和事件
 */
public class RunningLog {
    private java.util.Date dateTime;
    private String event;
    private Type type;
    private String note;

    public enum Type {
        INFO(0), WARN(1), ERROR(2);
        private final int code;
        Type(int code) { this.code = code; }

        public static Type fromCode(int code) {
            for (Type t : Type.values()) {
                if (t.code == code) {
                    return t;
                }
            }
            throw new IllegalArgumentException("无效的日志类型编码: " + code);
        }
    }

    /**
     * 构造函数
     * @param dateTime 日志时间
     * @param event 事件描述
     * @param type 事件类型（0: info, 1: warning, 2: error）
     * @param note 备注信息
     */
    public RunningLog(java.util.Date dateTime, String event, int type, String note) {
        this.dateTime = dateTime;
        this.event = event;
        this.type = Type.fromCode(type); // 转换为枚举
        this.note = note;
    }

    public RunningLog(java.util.Date dateTime, String event, Type type, String note) {
        this.dateTime = dateTime;
        this.event = event;
        this.type = type;
        this.note = note;
    }

    /**
     * 获取日志时间
     * @return 日志时间
     */
    public java.util.Date getDateTime() {
        return dateTime;
    }
    
    /**
     * 获取事件描述
     * @return 事件描述
     */
    public String getEvent() {
        return event;
    }
    
    /**
     * 获取事件类型
     * @return 事件类型
     */
    public Type getType() {
        return type;
    }
    
    /**
     * 获取备注信息
     * @return 备注信息
     */
    public String getNote() {
        return note;
    }



    /**
     * 返回运行日志对象的字符串表示
     * @return 运行日志信息字符串
     */
    @Override
    public String toString() {
        return "RunningLog{dateTime=" + dateTime + ", event='" + event + "', type=" + type + ", note='" + note + "'}";
    }
}