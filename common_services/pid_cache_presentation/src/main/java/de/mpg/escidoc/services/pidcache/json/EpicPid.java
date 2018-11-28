package de.mpg.escidoc.services.pidcache.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EpicPid {
  private String epicPid;

  @JsonProperty("epic-pid")
  public String getEpicPid() {
    return this.epicPid;
  }

  @JsonProperty("epic-pid")
  public void setEpicPid(String epicPid) {
    this.epicPid = epicPid;
  }

}
