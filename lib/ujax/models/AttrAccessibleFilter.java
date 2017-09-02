package lib.ujax.models;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class AttrAccessibleFilter implements ExclusionStrategy {

  @Override
  public boolean shouldSkipClass(Class<?> __) {
    return false;
  }

  @Override
  public boolean shouldSkipField(FieldAttributes fieldAttributes) {
    return fieldAttributes.getAnnotation(Accessible.class) == null;
  }
}