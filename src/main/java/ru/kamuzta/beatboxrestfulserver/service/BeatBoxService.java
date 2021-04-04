package ru.kamuzta.beatboxrestfulserver.service;

import org.springframework.stereotype.Service;
import ru.kamuzta.beatboxrestfulserver.model.Message;
import java.util.*;

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
