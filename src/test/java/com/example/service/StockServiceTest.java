package com.example.service;

import com.example.repository.StockRepository;
import com.domain.Stock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockServiceTest {
    @Autowired
    StockService stockService;

    @Autowired
    StockRepository stockRepository;

    @BeforeEach
    public void before() {
        Stock stock = new Stock(1l, 100l);
        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    public void after () {
        stockRepository.deleteAll();;
    }

    @Test
    public void stock_decrease() {
        stockService.decrease(1l, 1l);
        Stock stock = stockRepository.findById(1l).orElseThrow();
        assertEquals(99l, stock.getQuantity());
    }

    @Test
    public void 동시에_100개의_요청() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(100);
        for(int i = 0; i< threadCount; i++) {
            executorService.submit(() -> {
                try{
                    stockService.decrease(1l, 1l);
                }finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        Stock stock = stockRepository.findById(1l).orElseThrow();
        assertEquals(0l, stock.getQuantity());
    }
}