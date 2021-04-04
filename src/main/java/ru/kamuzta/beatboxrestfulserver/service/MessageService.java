package ru.kamuzta.beatboxrestfulserver.service;

import ru.kamuzta.beatboxrestfulserver.model.Message;

import java.util.List;

public interface MessageService {

void sendMessage(Message message);
List<Message> getChat();
String checkConnection();

}
