package ru.kamuzta.beatboxrestfulserver;

import ru.kamuzta.beatboxrestfulserver.model.Message;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import javax.sound.midi.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.event.*;

public class BBSwingClient {
    private String userName;
    private String serverIp;
    private String serverPort;

    JFrame theFrame;
    JPanel mainPanel;
    JList incomingList;
    JTextField userMessage;
    ArrayList<JCheckBox> checkboxList;
    Vector<Message> messages = new Vector<Message>();
    Socket sock;
    ObjectOutputStream out;
    ObjectInputStream in;
    Thread remote;
    HashMap<String, boolean[]> otherSeqsMap = new HashMap<String, boolean[]>();
    Sequencer sequencer;
    Sequence sequence;
    Track track;

    private final String[] instrumentNames = {
            "Bass Drum",
            "Closed Hi-Cat",
            "Open Hi-Cat",
            "Acoustic Snare",
            "Crash Cymbal",
            "Hand Clap",
            "High Tom",
            "Hi Bongo",
            "Maracas",
            "Whistle",
            "Low Conga",
            "Cowbell ",
            "Vibraslap",
            "Low-mid Tom",
            "High Agogo",
            "Open Hi Conga"
    };
    private final int[] instruments = {
            34,
            42,
            46,
            38,
            49,
            39,
            50,
            60,
            70,
            72,
            64,
            56,
            58,
            47,
            67,
            63
    };

    public BBSwingClient(String userName, String serverIp, String serverPort) {
        this.userName = userName;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) {
        BBSwingClient client = new BBSwingClient("Anonymous", "127.0.0.1", "8080");
        client.setupGui();
        client.setupMidi();
    }

    public void setupGui() {
        theFrame = new JFrame("BeatBox");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        checkboxList = new ArrayList<JCheckBox>();

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Menu");
        JMenuItem saveMenuItem = new JMenuItem("Save melody");
        saveMenuItem.addActionListener(new SaveMenuListener());
        JMenuItem loadMenuItem = new JMenuItem("Load melody");
        loadMenuItem.addActionListener(new LoadMenuListener());
        JMenuItem connectMenuItem = new JMenuItem("Connect to server");
        connectMenuItem.addActionListener(new ConnectMenuListener());
        JMenuItem disconnectMenuItem = new JMenuItem("Disconnect from server");
        disconnectMenuItem.addActionListener(new DisconnectMenuListener());

        fileMenu.add(saveMenuItem);
        fileMenu.add(loadMenuItem);
        fileMenu.add(connectMenuItem);
        fileMenu.add(disconnectMenuItem);
        menuBar.add(fileMenu);
        theFrame.setJMenuBar(menuBar);

        Box buttonBox = new Box(BoxLayout.Y_AXIS);

        JButton start = new JButton("Start");
        start.addActionListener(new MyStartListener());
        buttonBox.add(start);

        JButton stop = new JButton("Stop");
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);

