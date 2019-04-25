package luna.vip.myspringmvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注请求的url
 * Author luna
 * Date: 2019/4/25
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface MyRequestMapping {
    String value() default "";
    MethodType method() default MethodType.NONE;

    public enum MethodType {
        PUT,
        POST,
        GET,
        UPDATE,
        DELETE,
        NONE
    }
}
