Key:
**ACK: acknowledgement** NAK: negative-acknowledgement

```

1) QUIT GAME
Client: 
- request
  - command: quit

Server:
- response
  - id: ACK
  - request: 
    - command: quit  // copy of request xml

2) CREATE GAME
Client: 
- request
  - command: create_game
  - game
    - name: "String"

Server:
a) OK
- response
  - id: ACK
  - req... // copy of request xml

b) Game exists
- response
  - id: NAK
  - message: This game...
  - request:
    - command: create_game     ... // copy of request xml

c) etc.

```