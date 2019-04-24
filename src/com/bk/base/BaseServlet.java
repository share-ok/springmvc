package com.bk.base;

import com.bk.util.BeanUtils;
import com.bk.util.ClassUtils;
import com.bk.util.StringUtils;
import com.ruanmou.vip.orm.common.ArrayUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 软谋教育Java VIP课程
 * <pre>
 *    SpringMVC框架的前端控制器(控制请求转发和重定向，数据流向)
 * </pre>
 * 所有servlet类的父类
 * @author gerry
 * @date 2018-06-28
 */
public abstract class BaseServlet extends HttpServlet {
    /**
     * 重写父类中service方法
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 定义保存参数类型对应值的集合
        List<Object> parameterList = null;
        // 定义存储调用方法返回的url路径
        Object url = null;
        // 获取当前请求的方法名称
        String methodName = request.getParameter("param");

        try {

            // 定义方法对象句柄
            Method method = null;
            // 根据方法名称查找controller对象中使用方法
            // 1、获取当前调用类中所有定义的方法
            Method[] methods = this.getClass().getDeclaredMethods();
            if (ArrayUtils.isNotEmpty(methods)) {
                for (Method method1 : methods) {
                    if (methodName.equals(method1.getName())) {
                        // 找到调用方法名称相同的方法对象
                        method = method1;
                        break;
                    }
                }
            }

            if (method != null) {
                // 获取到方法的参数类型列表
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (ArrayUtils.isNotEmpty(parameterTypes)) {
                    parameterList = new ArrayList<Object>();

                    for (Class<?> cl : parameterTypes) {
                        System.out.println(cl);
                        if (!cl.isInterface()) {
                            if (ClassUtils.isSystemClass(cl)) {
                                // 定义处理系统类的方法
                                parameterList.add(BeanUtils.convertHttpServletRequestToSystemClassBean(request ,cl));
                            } else {
                                // 处理自定义方法
                                parameterList.add(BeanUtils.convertHttpServletRequestToJavaBean(request, cl));
                            }
                        } else {
                            // 添加到javaweb中常用HttpServlet中API对象
                            if (cl == HttpServletRequest.class) {
                                // 注入request对象
                                parameterList.add(request);
                            } else if (cl == HttpServletResponse.class) {
                                // 注入reqpsone对象
                                parameterList.add(response);
                            } else if (cl == HttpSession.class) {
                                // 注入session对象
                                parameterList.add(request.getSession());
                            } else if (cl == ServletContext.class) {
                                // 注入applicaiton对象
                                parameterList.add(this.getServletContext());
                            }
                        }
                    }
                }

                if (null != parameterList && parameterList.size() > 0) {
                    // 反射调用方法
                    url = method.invoke(this, parameterList.toArray());
                } else {
                    // 反射调用方法
                    url = method.invoke(this);
                }

                if (null != url) {
                    String urlString = url.toString();
                    if (urlString.startsWith("redirect:")) {
                        // 重定向
                        response.sendRedirect(request.getContextPath()+ StringUtils.trimByPrefix(urlString,"redirect:"));
                    } else if (urlString.startsWith("forward:")) {
                        // 转发
                        request.getRequestDispatcher(StringUtils.trimByPrefix(urlString,"forward:")).forward(request, response);
                    } else {
                        // 默认为转发
                        request.getRequestDispatcher(urlString).forward(request, response);
                    }
                } else {
                    System.out.println("访问的"+url+"路径不存在，请检查。");
                }
            } else {
                System.out.println("没有"+this.getClass().getSimpleName()+"类中找到调用的"+methodName+"的方法");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
