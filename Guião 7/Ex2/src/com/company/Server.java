package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Arrays.asList;

class ContactManager {
    private HashMap<String, Contact> contacts;
    private Lock lock= new ReentrantLock();

    public ContactManager() {
        contacts = new HashMap<>();
        // example pre-population
        update(new Contact("John", 20, 253123321, null, asList("john@mail.com")));
        update(new Contact("Alice", 30, 253987654, "CompanyInc.", asList("alice.personal@mail.com", "alice.business@mail.com")));
        update(new Contact("Bob", 40, 253123456, "Comp.Ld", asList("bob@mail.com", "bob.work@mail.com")));
    }


    // @TODO
    public void update(Contact c) {
        try {
            this.lock.lock();
            if(!this.contacts.containsKey(c.name())) this.contacts.put(c.name(), c);
        }
        finally {
            this.lock.unlock();
        }
    }

    // @TODO
    public ContactList getContacts() {
        try {
            this.lock.lock();
            ContactList contacts = (ContactList) new ArrayList<Contact>();
            for (Map.Entry<String, Contact> entry : this.contacts.entrySet()) contacts.add(entry.getValue());
            return contacts;
        }
        finally {
            this.lock.unlock();
        }
    }
}

class ServerWorker implements Runnable {
    private Socket socket;
    private ContactManager manager;

    public ServerWorker (Socket socket, ContactManager manager) {
        this.socket = socket;
        this.manager = manager;
    }

    // @TODO
    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
            Contact receivedContact = Contact.deserialize(in);
            this.manager.update(receivedContact);
            System.out.println("Contact updated/created!: Contact: " + receivedContact.toString());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}



public class Server {

    public static void main (String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        ContactManager manager = new ContactManager();

        while (true) {
            Socket socket = serverSocket.accept();
            Thread worker = new Thread(new ServerWorker(socket, manager));
            worker.start();
        }
    }
}
