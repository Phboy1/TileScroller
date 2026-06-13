# It's About Time

Welcome to "It's About Time", a tile-based puzzle platformer rewinding game.

This project was created for #Horizons.

# What is it?

The mechanic is simple. You move through a level, hit a problem, rewind to an earlier point, and leave a ghost copy of yourself there. Now you have two bodies. Do it again and you have three. The ghosts are solid. They can block hazards, stand on switches, and create paths you couldn't make by yourself. The catch is you can only keep so many ghosts active at once, and they're just as mortal as you are. One wrong move and a ghost dies in the lava or gets caught by an enemy. You have to be smart about where you place them.

# How It Works

Levels are full of obstacles. Lava kills you. Enemies hunt you with pathfinding. Doors need keys. The shop sells temporary power-ups. Everything in the level can kill your ghost bodies just like it kills you, so every decision about where to leave a ghost matters. You learn this through the levels themselves. Early ones teach you the mechanic. Later ones combine ghosts with enemies, multiple keys, and timing that requires coordination across multiple bodies at once.

## The Rewind System
Every frame, the game records the offset of the tiles and the offset of the actual player. This gets stored in a Movement object that holds cameraX, camerY, playerX, playerY, and direction the player was facing. All these Movement objects go into a list that keeps growing as the game runs.

## Player Movement

The game uses a tile-scrolling system where the world moves around the player. Movement is handled through world offsets and collision checks rather than moving the player directly through the map. Every movement is recorded as part of the rewind system, allowing ghost bodies to perfectly recreate previous actions.

## Collision System

Collision is tile-based. Before movement occurs, the game checks nearby tiles to determine whether the destination is valid. Walls block movement, while special tiles such as lava, buttons, doors, and teleporters trigger interactions. Ghosts use the same collision system as the player, allowing them to interact with the world naturally.

## Enemy System

Enemies use pathfinding to navigate the map and hunt the player. They intelligently move around obstacles and attempt to find the shortest path to their target. Enemies can attack both players and ghosts, making them an important part of both the platforming and puzzle-solving experience.

# Motivation

I wanted a game where my past could affect my future, and I could constantly escape a dungeon.

# Controls

WASD or arrow keys to move. Space to attack. E to interact with buttons. Q for the shop. R to rewind. Click a ghost to take control of that body.
