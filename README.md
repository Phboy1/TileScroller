# It's About Time

Welcome to "It's About Time", a tile-based puzzle platformer rewinding game.

This project was created for #Horizons.

# What is it?

The mechanic is simple. You move through a level, hit a problem, rewind to an earlier point, and leave a ghost copy of yourself there. Now you have two bodies. Do it again and you have three. Every life that you once had, is there with you and can attack, press buttons, and help you as you go. Ghosts will move exactly how they moved in the past and are buffered with no movement if you gain more time thorugh the shop.

# How It Works

Levels are full of obstacles. Lava kills you. Enemies hunt you with pathfinding. Doors need buttons to be pressed. The shop sells time, better coin drops, and more ghosts. Everything in the level can kill your ghost bodies just like it kills you, so every decision about where to leave a ghost matters. Some buttons open multiple doors, some doors are timer doors, and some can only be pressed my manuvering enemies to press the buttons for you.

## The Rewind System
Every frame, the game records the offset of the tiles and the offset of the actual player. This gets stored in a Movement object that holds cameraX, cameraY, playerX, playerY, and direction the player was facing. All these Movement objects go into a list that keeps growing as the game runs. In Culminating.java I store a 2D array of Movement. 1 direction for all the ghosts that I might have, and 1 direction for the movement in one ghost life.

## Player Movement

The game uses a tile-scrolling system where the world moves around the player. Movement is handled through world offsets and collision checks rather than moving the player directly through the map. Every movement is recorded as part of the rewind system, allowing ghost bodies to perfectly recreate previous actions. Additionally, the player has a pushout feature in the doors meaning the player will get pushed out of the door when a door is shut and they are inside of the door causing collision not to break.

## Collision System

Collision is tile-based. Before movement occurs, the game checks nearby tiles to determine whether the destination is valid. Walls block movement, while special tiles such as lava, buttons, doors, and teleporters trigger interactions. Ghosts use the same collision system as the player, allowing them to interact with the world naturally. Weirdly, there is a buffer of 2 * CAMERA_SPEED and I frankly don't know why, I just did some testing and found that that number fits best.

## Enemy System

Enemies use pathfinding to navigate the map and hunt the player. They try to take the shortest path directly to the player using atan2 and some trigonometry to find the best angle to go towards the player. If the path is blocked it will try to move up down left or right to match the x or y value of the player. Enemies can be pushed out of doors incase they get stuck.

# Motivation

I wanted a game where my past could affect my future, and I could constantly escape a dungeon.

# Controls

WASD or arrow keys to move. Space to attack. E to interact with buttons. Q for the shop. R to rewind. Click a ghost to take control of that body.

# Screenshots from the Game

<img width="697" height="653" alt="Screenshot 2026-06-13 165902" src="https://github.com/user-attachments/assets/a5fd5f8c-80a6-460f-bd83-9ecfeab78a23" />
<img width="537" height="347" alt="Screenshot 2026-06-13 165731" src="https://github.com/user-attachments/assets/f3fba592-2678-4f67-8841-a350ab1ac32c" />
<img width="672" height="432" alt="Screenshot 2026-06-13 165535" src="https://github.com/user-attachments/assets/2e5c1a31-28bd-4bb9-9863-d3ffd03495af" />
<img width="685" height="461" alt="Screenshot 2026-06-13 165445" src="https://github.com/user-attachments/assets/b689f7ce-88ea-4a28-afb7-5b2088b1bdee" />
<img width="840" height="383" alt="Screenshot 2026-06-13 165354" src="https://github.com/user-attachments/assets/15548acf-364a-4840-bef4-4ac95a44ac48" />

