package luna.vip.myspringmvc.utils;

import luna.vip.myspringmvc.DispatcherServlet;
import luna.vip.myspringmvc.annotation.MyController;
import luna.vip.myspringmvc.annotation.MyRepository;
import luna.vip.myspringmvc.annotation.MyService;

/**
 * @author luna
 * @date 2019-04-24
 */
public class ReflectUtil {

    /**
     * 根据类名获取首字母小写的别名
     *
     * @param className
     * @return
     */
    private static String getLowerAlias(Class<?> className) {
        String simpleName = className.getSimpleName();
        String first = simpleName.substring(0, 1).toLowerCase();
        String end = simpleName.substring(1);
        return first.concat(end);
    }

    /**
     * 获取类别名（若注解有别名，则取）
     *
     * @param className
     * @return
     */
    public static String getAnnotationAlias(Class<?> className) {
        String simpleName = getLowerAlias(className);
        MyController myController = className.getAnnotation(MyController.class);
        MyRepository myRepository = className.getAnnotation(MyRepository.class);
        MyService myService = className.getAnnotation(MyService.class);

        if (null != myController && !"".equals(myController.value())) {
            simpleName = myController.value();
        } else if (null != myRepository && !"".equals(myRepository.value())) {
            simpleName = myRepository.value();
        } else if (null != myService && !"".equals(myService.value())) {
            simpleName = myService.value();
        }
        return simpleName;
    }

    /**
     * 处理请求路径
     * @param url
     * @return
     */
    public static String handleUrl(String url) {
        return url.startsWith("/") ? url : "/".concat(url);
    }


    public static void main(String[] args) {
        System.out.println(ReflectUtil.getLowerAlias(DispatcherServlet.class));
    }


}
