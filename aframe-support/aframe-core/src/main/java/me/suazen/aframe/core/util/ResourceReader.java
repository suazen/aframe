package me.suazen.aframe.core.util;

import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.core.exception.ResourceException;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.UrlResource;

import java.net.URL;
import java.util.Properties;

/**
 * @author sujizhen
 * @date 2023-06-13
 **/
@Slf4j
public class ResourceReader {
    public static URL readFile(String path){
        return readFile(ResourceReader.class.getClassLoader(),path);
    }

    public static URL readFile(ClassLoader loader,String path){
        URL url = loader.getResource(path);
        if (url == null){
            throw new ResourceException("Can not find file in path {%s}",path);
        }
        log.info("file match in path {}",url.getPath());
        return url;
    }

    public static Properties readYaml(String path){
        return readYaml(ResourceReader.class.getClassLoader(),path);
    }

    public static Properties readYaml(ClassLoader loader,String path){
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(new UrlResource(readFile(loader,path)));
        return factoryBean.getObject();
    }
}
