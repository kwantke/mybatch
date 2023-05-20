package kr.or.mybatch.mapper.first;

import kr.or.mybatch.model.vo.FirstDataVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FirstMapper {
  public List<FirstDataVo> getFirstData();
}

