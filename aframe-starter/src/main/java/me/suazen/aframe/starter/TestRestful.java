package me.suazen.aframe.starter;

import me.suazen.aframe.framework.core.restful.annotation.Get;
import me.suazen.aframe.framework.core.restful.annotation.Restful;

@Restful("test")
public interface TestRestful {
    @Get(sql="test")
    void test();
}
