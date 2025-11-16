package cn.edu.nwpu.homesphere;

/**
 * 用户类，代表HomeSphere系统中的用户
 */
public class User {
    private int userId;
    private String loginName;
    private String loginPassword;
    private String username;
    private String email;
    private boolean isAdmin;
    
    /**
     * 构造函数
     * @param userId 用户ID
     * @param loginName 登录名
     * @param loginPassword 登录密码
     * @param username 用户姓名
     * @param email 电子邮件
     * @param isAdmin 是否为管理员
     */
    public User(int userId, String loginName, String loginPassword, String username, String email, boolean isAdmin) {
        this.userId = userId;
        this.loginName = loginName;
        this.loginPassword = loginPassword;
        this.username = username;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    public User(int userId, String username, String password, String realName, String email) {
        this(userId, username, password, realName, email, false); // 默认非管理员
    }

    /**
     * 获取用户ID
     * @return 用户ID
     */
    public int getUserId() {
        return userId;
    }



    /**
     * 获取登录名
     * @return 登录名
     */
    public String getLoginName() {
        return loginName;
    }
    
    /**
     * 获取登录密码
     * @return 登录密码
     */
    public String getLoginPassword() {
        return loginPassword;
    }
    
    /**
     * 获取用户姓名
     * @return 用户姓名
     */
    public String getUserName() {
        return username;
    }

    
    /**
     * 获取电子邮件
     * @return 电子邮件
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * 是否为管理员
     * @return 是否为管理员
     */
    public boolean isAdmin() {
        return isAdmin;
    }
    
    /**
     * 设置管理员权限
     * @param admin 是否为管理员
     */
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
    
    /**
     * 比较两个用户对象是否相等
     * @param obj 要比较的对象
     * @return 如果ID相等则返回true，否则返回false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return userId == user.userId;
    }
    
    /**
     * 返回用户对象的字符串表示
     * @return 用户信息字符串
     */
    @Override
    public String toString() {
        return "User{userId=" + userId + ", loginName='" + loginName + "', username='" + username + "', email='" + email + "', isAdmin=" + isAdmin + "}";
    }
}