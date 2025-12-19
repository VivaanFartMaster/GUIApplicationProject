Project Name: Bullet Bonanza

Overview:
Bullet Bonanza is a 2D arcade-style shooter game made using Java Swing. It has an object oriented design along with user authentication and entity management. 

What the project does:
The core gameplay is built off the player’s survival. The player controls a blue bar at the bottom of the screen using left and right arrow keys. The player may also shoot a “bullet” upward by pressing the spacebar key.
The game starts with the score at 0, player at 5/5 lives, and the player can gain points by methods outlined below. Lives may also be replenished at the cost of score. 

Enemies:
- Red squares: These must be shot with a bullet. If they hit the player or the bottom of the screen, the player loses a life.
- Blue squares: If the player touches these, they gain a point. If the player shoots it, then the player loses a life. 
- Orange squares: These launch at a diagonal and bounce off walls. They gain 3 points by clicking them directly and 1 point if shot at. They also make the player lose a life if it makes contact with a player. 

System features:
User authentication: There is a fully functioning login/register screen that saves data to a file, allowing for data persistence. The file “users.txt” saves username, password, and high score.
Game speed: The speed of the game increases as the player gains points, with an increase of 10% (of the original game speed) with every 5 points gained.
Visual Feedback: The screen flashes different colors (Green, Red, Cyan) based on events like damage or power-ups.

Project goals/purpose:
This was created as the GUI Application Project for ISC4U. The project aims to show proficiency in Java swing and ability to modify existing code created by AI. 
The following topics are covered:
Using Inheritance (abstract GameEntity class), Polymorphism, and Encapsulation.
Manage State: Handling complex game states (Start, Playing, Paused, Game Over) and user states (Logged In/Out).
Handle Input: Processing Keyboard (movement/shooting) and Mouse (interaction) events simultaneously.
File I/O: Reading and writing user data to local storage to maintain persistence across sessions.


Installation and setup:
Java version 8 or higher must be installed along with a text editor/IDE to run the code.
Create a folder to house the files, and implement all files shown in the project. Run “BulletBonanza.java”.

What was changed from the original AI driven code?
The initial code used a single class “BulletBonanza” along with GamePanel to house all the code. The new code separates everything into distinct files, allowing for easier editing.
Basic fixes: Much text was overlapping with other text and also blending into the background, simply changing the colour from black to white and changing coordinates quickly fixed this.
New Enemy Type: The initial code only had falling Red/Blue squares. The new code adds a Bouncing Enemy (Orange) that bounces off walls and requires a mouse click to destroy (adding a new mechanic).
CardLayout Navigation: The new code uses a CardLayout manager. This acts like a deck of cards, allowing the window to flip between the "Login Screen" and the "Game Screen" without opening new windows.
GridBagLayout is used for the login page to have centered and resizable objects. 
Login page and data persistence: A page to create an account and login to a pre-existing one was made. Along with saving usernames, passwords, and high scores.
A hashmap is used to save data while the code is running, mapping the username as a key and the user object as the value. This ensures quick lookup of if a username already exists.

Initial Code: Used ArrayList<Rectangle> or a simple Enemy class with an Enum to check if it was RED or BLUE. It used if statements to decide how things moved.
New Code: Uses Polymorphism.
There is a parent class GameEntity.
Bullet, FallingEnemy, and BouncingEnemy all inherit from it. 

How the program meets requirements:
Collision detection: The bullet interacting with any enemy type as well as the enemies interacting directly with the player. The bullet deletes the enemies and causes the player’s colours to change for example.
Buttons/mouse events: There are multiple things to click: A pause button, a button to buy lives, and the orange enemy. A space bar is used to fire a bullet as well.
Abstract Classes/Inheritance:
The Abstract Class (GameEntity):
It defines the common properties: x, y, width, height, color, speed.
It defines the abstract method: public abstract void move(). This forces every child to invent its own way of moving.


GUI Design + Files I/O: 
GridBagLayout is used to center the login box perfectly. The simplistic theme is easy to follow. All information is displayed in a non cluttered and easy to read way.
UserManager.java allows for file I/O. It reads users.txt and transfers the data into a hashmap. The high score is updated when applicable and updates the users.txt file. 



