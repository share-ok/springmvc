package luna.vip.myspringmvc.utils;

/**
 * @author luna
 * @date 2019-04-24
 */
public class ClassUtils {
    /**
     * 判断一个类是否是系统类
     * @param cl
     * @return
     */
    public static boolean isSystemClass(Class<?> cl) {
        System.out.println(cl.getClassLoader());
        return cl != null && cl.getClassLoader() == null;
    }
}
