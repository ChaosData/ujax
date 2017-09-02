package lib.ujax.filter.common;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileOutputStream;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTSigner;

public class JWTSecret {
  public static final int MAX_AGE = 60*60*12;

  private static byte[] secret = new byte[32];

  public static JWTVerifier verifier = null;
  public static JWTSigner signer = null;

  static {
    int read = 0;
    try {
      FileInputStream fis = new FileInputStream("./session.secret");
      read = fis.read(secret);
      fis.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (read != 32) {
      try {
        FileInputStream fis = new FileInputStream("/dev/urandom");
        fis.read(secret, 0, 32);
        fis.close();

        File secret_file = new File("./session.secret");
        if(!secret_file.exists()) {
          secret_file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(secret_file);
        fos.write(secret, 0, 32);
        fos.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    verifier = new JWTVerifier(secret);
    signer = new JWTSigner(secret);
  }

}
