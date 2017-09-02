package lib.ujax.models.magic;

import com.google.gson.Gson;

interface GsonProxy {
  Gson getOriginal();
  void setOriginal(Gson gson);
}