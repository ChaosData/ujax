package app.models;

import javax.persistence.*;
import java.sql.Timestamp;
import lib.ujax.models.Accessible;
import lib.ujax.models.Expose;

@Entity
@Table(name="todos")  
public class Todo {

  @Expose
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private long id;

  public long getId() { return id; }
  private void setId(long id) { this.id = id; }

  @ManyToOne
  private User user;

  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }

  @Accessible
  @Column(name="local_id")
  private String local_id = null;

  public String getLocalId() { return local_id; }
  public void setLocalId(String local_id) { this.local_id = local_id; }


  @Accessible
  @Column(name="text")
  private String text = null;

  public String getText() { return text; }
  public void setText(String text) { this.text = text; }

  @Accessible
  @Column(name="completed")
  private boolean completed = false;

  public boolean getCompleted() { return completed; }
  public void setCompleted(boolean completed) { this.completed = completed; }

  @Column(name="creation")
  private Timestamp creation = null;

  public Timestamp getCreation() { return creation; }
  public void setCreation(Timestamp creation) { this.creation = creation; }

  @Column(name="deletion")
  private Timestamp deletion = null;

  public Timestamp getDeletion() { return deletion; }
  public void setDelection(Timestamp deletion) { this.deletion = deletion; }
}