package com.abc.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 元注解
 * runtime：运行期有效
 * class:字节码期有效
 * source:源代码期间有效
 * field:作用在属性上
 * type:作用在类上
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Autowired {

//    Class value();//等于注解中的一个属性
}
