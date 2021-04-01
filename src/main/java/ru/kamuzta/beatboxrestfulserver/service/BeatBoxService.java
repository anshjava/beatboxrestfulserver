package ru.kamuzta.beatboxrestfulserver.service;

import org.springframework.stereotype.Service;
import ru.kamuzta.beatboxrestfulserver.model.Message;

import java.io.*;
import java.net.*;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

@Service
public class BeatBoxService implements MessageService{
    private List<Message> messages;

    @Override
    public void sendMessage(Message message) {
        messages.add(message);
    }

    @Override
    public List<Message> getChat() {
        return messages;
    }
}
