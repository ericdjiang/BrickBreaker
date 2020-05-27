# Contributor: Eric Jiang (edj9)
This game was a single-person project. Eric Jiang was solely responsible for the design and implementation of the project. After studying online tutorials on JavaFX, Eric planned and programmed a solution in code. Upon completion, Eric was also responsible for testing, bug fixes, and documentation.
# Design Goals
A primary goal of this project was to ensure modularity by breaking down the game into multiple class components. For this reason, I felt it was important to separate the visible nodes on screen (e.g. bricks, power ups, bouncers, etc.) into individual classes. Let us examine the example of bouncers, for instance. While the original breakout game only features one bouncer, my game was capable of handling multiple bouncers. This proved surprisingly simple given that adding a bouncer to the field of play simply meant initializing a new Bouncer object via my `initBouncer()` method. Likewise, utilizing a separate Paddle class allowed me to initialize and handle two Paddles at the same time quite easily, as both paddles had the exact same game logic (collision detection, etc).

Furthermore, I wanted to make the addition of new levels very easy; rather than having one text file specifying the layout for one level, I had the layouts for all the levels stored in one text file separated by a “-“ character. When reading in the layouts, my program was agnostic to how many levels there were, simply appending each new level to an ArrayList containing the layouts. Thus, if the programmer were to want to add 50 more new levels, only the layout file would need to be changed.
# Project Design
In this project, each node visible on screen (e.g. any game objects are text) is an instance of a particular class. Each game node has class-specific methods such as decrementing lives and checking for collisions between objects; for instance, bouncers, paddles, power ups, and bricks each have their own individual methods which are called in Main. The Main class serves as the central hub that declares, initializes, and stores the stage, scene, and all the aforementioned nodes. The Main serves as the controller, running a timeline which checks for collisions, removes dead nodes, and rerenders the screen multiple times each second to reflect these game changes.
# Assumptions
One of the major assumptions of the games is that each level will consist of a 3x6 grid of blocks. When reading in a layout file, the LayoutParser class expects that each level will have 3 rows and 6 columns, and each string block will be separated with a “-“ character. If for instance, one of the rows in the layout file contains 5 characters rather than 6, the LayoutParser will crash. 

Another assumption is that Bricks will have strengths ranging from 0-3. This assumption is relatively benign, as it only affects the visible color of the Brick object. My code can handle a Brick of any strength, but any strengths above 3 will default to a blue color; there are only colors corresponding to strengths 1-3.
# Adding New Features
All features which I intended on adding to the project as specified in the PLAN were successfully implemented. Due to the flexibility of the LayoutParser class, it is simple to remove or add new levels; simply add a 3x6 ASCII wordart representation where each "cell" consists of a strength from 1-3 and an optional character corresponding to a powerup. No code in main needs to be changed in order for the newly added level to appear, as the game can initialize accept any number of levels/brick layouts. In addition, to add a new cheat code, simply edit the handleKeyInput class in Main.java to listen for a specific keypress, then call the corresponding method for the powerup. Finally, to create an entirely new powerup, the user need only edit two parts of the Main class. First, the user must define a unique single character string (which will represent the power up in the brick layout file) which maps to this power up as a global instance variable. Then, in the handlePowerUp method, simply map this unique mapping key to a method which executes the desired power up.   