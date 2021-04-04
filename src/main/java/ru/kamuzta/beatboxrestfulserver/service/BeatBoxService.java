package ru.kamuzta.beatboxrestfulserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.stereotype.Service;
import ru.kamuzta.beatboxrestfulserver.model.Message;

import java.io.*;
import java.net.*;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

@Service
public class BeatBoxService implements MessageService{
    private List<Message> messages = new ArrayList<>();

    @Override
    public void sendMessage(Message message) {
        messages.add(message);
    }

    @Override
    public List<Message> getChat() {
        return messages;
    }

    @Override
    public String checkConnection() {
        return "Wellcome to BeatBoxRESTfulServer";
    }

}
