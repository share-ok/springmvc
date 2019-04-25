package luna.vip.myspringmvc;

import com.bk.util.StringUtils;
import luna.vip.myspringmvc.annotation.*;
import luna.vip.myspringmvc.utils.BeanUtils;
import luna.vip.myspringmvc.utils.ClassUtils;
import luna.vip.myspringmvc.utils.ReflectUtil;
import luna.vip.myspringmvc.xmlparser.XMLConfigurationParser;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;

/**
 * IOC和DI核心处理
 *
 * @author luna
 * @date 2019-04-24
 */
public class DispatcherServlet extends HttpServlet {
    // private String contextConfigLocation ="applicationContext.xml";
    private String contextConfigLocation;
    // 创建线程安全集合
    private List<Class<?>> classList = Collections.synchronizedList(new ArrayList<Class<?>>());
    private Map<String, Object> contextContainer = Collections.synchronizedMap(new HashMap<>());
    // 存储访问路径与方法对应实例映射关系的集合
    private Map<String, Object> urlMappingContext = Collections.synchronizedMap(new HashMap<>());

    public DispatcherServlet() {
        System.out.println("servlet 实例");
    }

    public void setContextConfigLocation(String contextConfigLocation) {
        this.contextConfigLocation = contextConfigLocation.replace("classpath:", "");
    }

    @Override
    public void init() throws ServletException {
        setContextConfigLocation(getInitParameter("contextConfigLocation"));
        try {
            // 获取包路径文件
            String basePackage = XMLConfigurationParser.ReadXMLBasePackage(contextConfigLocation);
            // 扫描所有带注解的类存放到集合中
            scanBasePackageAnnotationClass(basePackage);
            // 将所有扫描的类完成实例映射，【别名作为key,实例作为value】
            doIOC();
            doDI();
            urlMappingToMethod();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理客户端请求的业务逻辑
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取到访问路径
        String requestURI = request.getRequestURI();
        // 调用方法后返回的路径
        Object url = null;
        List<Object> parameterList = new ArrayList<>();
        Method method = (Method) urlMappingContext.get(requestURI);
        try {
            if (null != method) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (null != parameterTypes) {
                    for (Class<?> parameterType : parameterTypes) {
                        if (parameterType.isInstance(parameterType)) {
                            if (ClassUtils.isSystemClass(parameterType)) {
                                // 定义处理系统类的方法
                                parameterList.add(BeanUtils.convertHttpServletRequestToSystemClassBean(request, parameterType));
                            } else {
                                // 处理自定义类型方法
                                parameterList.add(BeanUtils.convertHttpServletRequestToJavaBean(request, parameterType));
                            }
                        } else {
                            // 添加到javaWeb中常用HttpServlet中API对象
                            if (parameterType == HttpServletRequest.class) {
                                // 注入request对象
                                parameterList.add(request);
                            } else if (parameterType == HttpServletResponse.class) {
                                // 注入response对象
                                parameterList.add(response);
                            } else if (parameterType == HttpSession.class) {
                                // 注入session对象
                                parameterList.add(request.getSession());
                            } else if (parameterType == ServletContext.class) {
                                // 注入application对象
                                parameterList.add(this.getServletContext());
                            }
                        }
                    }
                    // 确定方法对应实例对象Class对象
                    Class<?> declaringClass = method.getDeclaringClass();
                    // 通过class对象获取容器中实例化好的对象
                    Object instance = getContextBean(ReflectUtil.getAnnotationAlias(declaringClass));
                    if (null != parameterList && parameterList.size() > 0) {
                        // 反射调用方法
                        url = method.invoke(instance, parameterList.toArray());
                    } else {
                        // 反射调用方法
                        url = method.invoke(instance);
                    }
                    if (null != url) {
                        String urlString = url.toString();
                        if (urlString.startsWith("redirect:")) {
                            // 重定向
                            response.sendRedirect(request.getContextPath() + StringUtils.trimByPrefix(urlString, "redirect:"));
                        } else if (urlString.startsWith("forward:")) {
                            // 转发
                            request.getRequestDispatcher(StringUtils.trimByPrefix(urlString, "forward:")).forward(request, response);
                        } else {
                            // 默认为转发
                            request.getRequestDispatcher(urlString).forward(request, response);
                        }
                    } else {
                        System.out.println("访问的" + url + "路径不存在，请检查。");
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 完成访问路径与对应方法的映射装配
     */
    private void urlMappingToMethod() {
        if (classList.size() <= 0) {
            return;
        }

        for (Class<?> aClass : classList) {
            // 获取MyController注解类
            if (aClass.isAnnotationPresent(MyController.class)) {
                // 判断类上是否有MyRequestMapping注解
                if (aClass.isAnnotationPresent(MyRequestMapping.class)) {
                    MyRequestMapping myRequestMapping = aClass.getAnnotation(MyRequestMapping.class);
                    String baseUrl = "";
                    if (!"".equals(myRequestMapping.value())) {
                        baseUrl = ReflectUtil.handleUrl(myRequestMapping.value());
                    }
                    // 获取当前类中所有带有注解的方法的方法，并是本类中public修饰
                    Method[] methods = aClass.getDeclaredMethods();
                    for (Method method : methods) {
                        if (method.getModifiers() == Modifier.PUBLIC && method.isAnnotationPresent(MyRequestMapping.class)) {
                            MyRequestMapping methodMapping = method.getAnnotation(MyRequestMapping.class);
                            String methodUrl = ReflectUtil.handleUrl(methodMapping.value());
                            urlMappingContext.put(baseUrl.concat(methodUrl), method);
                        }
                    }
                }
            }
        }
    }

    /**
     * 实现DI
     *
     * @throws Exception
     */
    private void doDI() throws Exception {
        if (classList.size() <= 0) {
            return;
        }
        for (int i = 0; i < classList.size(); i++) {
            Class<?> aClass = classList.get(i);
            // 获取到类别名
            String annotationAlias = ReflectUtil.getAnnotationAlias(aClass);
            // 获取当前类的的实例
            Object annotationInstance = contextContainer.get(annotationAlias);
            Field[] declaredFields = aClass.getDeclaredFields(); // 获取当前类所有的声明
            if (declaredFields.length > 0) {
                for (Field field : declaredFields) {
                    MyAutowired myAutowired = field.getAnnotation(MyAutowired.class);
                    Object objectName = null;
                    if (null == myAutowired) {
                        continue;
                    } else {
                        if (!"".equals(myAutowired.value())) {
                            // 根据别名装配
                            String value = myAutowired.value();
                            // 根据别名获取到的实例
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
                    field.set(annotationInstance, objectName);
                }
            }
        }

    }

    /**
     * IOC实现
     *
     * @throws Exception
     */
    private void doIOC() throws Exception {
        if (classList.size() <= 0) {
            return;
        }
        for (int i = 0; i < classList.size(); i++) {
            Class<?> aClass = classList.get(i);
            // 获取到别名
            String annotationAlias = ReflectUtil.getAnnotationAlias(aClass);
            // 将别名和实例放入到contextContainer map中
            contextContainer.put(annotationAlias, aClass.newInstance());
        }
    }

    /**
     * 扫描指定路径下文件，将带有注解的类放入到classList集合中
     *
     * @param basePackage
     * @throws Exception
     */
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

    /**
     * 定义根据容器key获取对应实例
     */
    public Object getContextBean(String beanName) {
        return contextContainer.get(beanName);
    }

    public static void main(String[] args) throws ServletException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.init();
        // dispatcherServlet.urlMappingToMethod();
    }
}
