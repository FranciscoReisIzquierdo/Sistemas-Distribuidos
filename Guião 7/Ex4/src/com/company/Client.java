package com.company;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client {

    public static Contact parseLine (String userInput) {
        String[] tokens = userInput.split(" ");

        if (tokens[3].equals("null")) tokens[3] = null;

        return new Contact(
                tokens[0],
                Integer.parseInt(tokens[1]),
                Long.parseLong(tokens[2]),
                tokens[3],
                new ArrayList<>(Arrays.asList(tokens).subList(4, tokens.length)));
    }


    public static void main (String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345);
        DataOutputStream out= new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        DataInputStream input= new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("1-> Adicionar/atualizar contacto\n2-> Receber lista de contactos\n");

        String userInput;
        while ((userInput = in.readLine()) != null) {
            out.writeInt(Integer.parseInt(userInput));
            out.flush();

            if(Integer.parseInt(userInput)== 1) {
                userInput = in.readLine();
                Contact newContact = parseLine(userInput);
                System.out.println(newContact.toString());
                newContact.serialize(out);
                out.flush();
                break;
            }
            else if(Integer.parseInt(userInput)== 2){
                List<Contact> lista= ContactList.deserialize(input);
                System.out.println("Lista de contactos recebida!\nLista:");
                for(Contact c: lista) System.out.println(c.toString());
                break;
            }
            else{
                System.out.println("Opção inválida!");
                break;
            }
        }
        socket.close();
    }
}
