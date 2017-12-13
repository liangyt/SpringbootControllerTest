package com.liangyt.controller;

import com.liangyt.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述：MockMvc测试
 *
 * @author tony
 * @创建时间 2017-12-13 11:29
 */
@RestController
@RequestMapping(value = "/mockapi")
public class MockMvcController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/get/{id}")
    public Object get(@RequestParam("name") String name,
                      @PathVariable("id") Long id) {

        logger.debug("请求的参数为：name:" + name + ";id:" + id);

        Map map = new HashMap();
        map.put("user", "张三");
        return map;
    }

    @PostMapping("/post")
    public Object post(@RequestBody User user) {
        logger.debug(user.toString());
        return user;
    }
}
