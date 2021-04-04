package ru.kamuzta.beatboxrestfulserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kamuzta.beatboxrestfulserver.model.Message;
import ru.kamuzta.beatboxrestfulserver.service.BeatBoxService;

import java.util.List;

@RestController
public class Controller {

    private final BeatBoxService service;

    @Autowired
    public Controller(BeatBoxService service) {
        this.service = service;
    }

    @PostMapping(value = "/sendmessage")
    public ResponseEntity<?> sendMessage(@RequestBody Message message) {
        if (message != null) {
            service.sendMessage(message);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/getchat")
    public ResponseEntity<List<Message>> getChat() {
        final List<Message> messages = service.getChat();

        return messages != null &&  !messages.isEmpty()
                ? new ResponseEntity<>(messages, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/checkconnection")
    public ResponseEntity<String> checkConnection() {
        final String greeting = service.checkConnection();

        return greeting != null
                ? new ResponseEntity<>(greeting, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
