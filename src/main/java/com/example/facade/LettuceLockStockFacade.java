package com.example.facade;

import com.example.repository.RedisLockRepository;
import com.example.service.StockService;
import org.springframework.stereotype.Component;

@Component
public class LettuceLockStockFacade {
    private RedisLockRepository redisLockRepository;

    private StockService stockService;

    public LettuceLockStockFacade(RedisLockRepository redisLockRepository, StockService stockService) {
        this.redisLockRepository = redisLockRepository;
        this.stockService = stockService;
    }

    public void decrease(long key, Long quantity) throws InterruptedException {
        while (!redisLockRepository.lock(key)){ // spin lock
            Thread.sleep(100); // 레디스 부하 줄임
        }
        try{
            stockService.decrease(key, quantity);
        }finally {
            redisLockRepository.unlock(key);
        }
    }
}
