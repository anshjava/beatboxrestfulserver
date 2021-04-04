package ru.kamuzta.beatboxrestfulserver.service;

import ru.kamuzta.beatboxrestfulserver.model.Message;

import java.util.List;

public interface MessageService {

    /**
     * Send new message to server
     * endpoint /sendmessage
     * @param message - message to send in JSON
     */
    void sendMessage(Message message);

    /**
     * Return all messages to client
     * endpoint /getchat
     * @return list of messages in JSON
     */
    List<Message> getChat();

    /**
     * Checking server status
     * endpoint /checkconnection
     * @return greetig_message
     */
    String checkConnection();

}
