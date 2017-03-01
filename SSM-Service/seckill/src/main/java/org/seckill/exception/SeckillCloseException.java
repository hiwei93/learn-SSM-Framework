package org.seckill.exception;

/**
 * 秒杀关闭异常
 * Created by wei11 on 2017/2/28.
 */
public class SeckillCloseException extends SeckillException {

    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
