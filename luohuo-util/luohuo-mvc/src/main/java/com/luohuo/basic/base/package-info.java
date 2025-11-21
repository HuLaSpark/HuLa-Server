/**
 * MVC基础包， 封装的 Controller、Service、Manager、Mapper等
 * <p>
 * 为什么Controller要拆分成这么多个？而Service和Mapper不拆分
 * 1. Controller 是给前端使用的，对于前端人员，看到的无用接口越少，越有利于对接
 * 2. 过多的 Controller 接口暴露在外，增加被人恶意攻击风险
 * 3. Service、Manager和Mapper都是后端人员使用，丰富一些无所谓,就算会多个后端协同开发，因为都懂JAVA，沟通阅读起来没那么难
 * 4. Service： 业务逻辑层（大业务）， 相对具体的业务逻辑服务层
 * 5. Manager: 通用业务层（小业务）， 继承了MP的IService：
 * 1） 对第三方平台封装的层，预处理返回结果及转化异常信息。
 * 2） 对 Service 层通用能力的下沉，如缓存方案、中间件通用处理。
 * 3） 与 DAO 层交互，对多个 DAO 的组合复用。
 * 6. Mapper：数据访问层, 继承了MP的BaseMapper， 与底层MySQL交互
 *
 * @version 4.0.0
 * @author 乾乾
 * @date 2020年03月07日22:35:57
 * @since 4.0.0
 */
package com.luohuo.basic.base;

