package com.liangyt.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.liangyt.Application;
import com.liangyt.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 描述：测试MockMvc
 * 创建时间 2017-12-13 11:38
 * 作者 tony
 */
@SuppressWarnings("all")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration // 声明一个ApplicationContext集成测试, 用于加载WebApplicationContext模拟ServletContext
//配置事务的回滚,对数据库的增删改都会回滚,便于测试用例的循环利用,因为本示例没有使用数据还需要测试验证
//@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@SpringBootTest(classes = Application.class)
public class MockMvcControllerTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void get() throws Exception{
        MvcResult mvcResult = mockMvc.perform( // 执行一个RequestBuilder请求
                MockMvcRequestBuilders // 有几个静态方法，用以发起不同类型的请求
                .get("/mockapi/get/12")
                .accept(MediaType.APPLICATION_JSON) // accept 类型
                .param("name", "我是名字")) // 查询参数
                .andDo( // 添加ResultHandler结果处理器，比如调试时打印结果到控制台（对返回的数据进行的判断
                        print()) // 打印请求信息，如请求头，请求路径，请求数据等
                .andExpect(status().isOk()) // 判断状态是否返回 200
                // 判断返回的数据是否正确 用“$.属性”获取里面的数据，如我要获取返回数据中的"data.name"，可以写成"$.data.name"
                // org.hamcrest.Matchers 这个类有很多判断方式可以看一下
                .andExpect(jsonPath("$.user", is("张三"))) // 判断 user 是否为 张三， 可以修改一个不正确的值看看结果
                .andReturn(); // Mock返回结果

        MockHttpServletResponse response = mvcResult.getResponse(); // 请求返回的结果

        logger.debug(response.getStatus() + ""); // 请求状态
        logger.debug(response.getContentAsString()); // 返回结果用字符串显示
    }

    @Test
    public void post() throws Exception{
        User user = new User(2L, "李四");

        // 利用 jackjson 处理对象为json格式的字符串,以便 @RequestBody 接收
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String sendData = objectWriter.writeValueAsString(user);

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders
                .post("/mockapi/post")
                .contentType(MediaType.APPLICATION_JSON) // 发送数据的格式类型
                .content(sendData))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse(); // 请求返回的结果

        logger.debug(response.getStatus() + ""); // 请求状态
        logger.debug(response.getContentAsString()); // 返回结果用字符串显示
    }
}
