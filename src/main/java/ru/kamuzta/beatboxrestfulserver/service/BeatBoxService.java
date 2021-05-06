package ru.kamuzta.beatboxrestfulserver.service;

import org.springframework.stereotype.Service;
import ru.kamuzta.beatboxrestfulserver.model.Message;
import java.util.*;

@Service
public class BeatBoxService{
    private List<Message> messages = new ArrayList<>();

    /**
     * Send new message to server
     * endpoint /sendmessage
     * @param message - message to send in JSON
     */
    public void sendMessage(Message message) {
        messages.add(message);
    }

    /**
     * Return all messages to client
     * endpoint /getchat
     * @return list of messages in JSON
     */
    public List<Message> getChat() {
        return messages;
    }

    /**
     * Checking server status
     * endpoint /checkconnection
     * @return greetig_message
     */
    public String checkConnection() {
        return "Wellcome to BeatBoxRESTfulServer";
    }

}
