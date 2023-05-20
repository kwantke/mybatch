package kr.or.mybatch.model.vo;

import lombok.Data;

@Data
public class FirstDataVo {
  private Long seq;
  private String id;
  private String name;
  private String email;
  private String password;
  private String reg_date;
}
