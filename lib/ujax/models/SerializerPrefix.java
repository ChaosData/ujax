package lib.ujax.models;

import lib.ujax.models.magic.GsonProxyPrefix;

class SerializerPrefix implements GsonProxyPrefix {
  public String getAllowedPrefix() {
    return "toJs";
  }
}