package ru.kamuzta.beatboxrestfulserver.service;

import org.springframework.stereotype.Service;
import ru.kamuzta.beatboxrestfulserver.model.DBWorker;
import ru.kamuzta.beatboxrestfulserver.model.Message;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class BeatBoxService {
    private List<Message> messages = new CopyOnWriteArrayList<>();
    private DBWorker dbWorker = new DBWorker();

    /**
     * Send new message to server
     * endpoint /sendmessage
     * @param fromHttpMessage - message received by HTTP
     */
    public void sendMessage(Message fromHttpMessage) {
        final String INSERT_NEW = "INSERT INTO beatbox (time,name,message,melody) VALUES(?,?,?,?)";
        try (PreparedStatement preparedStatement = dbWorker.getConnection().prepareStatement(INSERT_NEW)) {
            Timestamp timeStamp = new java.sql.Timestamp(System.currentTimeMillis());
            preparedStatement.setTimestamp(1, timeStamp);
            preparedStatement.setString(2, fromHttpMessage.getSenderName());
            preparedStatement.setString(3, fromHttpMessage.getSenderMessage());
            preparedStatement.setString(4, fromHttpMessage.convertSenderMelodyToText());
            preparedStatement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    /**
     * Return all messages to client
     * endpoint /getchat
     * @return list of messages in JSON
     */
    public List<Message> getChat() {
        messages.clear();
        final String GET_ALL = "SELECT * FROM beatbox";
        try (PreparedStatement preparedStatement = dbWorker.getConnection().prepareStatement(GET_ALL)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                Message msg = new Message();
                msg.setId(resultSet.getInt("id"));
                msg.setSenderTime(resultSet.getTimestamp("time").toLocalDateTime());
                msg.setSenderName(resultSet.getString("name"));
                msg.setSenderMessage(resultSet.getString("message"));
                msg.setSenderMelody(msg.convertTextToSenderMelody(resultSet.getString("melody")));
                messages.add(msg);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
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
