package org.example.model;
// mahane na javaDoc, mahane na validaciq ot service - dobavqne v bazata
// 1 handler , mahane na validaciq, pytest/assert, ako grumne json - error hand., HttpServer,
// blokirano i neblokirano, sinhr i asinhr
// da opravq handlerite da ne sa samo za post, da mahna dto-tata
public class User {

    private String full_name;
    private String email;
    private String password;

    public User(String full_name, String email, String password) {
        this.full_name = full_name;
        this.email = email;
        this.password = password;
    }
    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String name) {
        this.full_name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
