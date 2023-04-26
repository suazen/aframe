package me.suazen.aframe.framework.core.restful;

import cn.hutool.core.util.StrUtil;
import me.suazen.aframe.framework.core.restful.annotation.Get;
import me.suazen.aframe.framework.core.restful.annotation.Post;
import me.suazen.aframe.framework.core.restful.annotation.Restful;
import me.suazen.aframe.framework.core.restful.annotation.RestfulScan;
import me.suazen.aframe.framework.core.restful.domain.RestMapping;
import me.suazen.aframe.framework.core.restful.exception.RestMappingConflictException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RestfulRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {
    private static final Map<String, RestMapping> getMap = new HashMap<>();
    private static final Map<String, RestMapping> postMap = new HashMap<>();

    /**
     * 资源加载器
     */
    private ResourceLoader resourceLoader;
    /**
     * 环境
     */
    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        // 创建scanner
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(resourceLoader);

        // 设置扫描器scanner扫描的过滤条件
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(Restful.class);
        scanner.addIncludeFilter(annotationTypeFilter);

        // 获取指定要扫描的basePackages
        Set<String> basePackages = getBasePackages(metadata);

        // 遍历每一个basePackages
        for (String basePackage : basePackages) {
            // 通过scanner获取basePackage下的候选类(有标@Restful注解的类)
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            // 遍历每一个候选类，如果符合条件就把他们注册到容器
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    // verify annotated class is an interface
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;

                    Class<?> beanClass = getBeanClass(beanDefinition);
                    Restful restful = beanClass.getAnnotation(Restful.class);
                    Method[] methods = beanClass.getDeclaredMethods();
                    for (Method method : methods) {
                        if (method.isAnnotationPresent(Get.class)) {
                            Get get = method.getAnnotation(Get.class);
                            String path = concatPath(restful.value(),get.value(),get.path());
                            if (getMap.containsKey(path)){
                                throw new RestMappingConflictException(String.format("接口路径已存在:%s %s",method,path));
                            }
                            getMap.put(path,
                                    new RestMapping(beanClass, method, get.contentType().toString(Charset.defaultCharset()), get.returnType()));
                        } else if (method.isAnnotationPresent(Post.class)) {
                            Post post = method.getAnnotation(Post.class);
                            String path = concatPath(restful.value(),post.value(),post.path());
                            if (postMap.containsKey(path)){
                                throw new RestMappingConflictException(String.format("接口路径已存在:%s %s",method,path));
                            }
                            postMap.put(path,
                                    new RestMapping(beanClass, method, post.contentType().toString(Charset.defaultCharset()), post.returnType()));
                        }
                    }
                }
            }
        }
    }

    private String concatPath(String... paths){
        StringBuilder builder = new StringBuilder();
        for (String path:paths){
            if (StrUtil.isBlank(path)){
                continue;
            }
            builder.append(path.startsWith("/")?"":"/")
                    .append(path.endsWith("/")?path.substring(0,path.length()-1):path);
        }
        return builder.toString();
    }

    private Class<?> getBeanClass(BeanDefinition beanDefinition) {
        try {
            return Class.forName(beanDefinition.getBeanClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建扫描器
     */
    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }

    /**
     * 获取base packages
     */
    protected static Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        // 获取到@EnableSimpleRpcClients注解所有属性
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(RestfulScan.class.getCanonicalName());
        Set<String> basePackages = new HashSet<>();
        
        if (attributes != null){
            // basePackages 属性是否有配置值，如果有则添加
            for (String pkg : (String[]) attributes.get("basePackages")) {
                if (pkg != null && !"".equals(pkg)) {
                    basePackages.add(pkg);
                }
            }
        }

        // 如果上面两步都没有获取到basePackages，那么这里就默认使用当前项目启动类所在的包为basePackages
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }

        return basePackages;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public static RestMapping getRestMapping(RequestMethod method, String url) {
        if (method == RequestMethod.GET) {
            return getMap.get(url);
        } else if (method == RequestMethod.POST) {
            return postMap.get(url);
        }
        return null;
    }
}
