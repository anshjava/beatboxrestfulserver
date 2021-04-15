# beatboxrestfulserver
BeatBox RESTful Server

Client-Server chat.
Server is on Spring Boot RESTful.
Client is on Swing gui.
You can send messages and create midi melodies together.
The main idea got from book "Head First Java" by Kathy Sierra and Bert Bates. And lot's of functions were added.

Client creates Message object, contained Name, LocalDateTime, Text message and Melody encoded in bolean[]. To work with sound i used javax.sound.midi.
Client can pack his message to JSON and send his it to server end-point. Server receives message, decode it to POJO and puts to list.
When any client send GET response to endpoint /getchat - server returned list of all messages in JSON.

Also client can save and load beautiful melodies to *txt file on your localdrive.


