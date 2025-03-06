package t.coding.fun.utils;

/**
 * @author T
 * @description 环境工具类
 * @date 2025年03月06日 20:54
 */
public class EnvUtils {

    /**
     * @Description: 获取环境变量
     * @Param:
     * @return:
     * @Author: tangzhaofeng
     * @Date: 3/5/2025
     */
    public static String getEnv(String key) {
        String value = System.getenv(key);
        if (null == value || value.isEmpty()) {
            throw new RuntimeException("value is null");
        }
        return value;
    }

}
