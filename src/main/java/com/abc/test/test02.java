package com.abc.test;

import com.abc.annotation.Component;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 找标注有@Component注解的类
 */
public class test02 {

    private final static HashMap<Class, List<Class>> map = new HashMap<Class, List<Class>>();

    public static void main(String[] args) throws ClassNotFoundException {
        String packages="com.abc";
        URL url = test02.class.getResource("/");
        String resourcePath = url.getPath();
        String replaceAll = packages.replaceAll("\\.", "/");
        String path = resourcePath+replaceAll;

        //通过此类获取当前路径下有哪些文件
        File file = new File(path);
        File[] fil = file.listFiles();
        for (File filel: fil) {
            String name = filel.getName();
            //获取Java源文件
            if (filel.isFile() && name.endsWith(".class")) {
                String[] split = name.split("\\.");
                //找有Component注解的类
                Class<?> aClass = Class.forName(packages + "." + split[0]);
                Component annotation = aClass.getAnnotation(Component.class);
                if (annotation != null) {
                    Class<?>[] interfaces = aClass.getInterfaces();
                    for (int j=0; j<interfaces.length; j++) {
                        Class<?> anInterface = interfaces[j];

                        List<Class> sonList = map.get(anInterface);
                        if (sonList == null) {
                            //第一次找到这个父类，就保存到map中
                            ArrayList<Class> sonClassList = new ArrayList<Class>();
                            sonClassList.add(aClass);
                            map.put(anInterface,sonClassList);
                        } else {
                            //第二次找到，就将父类对应的s子类集合获取，并且将新的s子类加入集合
                            sonList.add(aClass);
                        }
                    }
                }
                System.out.println(map);
                //在有component注解的类上找他们的父类或者接口
            }
        }
    }
}
