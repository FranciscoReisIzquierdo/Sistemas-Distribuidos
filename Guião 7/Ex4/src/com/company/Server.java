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
            if(!this.contacts.containsKey(c.name())) this.contacts.put(c.name(), c); //Adicionar
            else this.contacts.replace(c.name(), c); //Actualizar
        }
        finally {
            this.lock.unlock();
        }
    }

    // @TODO
    public List<Contact> getContacts() {
        try {
            this.lock.lock();
            List<Contact> contacts = new ArrayList<>();
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
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));

            while (true) {
                int option= in.readInt();
                if(option== 1) {
                    Contact receivedContact = Contact.deserialize(in);
                    this.manager.update(receivedContact);
                    System.out.println("Contact updated/created!: Contact: " + receivedContact.toString());
                    break;
                }
                else if(option== 2){
                    List<Contact> lista= this.manager.getContacts();
                    ContactList listaCompleta= new ContactList(lista);
                    listaCompleta.serialize(out);
                    out.flush();
                    System.out.println("Lista de contactos enviada!");
                    break;
                }
                else{
                    System.out.println("Opção inválida!");
                    break;
                }
            }
        }
        catch(IOException e){
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
