package zss.vip.myspringmvc;

import zss.vip.myspringmvc.annotation.MyAutowired;
import zss.vip.myspringmvc.annotation.MyController;
import zss.vip.myspringmvc.annotation.MyRepository;
import zss.vip.myspringmvc.annotation.MyService;
import zss.vip.myspringmvc.utils.ReflectUtil;
import zss.vip.myspringmvc.xmlparser.XMLConfigurationParser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

/**
 * Created with IntelliJ IDEA
 * Created By zhangshanshan
 * Date: 2019/4/22
 * Time: 17:13
 */
public class DispatcherServlet extends HttpServlet {
    private String contextConfigLocation = "applicationContext.xml";

    // 创建线程安全集合
    private List<Class<?>> classList = Collections.synchronizedList(new ArrayList<Class<?>>());
    private Map<String, Object> contextContainer = Collections.synchronizedMap(new HashMap<>());

    @Override
    public void init() throws ServletException {
        try {
            String basePackage = XMLConfigurationParser.ReadXMLBasePackage(contextConfigLocation);
            // 扫描所有带注解的类存放到集合中
            scanBasePackageAnnotationClass(basePackage);
            // 将所有扫描的类完成实例映射，【别名作为key,实例作为】
            doIOC();
            doDI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doDI() throws Exception {
        if (classList.size() < 0) {
            return;
        }
        for (int i = 0; i < classList.size(); i++) {
            Class<?> aClass = classList.get(i);
            // 获取到类别名
            String annotationAlias = ReflectUtil.getAnnotationAlias(aClass);
            Object annotationName = contextContainer.get(annotationAlias);
            Field[] declaredFields = aClass.getDeclaredFields(); // 获取当前类所有的声明
            if (declaredFields.length > 0) {
                for (Field field : declaredFields) {
                    MyAutowired annotation = field.getAnnotation(MyAutowired.class);
                    Object objectName = null;
                    if (null == annotation) {
                        continue;
                    } else {
                        if (!"".equals(annotation.value())) {
                            // 根据别名装配
                            String value = annotation.value();
                            objectName = contextContainer.get(value);
                        } else {
                            // 根据类型状态
                            // 1、获取字段的类型
                            Class<?> type = field.getType();
                            // 2、获取到容器中所有装配的实例集合
                            Collection<Object> values = contextContainer.values();
                            Iterator<Object> iterator = values.iterator();
                            while (iterator.hasNext()) {
                                // 3、判断字段类型是否是子类型还是同类型
                                Object obj = iterator.next();
                                if (type.isAssignableFrom(obj.getClass())) {
                                    objectName = obj;
                                    break;
                                }
                            }
                        }
                    }
                    field.setAccessible(true);
                    field.set(annotationName,objectName);
                }
            }
        }

    }

    private void doIOC() throws Exception {
        if (classList.size() < 0) {
            return;
        }
        for (int i = 0; i < classList.size(); i++) {
            Class<?> aClass = classList.get(i);
            // 获取到别名
            String annotationAlias = ReflectUtil.getAnnotationAlias(aClass);
            contextContainer.put(annotationAlias, aClass.newInstance());
        }
    }

    private void scanBasePackageAnnotationClass(String basePackage) throws Exception {
        URL resource = this.getClass().getClassLoader().getResource(basePackage.replace(".", "/"));
        File file = new File(resource.toURI());
        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File childFile) {
                if (childFile.isDirectory()) {
                    try {
                        scanBasePackageAnnotationClass(basePackage + "." + childFile.getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (childFile.getName().endsWith(".class")) {
                        String className = childFile.getName().replace(".class", "");
                        try {
                            // Class.forName方法底层实现使用loadClass，loadClass的提高开发效率
                            Class<?> aClass = this.getClass().getClassLoader().loadClass(basePackage + "." + className);
                            // 将需要的类保存到集合中
                            if (aClass.isAnnotationPresent(MyRepository.class)
                                    || aClass.isAnnotationPresent(MyController.class)
                                    || aClass.isAnnotationPresent(MyService.class)) {
                                classList.add(aClass);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return false;
            }
        });
    }

    public static void main(String[] args) throws ServletException {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.init();

    }
}
