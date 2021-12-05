package com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ContactList extends ArrayList<Contact> {
    private List<Contact> list;

    public ContactList(List<Contact> list){ this.list= list; }

    // @TODO
    public void serialize (DataOutputStream out) throws IOException {
        out.writeInt(list.toArray().length);
        for(Contact c: this.list){
            c.serialize(out);
        }
    }

    // @TODO
    public static List<Contact> deserialize (DataInputStream in) throws IOException {
        List list= new ArrayList<Contact>();
        int size= in.readInt();
        for(int i= 0; i< size; i++){
            Contact c= Contact.deserialize(in);
            list.add(c);
        }
        return list;
    }
}
