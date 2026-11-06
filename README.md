# Emberfall Java RPG

A Chinese-language fantasy RPG built with Java Swing. Explore the Mistbound Frontier, encounter monsters, earn gold, recruit allies, and prepare for turn-based battles.

All visuals and audio are drawn or synthesized by the game at runtime; no external asset files are required.

## Requirements

- JDK 8 or later

## Run

From this directory, compile and launch the game:

```powershell
javac -encoding UTF-8 Main.java
java Main
```

The game opens as a desktop window. Enter a character name to begin; if left blank, the default name is `勇者`.

## Features

- Dark-fantasy home screen and custom-painted battle scenes
- Animated exploration scenes for treasure, allies, quiet encounters, and monsters
- Animated treasure coins and a waving, arriving ally
- Turn-based party combat: allies attack alongside the hero
- Melee and ranged combat, plus level-unlocked special skills
- Experience, levels, stat growth, and a level-gated final boss
- A custom equipment shop with several swords, a bow, a staff, armor, and healing
- Turn-based combat with animated attacks, health bars, and battle log
- Synthesized battle sound effects and looping background music, with a music toggle
- Dedicated animated victory and defeat screens
- Custom character-creation screen
- Random exploration encounters, allies, and gold
- Defeat monsters to earn experience; unlock Flame Slash at level 3 and Arcane Arrow at level 5
- Challenge the Demon King once you reach level 5

The game generates music and sound effects through the Java Sound API. If the computer has no available audio output, the game displays a warning and remains playable.
