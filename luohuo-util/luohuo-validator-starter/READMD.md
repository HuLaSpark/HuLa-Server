# 说明

## 测试代码请查看： luohuo-authority-controller -> test/*ValidateController

## hibernate-validator 官方对以下3种 入参类型 的请求都支持校验， 但目前本工具类只支持获取第二和第三种类型的入参校验规则

- 1、普通参数类型 （详见：ParamValidateController）

```
@Validated
public class ParamValidateController {
    @GetMapping("/requestParam/get1")
    public String paramGet1(@NotEmpty(message = "不能为空")
                            @RequestParam(value = "code", required = false) String code) {
        return "一定要在类上面写@Validated注解";
    }
}
```

- 2、对象参数 （详见：ObjValidateController）

```
    @GetMapping("/obj/get3")
    public String objGet3(@Validated InnerDTO data) {
        return "只有参数上有@Validated 可以验证";
    }
```

- 3、@RequestBody 格式的对象参数 （详见：BodyValidateController）

```
    @PostMapping("/post6")
    public String bodyPost6(@Validated @RequestBody HiberDTO data) {
        return "只有参数上有@Validated， 可以验证 ";
    }
```

## 如何使用？

参考： https://www.kancloud.cn/zuihou/zuihou-admin-cloud/2074606

