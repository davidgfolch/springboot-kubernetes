package com.kubikdata.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class User {

    @Id
    Long id;
    String userName;
    String email;
    String name;
    String pass;
    boolean verified;

}
