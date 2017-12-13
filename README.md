#### MockMvc 和 Swagger UI 的示例对比  

以 <code>spring-boot-starter-web</code> 为基础，实现两个简单的restful api,一个是 Get 类型，一个是Post类型。

##### MockMvc
只需要再添加一个依赖
```apple js
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
</dependency>
```
添加两个类，一个启动类，一个Controller  
Controller比较简单， 如下：
```apple js
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
```
启动类:
```apple js
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```
到这那准备工作已经完成了，下面再看一下使用MockMvc的测试类:
```apple js
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
```
整个MockMvc的测试到这就全部完成了，可以跑一下测试示例看看结果。

##### Swagger UI
要使用 <code>Swagger UI</code>还需要添加对应的依赖，有两个
```apple js
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.7.0</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.7.0</version>
</dependency>
```
为了让Swagger被启用，需要在启动类添加一个注解
```apple js
@SpringBootApplication
@EnableSwagger2 // 启用 swagger
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```
这个 Swagger 就被启用了，服务启动的时候自动扫描对应的包.  
新建一个Controller类用于测试Swagger，同时也为了区分MockMvc测试：
```apple js
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
```
要看到效果，在控制台运行:
> mvn spring-boot:run  

启动完成访问路径:
> http://localhost:8080/swagger-ui.html