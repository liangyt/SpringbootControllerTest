package com.liangyt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 描述：启动
 *
 * @author tony
 * @创建时间 2017-12-13 11:27
 */
@SpringBootApplication
@EnableSwagger2 // 启用 swagger
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
