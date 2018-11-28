package de.mpg.escidoc.services.pidcache.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FullPid {
  private int idx; // 1,
  private String type; // "URL"
  private Object parsedData; // "http://dev-pubman.mpdl.mpg.de/pubman/faces/PidNotResolved.jsp?id=1509975377321"
  @JsonIgnore
  private String data; // "aHR0cDovL2Rldi1wdWJtYW4ubXBkbC5tcGcuZGUvcHVibWFuL2ZhY2VzL1Bp\nZE5vdFJlc29sdmVkLmpzcD9pZD0xNTA5OTc1Mzc3MzIx"
  @JsonIgnore
  private String timestamp; // "2017-11-06T13:36:17Z"
  @JsonIgnore
  private int ttlType; // 0
  @JsonIgnore
  private int ttl; // 86400
  @JsonIgnore
  private String[] refs; // []
  @JsonIgnore
  private String privs; // "rwr-"

  public int getIdx() {
    return this.idx;
  }

  public void setIdx(int idx) {
    this.idx = idx;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @JsonProperty("parsed_data")
  public Object getParsedData() {
    return this.parsedData;
  }

  @JsonProperty("parsed_data")
  public void setParsedData(Object parsedData) {
    this.parsedData = parsedData;
  }

  public String getData() {
    return this.data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  @JsonProperty("ttl_type")
  public int getTtlType() {
    return this.ttlType;
  }

  @JsonProperty("ttl_type")
  public void setTtlType(int ttlType) {
    this.ttlType = ttlType;
  }

  public int getTtl() {
    return this.ttl;
  }

  public void setTtl(int ttl) {
    this.ttl = ttl;
  }

  public String[] getRefs() {
    return this.refs;
  }

  public void setRefs(String[] refs) {
    this.refs = refs;
  }

  public String getPrivs() {
    return this.privs;
  }

  public void setPrivs(String privs) {
    this.privs = privs;
  }

}
