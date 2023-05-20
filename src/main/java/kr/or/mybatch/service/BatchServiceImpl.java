package kr.or.mybatch.service;

import kr.or.mybatch.mapper.first.FirstMapper;
import kr.or.mybatch.mapper.second.SecondMapper;
import kr.or.mybatch.model.vo.FirstDataVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BatchServiceImpl {

  FirstMapper firstMapper;

  SecondMapper secondMapper;

  @Autowired
  public BatchServiceImpl(FirstMapper firstMapper, SecondMapper secondMapper){
    this.firstMapper= firstMapper;
    this.secondMapper =secondMapper;
  }

  public void batchTest(){
    List<FirstDataVo> frDtList = firstMapper.getFirstData();

    int skip = 0;
    int limit = 500;
    while(skip < frDtList.size()){
      final List<FirstDataVo> list = frDtList
              .stream()
              .skip(skip)
              .limit(skip+limit)
              .collect(Collectors.toList());
      skip +=limit;
      secondMapper.insertSecondData(list);
    }
  }
}
