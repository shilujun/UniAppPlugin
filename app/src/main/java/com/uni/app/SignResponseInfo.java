package com.uni.app;

import java.util.Map;

public class SignResponseInfo {

  private int code;
  private String msg;
  private Map<String, String> data;

  public int getCode() {
    return this.code;
  }

  public String getMsg() {
    return this.msg;
  }

  public Map<String, String> getData() {
    return this.data;
  }
}
