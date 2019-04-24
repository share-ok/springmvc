package zss.vip.myspringmvc.utils;

import zss.vip.myspringmvc.DispatcherServlet;
import zss.vip.myspringmvc.annotation.MyController;
import zss.vip.myspringmvc.annotation.MyRepository;
import zss.vip.myspringmvc.annotation.MyService;

/**
 * Created with IntelliJ IDEA
 * Created By zhangshanshan
 * Date: 2019/4/23
 * Time: 16:46
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


    public static void main(String[] args) {
        System.out.println(ReflectUtil.getLowerAlias(DispatcherServlet.class));
    }


}
