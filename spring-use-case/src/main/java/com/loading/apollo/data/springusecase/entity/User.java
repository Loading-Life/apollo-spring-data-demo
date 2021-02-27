package com.loading.apollo.data.springusecase.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * desc:
 *
 * @author Lo_ading
 * @version 1.0.0
 * @date 2021/2/26
 */
@Entity
public class User {

  @Id
  private int id;

  private String name;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
