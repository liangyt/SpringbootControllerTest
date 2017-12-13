package com.liangyt.controller;

import com.liangyt.entity.User;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 *   <pre>
 *   @Api：用在类上，说明该类的作用
     @ApiOperation：用在方法上，说明方法的作用
     @ApiImplicitParams：用在方法上包含一组参数说明
     @ApiImplicitParam：用在@ApiImplicitParams注解中，指定一个请求参数的各个方面
        paramType：参数放在哪个地方
             header-->请求参数的获取：@RequestHeader
             query-->请求参数的获取：@RequestParam
             path（用于restful接口）-->请求参数的获取：@PathVariable
             body（不常用）
             form（不常用）
         name：参数名
         dataType：参数类型
         required：参数是否必须传
         value：参数的意思
         defaultValue：参数的默认值
     @ApiResponses：用于表示一组响应
         @ApiResponse：用在@ApiResponses中，一般用于表达一个错误的响应信息
             code：数字，例如400
             message：信息，例如"请求参数没填好"
             response：抛出异常的类
     @ApiModel：描述一个Model的信息（这种一般用在post创建的时候，使用@RequestBody这样的场景，请求参数无法使用@ApiImplicitParam注解进行描述的时候）
     @ApiModelProperty：描述一个model的属性
     @ApiParam: 对一个参数的描述
     </pre>
 *
 *
 * 描述：Swagger 测试
 * 创建时间 2017-12-13 14:33
 * 作者 tony
 */
@RestController
@RequestMapping(value = "/swaggerapi")
@Api(value = "测试Swagger")
public class SwaggerController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @ApiOperation(value = "测试Get类型")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "id", required = true, value = "用户ID"),
            @ApiImplicitParam(paramType = "query", name = "name", required = true, value = "用户名", defaultValue = "我是默认名字")
    })
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数没有填写完整"),
            @ApiResponse(code = 404, message = "请求路径有问题")
    })
    @GetMapping("/get/{id}")
    public Object get(@RequestParam("name") String name,
                      @PathVariable("id") Long id) {

        logger.debug("请求的参数为：name:" + name + ";id:" + id);

        Map map = new HashMap();
        map.put("user", "张三");
        return map;
    }

    @ApiOperation(value = "测试POST类型")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数没有填写完整"),
            @ApiResponse(code = 404, message = "请求路径有问题")
    })
    @PostMapping("/post")
    public Object post(@ApiParam(name = "user", value = "这里输入一些参数描述", required = true) @RequestBody User user) {
        logger.debug(user.toString());
        return user;
    }
}
