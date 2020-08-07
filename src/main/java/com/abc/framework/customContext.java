package com.abc.framework;

import com.abc.annotation.Autowired;
import com.abc.annotation.Component;
import com.abc.exception.UnknowException;
import com.abc.test.test02;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 自定义IOC框架
 * 做事的类
 * IOC思想：把自己亲自创建类这件事交给第三方（框架）做
 */
public class customContext {

    private final static HashMap<Class, List<Class>> map = new HashMap<Class, List<Class>>();

    //beanmap用来放初始化好的对象
    public static final  HashMap<String, Object> beansMap = new HashMap<String, Object>();

    private static final List<Class> beansList = new ArrayList<Class>();

    private String packages;

    public customContext() {
    }

    public customContext(String packages) {
        this.packages = packages;
        try {
            init(packages);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Object getBean(String id){
        return beansMap.get(id);
    }

    /**
     * 属性注入
     */
    private Object inject(Class bossClass) {
        try {
            //通过反射给water中的变量赋值
            Field[] fields = bossClass.getDeclaredFields();
//            T boss = bossClass.newInstance();
            //保证单例------------------------
            String id1 = getId(bossClass);
            Object boss = beansMap.get(id1);
            if(boss==null){
                beansMap.put(id1,boss);
            }
            //----------------------------
            //找到water
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                //允许暴力反射
                field.setAccessible(true);
                //获取属性上是否有注解
                Autowired annotation = field.getAnnotation(Autowired.class);
                if(annotation!=null){
                    Class value = field.getType();
                    //判断是否是个接口
                    if(value.isInterface()){
                        //遍历map集合
                        List<Class> classList = map.get(value);
                        if (classList == null) {
                            throw new NullPointerException();
                        }
                        //一个接口如果只要一个实现类，则直接注入
                        if (classList != null && classList.size() == 1) {
                            Class sClass = classList.get(0);
                            //解决单例问题
                            String id = getId(sClass);
                            Object bean = beansMap.get(id);
                            if (bean == null) {
                                Object instance = sClass.newInstance();
                                field.set(boss,instance);
                                beansMap.put(id,instance);
                            } else {
                                field.set(boss,bean);
                            }

                        }else{
                            boolean isAutowiredFail = true;
                            for (int j = 0; j < classList.size(); j++) {
                                Class aClass = classList.get(j);
                                String fieldName = field.getName();
                                String substring = fieldName.substring(1, fieldName.length());
                                String upperCase = fieldName.toUpperCase();
                                char fristChar = upperCase.charAt(0);
                                String className = fristChar+substring;
                                //如果集合里的名称和属性一致，则注入
                                if (aClass.getName().endsWith(className)){
                                    String id = getId(aClass);
                                    Object bean = beansMap.get(id);
                                    if (bean == null) {
                                        Object instance = aClass.newInstance();
                                        field.set(boss,instance);
                                        beansMap.put(id,instance);
                                    } else {
                                        field.set(boss,bean);
                                    }
//                                    field.set(leader,sonClass.newInstance());
                                    isAutowiredFail = false;
                                }
                            }
                            if (isAutowiredFail) {
                                throw new UnknowException();
                            }
                        }
                    }else{
                        String id = getId(value);
                        Object bean = beansMap.get(id);
                        if (bean == null) {
                            Object instance = value.newInstance();
                            field.set(boss,instance);
                            beansMap.put(id,instance);
                        } else {
                            field.set(boss,bean);
                        }
                    }
                }
            }
            return boss;
        }catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }catch (UnknowException e) {
        }
        return null;
    }

    /**
     *
     */
    private void init(String packages) throws ClassNotFoundException {
//        String packages="com.abc";
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
                    beansList.add(aClass);
                    //所有Component标注的类的接口，以及接口对应的所有实现类 （此处逻辑需要在依赖注入之前执行）
                    Class<?>[] interfaces = aClass.getInterfaces();
                    for (int j=0; j<interfaces.length; j++) {
                        Class<?> anInterface = interfaces[j];
                        List<Class> sonList = map.get(anInterface);
                        if (sonList == null) {
                            //第一次找到这个father，就保存到map中
                            ArrayList<Class> sonClassList = new ArrayList<Class>();
                            sonClassList.add(aClass);
                            map.put(anInterface,sonClassList);
                        } else {
                            //第二次找到，就将fater对应的son集合获取到，并且将新的son加入集合中
                            sonList.add(aClass);
                        }
                    }
                }
                System.out.println(map);
                //在有component注解的类上找他们的父类或者接口
            }
        }
        for (int i = 0,size=beansList.size(); i < size; i++) {
            Class aClass = beansList.get(i);
            Object obj = inject(aClass);
            String id = getId(aClass);
            beansMap.put(id,obj);
        }
    }

    /**
     *
     */
    private String getId(Class aClass){
        String tId = aClass.getName();
        //获取最后一个“.”的下标
        int lastIndex = tId.lastIndexOf(".");
        tId = tId.substring(lastIndex+1, tId.length());
        char firstChar = tId.toLowerCase().charAt(0);
        String substring = tId.substring(1, tId.length());
        String id = firstChar+substring;
        return id;
    }

}