        JButton upTempo = new JButton("Tempo Up");
        upTempo.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo);

        JButton downTempo = new JButton("Tempo Down");
        downTempo.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTempo);

        JButton sendIt = new JButton("Send It");
        sendIt.addActionListener(new MySendItListener());
        buttonBox.add(sendIt);

        userMessage = new JTextField();
        buttonBox.add(userMessage);

        incomingList = new JList();
        incomingList.addListSelectionListener(new MyListSelectionListener());
        incomingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane theList = new JScrollPane(incomingList);
        buttonBox.add(theList);
        incomingList.setListData(messages);

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (int i = 0; i < 16; i++) {
            nameBox.add(new Label(instrumentNames[i]));
        }
        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        theFrame.getContentPane().add(background);
        GridLayout grid = new GridLayout(16, 16);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel);

        for (int i = 0; i < 256; i++) {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkboxList.add(c);
            mainPanel.add(c);
        }

        theFrame.setBounds(50, 50, 300, 300);
        theFrame.pack();
        theFrame.setVisible(true);
    }

    public void setupMidi() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupNetwork() {
        try {
            sock = new Socket(serverIp, 4242);
            out = new ObjectOutputStream(sock.getOutputStream());
            out.writeObject(this.userName);
            in = new ObjectInputStream(sock.getInputStream());
            remote = new Thread(new RemoteReader());
            remote.start();
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm")) + " Connected to server " + serverIp + ":4242 successfully");
        } catch (Exception e) {
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm")) + " couldn't connect - you'll have to play alone");
        }
    }

    public void buildTrackAndStart() {
        ArrayList<Integer> trackList = null;
        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for (int i = 0; i < 16; i++) {
            trackList = new ArrayList<Integer>();

            for (int j = 0; j < 16; j++) {
                JCheckBox jc = (JCheckBox) checkboxList.get(j + (16 * i));
                if (jc.isSelected()) {
                    int key = instruments[i];
                    trackList.add(new Integer(key));
                } else {
                    trackList.add(null);
                }
            }
            makeTracks(trackList);
        }
        track.add(makeEvent(192, 9, 1, 0, 15));
        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeSequence(boolean[] checkboxState) {
        for (int i = 0; i < 256; i++) {
            JCheckBox check = (JCheckBox) checkboxList.get(i);
            if (checkboxState[i]) {
                check.setSelected(true);
            } else {
                check.setSelected(false);
            }
        }
    }

    public void makeTracks(ArrayList list) {
        Iterator it = list.iterator();
        for (int i = 0; i < 16; i++) {
            Integer num = (Integer) it.next();
            if (num != null) {
                int numKey = num.intValue();
                track.add(makeEvent(144, 9, numKey, 100, i));
                track.add(makeEvent(128, 9, numKey, 100, i + 1));
            }
        }
    }

    public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one, two);
            event = new MidiEvent(a, tick);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return event;
    }

    public class MySendItListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (sock != null) {
                boolean[] melodyToSend = new boolean[256];
                for (int i = 0; i < 256; i++) {
                    JCheckBox check = (JCheckBox) checkboxList.get(i);
                    if (check.isSelected()) {
                        melodyToSend[i] = true;
                    }
                }
                Message msg = new Message(LocalDateTime.now(), userName, userMessage.getText(), melodyToSend);
                try {
                    out.writeObject(msg);
                    System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm")) + " message sended to the server");

                } catch (Exception ex) {
                    System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm")) + " could not send message to the server");
                    ex.printStackTrace();
                }
                userMessage.setText("");
            } else {
                JOptionPane.showMessageDialog(theFrame, "Нет соединения с сервером");
            }
        }
    }

    public class MyStartListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            buildTrackAndStart();
        }
    }

    public class MyStopListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            sequencer.stop();
        }
    }

    public class MyUpTempoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 1.03));
        }
    }

    public class MyDownTempoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 0.97));
        }
    }

    public class MyListSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                Message selected = (Message) incomingList.getSelectedValue();
                if (selected != null) {
                    boolean[] selectedState = (boolean[]) otherSeqsMap.get(selected.toString());
                    changeSequence(selectedState);
                    sequencer.stop();
                    buildTrackAndStart();
                }
            }
        }
    }

    public class SaveMenuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileSave = new JFileChooser();
            fileSave.showSaveDialog(theFrame);
            saveFile(fileSave.getSelectedFile());
        }
    }

    private void saveFile(File file) {
        boolean[] melodyToSave = new boolean[256];
        for (int i = 0; i < 256; i++) {
            JCheckBox check = (JCheckBox) checkboxList.get(i);
            if (check.isSelected()) {
                melodyToSave[i] = true;
            }
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    bw.write(String.valueOf(melodyToSave[j + (16 * i)]));
                    if (j != 15) {
                        bw.write(";");
                    }
                }
                if (i != 15) {
                    bw.write("\n");
                }
            }
            bw.close();
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm")) + " melody has been saved to file " + file);
        } catch (Exception e) {
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm")) + " couldn't save the melody to file");
        }
    }

    public class LoadMenuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileLoad = new JFileChooser();
            fileLoad.showOpenDialog(theFrame);
            loadFile(fileLoad.getSelectedFile());
        }
    }

    private void loadFile(File file) {
        boolean[] arrayToLoad = new boolean[256];
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            String[] flags;
            for (int i = 0; i < 16; i++) {
                line = br.readLine();
                flags = line.split(";");
                for (int j = 0; j < 16; j++) {
                    arrayToLoad[j + (16 * i)] = Boolean.parseBoolean(flags[j]);
                }
            }
            changeSequence(arrayToLoad);
            sequencer.stop();
            buildTrackAndStart();
            br.close();
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm")) + " melody has been loaded from file " + file);
        } catch (Exception e) {
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm")) + " couldn't load the melody from " + file);
        }
    }

    public class ConnectMenuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (sock == null) {
                userName = JOptionPane.showInputDialog(theFrame, "Введите Ваше имя:");
                serverIp = JOptionPane.showInputDialog(theFrame, "Введите IP-адрес сервера:");
                setupNetwork();
            } else {
                JOptionPane.showMessageDialog(theFrame, "Соединение уже установлено");
            }
        }
    }

    public class DisconnectMenuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (sock != null) {
                try {
                    in.close();
                    out.close();
                    sock = null;
                    remote.interrupt();
                } catch (IOException ioe) {

                }
            } else {
                JOptionPane.showMessageDialog(theFrame, "Нет активного соединения");
            }
        }
    }

    public class RemoteReader implements Runnable {
        Message msg = null;

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    while ((msg = (Message) in.readObject()) != null) {
                        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm")) + " got an message from " + msg.getSenderName());
                        otherSeqsMap.put(msg.toString(), msg.getSenderMelody());
                        messages.add(msg);
                        incomingList.setListData(messages);
                    }
                }

            } catch (SocketException se) {
                try {
                    in.close();
                    out.close();
                    sock = null;
                    System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm")) + " you have been disconnected from server " + serverIp);
                } catch (IOException e) {

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

