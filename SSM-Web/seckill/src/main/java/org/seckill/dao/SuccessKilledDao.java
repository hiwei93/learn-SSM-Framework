package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.enerty.SuccessKilled;

/**
 * Created by wei11 on 2017/2/18.
 */
public interface SuccessKilledDao {

    /**
     * 查询购买明细，可过滤重复
     * @param seckillId
     * @param userPhone
     * @return
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    /**
     * 根据ID查询SuccessKilled并携带秒杀产品对象实体
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);


}
