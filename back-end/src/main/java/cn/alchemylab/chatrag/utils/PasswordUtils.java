package cn.alchemylab.chatrag.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    // 加密工作因子(默认是10，4-31之间，值越大越安全但越慢)
    private static final int WORKLOAD = 10;

    /**
     * 加密原始密码
     * @param rawPassword 原始密码
     * @return 加密后的哈希密码
     */
    public static String encode(String rawPassword) {
        // 自动生成盐并哈希密码
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(WORKLOAD));
    }


    /**
     * 验证密码是否匹配
     * @param rawPassword 原始密码(用户输入)
     * @param encodedPassword 已加密的密码(数据库存储)
     * @return 匹配返回true，否则false
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null || !encodedPassword.startsWith("$2a$")) {
            return false;
        }
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }


}