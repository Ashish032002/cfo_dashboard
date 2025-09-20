package com.example.demo.graphql.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CreateUserInput {
    @NotBlank
    @NotBlank(message = "name must not be blank")private String name;
    @NotBlank(message = "email must not be blank") @Email(message = "email must be a valid address") private String email;

    public CreateUserInput() {}
    public CreateUserInput(String name, String email) { this.name = name; this.email = email; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
}
