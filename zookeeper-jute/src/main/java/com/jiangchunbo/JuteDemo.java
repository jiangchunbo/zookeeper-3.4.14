package com.jiangchunbo;

import org.apache.jute.CsvOutputArchive;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collections;

public class JuteDemo {

    public static void main(String[] args) throws IOException {
        Person person = new Person();
        person.setId(1);
        person.setName("zhangsan");

        AddressBook addressBook = new AddressBook();
        addressBook.setPersons(Collections.singletonList(person));

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        CsvOutputArchive archive = new CsvOutputArchive(os);

        addressBook.serialize(archive, "");
        System.out.println(os.toString("utf-8"));
    }

}
