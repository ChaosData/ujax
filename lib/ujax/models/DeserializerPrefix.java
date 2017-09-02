package lib.ujax.models;

import lib.ujax.models.magic.GsonProxyPrefix;

class DeserializerPrefix implements GsonProxyPrefix {
  public String getAllowedPrefix() {
    return "toJs";
  }
}