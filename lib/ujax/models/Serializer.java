package lib.ujax.models;

import lib.ujax.models.magic.GsonInterceptor;
import lib.ujax.models.magic.GsonProxier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Serializer extends GsonInterceptor<SerializerPrefix> {

  public static Gson getInstance() {
    Gson gson = new GsonBuilder()
      .setExclusionStrategies(new AttrReadableFilter())
      .create();

    return GsonProxier.wrap(
      gson,
      GsonProxier.getProxyConstructor(
        GsonProxier.getProxyType(Serializer.class)
      )
    );
  }
}