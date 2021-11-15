Hi there,

This project is still in its early stages of development. (This readme is also in early stages so please excuse the tpyos...) 

## How to run the game:

Clone the repository:
```
git clone https://github.com/EliMLevy/Java-Multiplayer-Game.git
```
Change into the output directory:
```
cd Java-Multiplayer-Game/Fortschribung/FortschribungClient/bin
```
Compile the source code:
```
javac -d . ..\src\*.java
```
Run the game client and connect to a server:
```
java GameClient <Server IP address>
```
For testing purposes I have a server running at 159.223.153.157. Feel free to try out the game there.



## Here's what We have so far:

Server:

- The server code can be cloned and run on a hosting platform (I'm using digital ocean) and will await incoming websocket connections
- When two clients connect the server will send out important information for initializing the game. This info includes: the clients id, the location and id's of the starting worker for both players
- Even after the clients disconnect, the server continues waiting for incoming web socket connections

Client:

- I am using JFrame and Java's built in Graphics2D to create the game.
- When the game begins each player starts with 5 workers. 
- Each player can control his workers by selecting one (left click) and giving it a location to move to (right click). 
- These workers have no path finding abilities so if you want them to go around a corner you need to give them a path to follow by holding down SHIFT. 
- To make the game easier to play with a trackpad you can also press "X" to give a target to a worker and SHIFT+X works as expected.


## The vision:

Server:

- The server will serve the game map and starting conditions. 
- People can edit game server code to customize the map and starting conditions. Then they can host the server wherever they like and use the client to connect to it.

Client:

- The client will open with a welcome screen where you can choose to join a game on a pre-defined server or you can enter your own server's IP address.
- When two players join a server the game will begin
- Around the map there will be different objects that you can assign workers to
- Assigning a worker to a generator will create more workers after a specified time interval
- Assigning a worker to a tank will allow you to drive the tank around and destroy enemy workers
- At every specidfied time interval an orb will appear somewhere in the arena. To capture the orb you need to have a worker assigned to it for 20 seconds. 


## Challenges

- Learning how to use the Java Websocket framework. Oracle has a great tutorial where they go through server and client code for creating a web socket connection and sending and receiving information. Unfortunately, the example was simple enough that the client didn't have to do anything in between messages from the server. Therefore, the example read messages from the server with BufferedReader.readline(), a blocking method. This would not do for the game because if there is no message from the server, the client needs to move on to other tasks (like repainting the screen and awaiting user input). Thus, I needed to convert the BufferedReader that I generated from the output stream of the socket into a custom BufferedReader class where I could give the readline() method a timeout. I found a solution which, with several adjustments, solved this problem. The original solution can be found here: http://www.ostack.cn/?qa=630328/ 
- Keeping track of where on the screen to paint the workers. This problem arrises from the fact that the arena is larger than the screen and the client has the ability to drag the screen around to adjust their view. Therefore, the game simply needs to keep track of how far the screen was dragged and then paint the worker at its position - the screen offset. The solution can be used when assigning a target for the worker; simply subtract the offset from the current mouse position. The problem is compounded however when these positions need to be sent to the other client who has a different screen offset. Therefore, the workers need to keep track of their "absolute world position" so that it can be interpreted correctly by the other client.