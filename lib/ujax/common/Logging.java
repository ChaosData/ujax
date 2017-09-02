package lib.ujax.common;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.function.Supplier;

public class Logging {

  public static final Logger logger = LogManager.getLogger("ujax");

  public static String current() {
    StackTraceElement ste = new Exception().getStackTrace()[6];
    return ste.getClassName() + "["
           + ste.getFileName() + ":" + ste.getLineNumber() + "]::"
           + ste.getMethodName();
  };

}
