package lib.ujax.models;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class AttrReadableFilter implements ExclusionStrategy {

  @Override
  public boolean shouldSkipClass(Class<?> __) {
    return false;
  }

  @Override
  public boolean shouldSkipField(FieldAttributes fieldAttributes) {
    return fieldAttributes.getAnnotation(Accessible.class) == null &&
           fieldAttributes.getAnnotation(Expose.class) == null;
  }
}