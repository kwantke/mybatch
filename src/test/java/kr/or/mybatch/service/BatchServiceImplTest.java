package kr.or.mybatch.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class BatchServiceImplTest {

  @Autowired
  BatchServiceImpl batchService;

  @Test
  void batchTest() {
    batchService.batchTest();
  }
}