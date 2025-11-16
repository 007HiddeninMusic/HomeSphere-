package cn.edu.nwpu.homesphere;

/**
 * 自定义用户异常类，当用户登录失败时抛出
 */
public class InvalidUserException extends Exception {
    public InvalidUserException() {
        super("用户名或密码错误");
    }

    public InvalidUserException(String message) {
        super(message);
    }

    public InvalidUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
