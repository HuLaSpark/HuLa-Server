package com.luohuo.basic.echo.manager;

import com.google.common.base.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import com.luohuo.basic.annotation.echo.Echo;
import com.luohuo.basic.interfaces.echo.LoadService;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 封装 Echo 注解中解析出来的参数
 * <p>
 * 必须重写该类的 equals() 和 hashCode() 便于Map操作
 *
 * @author 乾乾
 * @date 2020年02月03日18:48:29
 */
@Data
@NoArgsConstructor
@ToString
@Slf4j
public class CacheLoadKeys {

    /**
     * 执行查询任务的类
     * <p/>
     * api()  和 feign() 任选其一,  使用 api时，请填写实现类， 使用feign时，填写接口即可
     * 如： @Echo(api="userServiceImpl") 等价于 @Echo(feign=UserService.class)
     * 如： @Echo(api="userController") 等价于 @Echo(feign=UserApi.class)
     */
    private String api;

    /**
     * 动态查询值
     */
    private Set<Serializable> keys = new HashSet<>();
    private LoadService loadService;

    public CacheLoadKeys(Echo rf) {
        this.api = rf.api();
    }

    public CacheLoadKeys(LoadKey lk, LoadService loadService, Set<Serializable> keys) {
        this.api = lk.getApi();
        this.loadService = loadService;
        this.keys = keys;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CacheLoadKeys that = (CacheLoadKeys) o;

        boolean apiMethod = Objects.equal(api, that.api);
        boolean isEqualsKeys = keys.size() == that.keys.size() && keys.containsAll(that.keys);
        return apiMethod && isEqualsKeys;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(api, keys);
    }


    /**
     * 加载数据
     *
     * @return 查询指定接口后得到的值
     */
    public Map<Serializable, Object> loadMap() {
        return loadService.findByIds(keys);
    }
}
