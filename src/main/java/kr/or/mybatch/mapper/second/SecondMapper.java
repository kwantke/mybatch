package kr.or.mybatch.mapper.second;

import kr.or.mybatch.model.vo.FirstDataVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SecondMapper {
  public List<FirstDataVo> getSecondData();
  public void insertSecondData(List<FirstDataVo> firstDataList);
}


