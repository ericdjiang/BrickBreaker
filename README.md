game
====

This project implements the game of Breakout.

Name: Eric Jiang (edj9)

### Timeline

Start Date: 01/13/2020

Finish Date: 01/20/2020

Hours Spent: 45

### Running the Program

Main class: Run Main.java

Data files needed: Brick layouts for all 3 levels are located in a single file called 'brick_layouts.txt' in resources folder.

Key/Mouse inputs: On level startup, left click mouse to launch the bouncer. The paddle will translate to the x location of your mouse cursor.

Cheat keys: 
* 1, 2, 3 correspond to the levels 1-3
* L: increase lives by 1
* R: reset paddle and bouncer positions and pause game
* A: widen paddles
* B: slow down bouncers
* C: create and launch additional bouncer
* D: enable lasers
* E: speed up bouncers

Known Bugs:
Extremely rarely (this has happened only once), a bouncer may become stuck within a brick.

Extra credit:
There are two paddles, both of which can be used to hit the bouncer. There are power ups which allow lasers and extra balls to be shot.

### Notes/Assumptions
I assumed that there would be a single text file specifying the location of all bricks/powerups. Thus, I assumed that each level's layout would contain a 3x6 grid of space-separated strings, where each string contained a number corresponding to the brick strength and an additional optional character corresponding to the powerup. I also assumed that each level layout be separated by a "-" character in the text file.

### Impressions
I felt this was a solid introductory project. It really helped me sharpen up my Java skills, reminding me of public/private, static variables, and object-oriented-programming. However, more direction with regards to how to set up levels would have been helpful. I spent a lot of time struggling with how best to modularize my code and whether or not my approach to adding/removing objects from the scene could be done more cleanly.
