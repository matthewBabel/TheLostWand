# TheLostWand
A 2D arcade style game developed in Java. Uses a object oriented design pattern. Everything from collision detection to linear interpolation is built entirely in Java without external libraries. The main game loop is located in the GamePanel class, which works with the Game Objects. 
Everything located in the Collision sub-folder is a collidable object besides the collision detector. A collidable object is an abstrct class used by the collision detector. A collidable object can return a collision mask of type polygon which is what the collision detector uses. 
