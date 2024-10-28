package com.example.doan;
public class FirestoreClass {
    public static class User {
        public String id;
        public String name;
        public String email;
        public String password;

        // No-argument constructor required for Firestore deserialization
        public User() {}

        public User(String i, String n, String e, String pass) {
            this.id = i;
            this.name = n;
            this.email = e;
            this.password = pass;
        }

    }
}
