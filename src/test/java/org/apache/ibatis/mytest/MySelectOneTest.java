package org.apache.ibatis.mytest;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MySelectOneTest {

    @Test
    public void testSelectById() throws Exception {

        // ① 读配置，构建 SqlSessionFactory
        //    断点打在这里 → 进 SqlSessionFactoryBuilder.build()
        //    观察 Configuration 对象是怎么被填充的
        String resource = "org/apache/ibatis/mytest/mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(inputStream);

        // ② 打开 SqlSession
        //    断点打在这里 → 进 DefaultSqlSessionFactory.openSession()
        //    观察 Executor 是怎么被选择和创建的
        try (SqlSession session = factory.openSession()) {

            // ③ 获取 Mapper 代理对象
            //    断点打在这里 → 进 MapperRegistry.getMapper()
            //    观察 MapperProxyFactory 怎么用 JDK 动态代理生成接口实例
            UserMapper mapper = session.getMapper(UserMapper.class);

            // ④ 调用接口方法 — 全流程从这里触发
            //    断点打在这里，然后 Step Into
            //    链路: MapperProxy.invoke()
            //       → MapperMethod.execute()
            //       → DefaultSqlSession.selectOne()
            //       → CachingExecutor.query()
            //       → BaseExecutor.query() (一级缓存)
            //       → SimpleExecutor.doQuery()
            //       → PreparedStatementHandler.query()
            //       → DefaultResultSetHandler.handleResultSets()
            User user = mapper.selectById(1);

            // ⑤ 验证结果
            assertNotNull(user);
            assertEquals(1, user.getId());
            assertEquals("Alice", user.getName());
            System.out.println("结果: " + user);
        }
    }
}
