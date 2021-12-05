package com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

class Contact {
    private String name;
    private int age;
    private long phoneNumber;
    private String company;     // Pode ser null
    private ArrayList<String> emails;

    public Contact (String name, int age, long phoneNumber, String company, List<String> emails) {
        this.name = name;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.company = company;
        this.emails = new ArrayList<>(emails);
    }

    public String name() { return name; }
    public int age() { return age; }
    public long phoneNumber() { return phoneNumber; }
    public String company() { return company; }
    public List<String> emails() { return new ArrayList(emails); }

    // @TODO
    public void serialize (DataOutputStream out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.age);
        out.writeLong(this.phoneNumber);
        out.writeBoolean(this.company!= null);
        if(this.company!= null) out.writeUTF(this.company);
        out.writeInt(this.emails.toArray().length);
        for(String email: this.emails) out.writeUTF(email);
    }

    // @TODO
    public static Contact deserialize (DataInputStream in) throws IOException {
        String name= in.readUTF();
        int age= in.readInt();
        long phoneNumber= in.readLong();
        boolean flag= in.readBoolean();
        String company;
        if(!flag) company= null;
        else company= in.readUTF();
        int length= in.readInt();
        List<String> emails= new ArrayList<>();
        for(int i= 0; i< length; i++) emails.add(in.readUTF());

        return new Contact(name, age, phoneNumber, company, emails);
    }

    public String toString () {
        StringBuilder builder = new StringBuilder();
        builder.append(this.name).append(";");
        builder.append(this.age).append(";");
        builder.append(this.phoneNumber).append(";");
        builder.append(this.company).append(";");
        builder.append(this.emails.toString());
        builder.append("}");
        return builder.toString();
    }

}
