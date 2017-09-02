package app.models;

import javax.persistence.*;
/*import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
*/
import org.mindrot.jbcrypt.BCrypt;

@Entity
@Table(name="users")  
public class User {

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private long id;

  @Column(name="username")
  private String username;

  @Column(name="password_hash")
  private String passwordHash;

  public long getId() {
      return id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  private String getPasswordHash() {
    return passwordHash;
  }

  private void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public void setPassword(String password) {
    this.passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(12));
  }

  public boolean validatePassword(String supplied_password) {
    return BCrypt.checkpw(supplied_password, this.passwordHash);
  }

}

