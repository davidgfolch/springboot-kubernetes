package com.kubikdata.user.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kubikdata.model.User;

public class UserDTO extends User {

    @Override
    @JsonIgnore
    public String getPass() { return null; }
}
