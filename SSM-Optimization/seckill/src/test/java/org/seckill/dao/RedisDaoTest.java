package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.enerty.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by wei11 on 2017/3/9.
 */
@RunWith(SpringJUnit4ClassRunner.class)
// 告诉junit spring配置位置
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class RedisDaoTest {
    private long id = 1001L;

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SeckillDao seckillDao;

    @Test
    public void testRedisGetSeckill() throws Exception {
        // get and put
        Seckill seckill = redisDao.getSeckill(id);
        if (seckill == null) {
            seckill = seckillDao.queryById(id);
            if(seckill != null) {
                String result = redisDao.putSeckill(seckill);
                System.out.println("result: " + result);
                seckill = redisDao.getSeckill(id);
                System.out.println("seckill: " + seckill);
            }
        }
    }
}