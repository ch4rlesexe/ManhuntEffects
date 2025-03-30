# ManhuntEffects

ManhuntEffects is a Minecraft 1.21 plugin designed for Manhunt-style gameplay. When a player logs in for the first time (or changes dimensions), they are prompted with a custom GUI to choose permanent abilities that persist even after death. Abilities differ by dimension and certain effects (like haste) can stack additively.

---

## Features

- **Custom Ability Selection GUI:**  
  Players are presented with a graphical menu on first login or when entering a new dimension. The options per dimension are:
  - **Overworld:**  
    - Jump Boost  
    - Haste +1 
    - Unlimited Stamina
  - **Nether:**  
    - Fire Resistance  
    - Haste +1 
    - Health Increase (+5 Hearts, raising max health from 20 to 30)
  - **The End:**  
    - Speed I  
    - Resistance I  
    - No fall damage

- **Persistent Effects:**  
  Abilities chosen are applied permanently across sessions, respawns, and dimension changes.

- **Additive Haste Effects:**  
  If a player selects Haste I in the Overworld and Haste II in the Nether, the effects are combined (resulting in a higher haste level).

- **Health Adjustment:**  
  The health increase ability directly sets the player's maximum health to 30. When reset, it reverts back to the default 20.

- **Reset Command:**  
  Use `/resetabilities` to clear your abilities and effects, or `/resetabilities <username>` to reset another player's abilities (with proper permissions).

---

## Installation

1. **Download or Clone the Repository:**
   ```bash
   git clone https://github.com/ch4rlesexe/ManhuntEffects.git
   ```
2. **Build the Plugin:**
   - Use Maven or Gradle to compile and package the plugin into a JAR file.
3. **Deploy:**
   - Place the resulting JAR file into your server's `plugins` folder.
   - Restart or reload your server.

---

## Usage

- **Ability Selection:**  
  On first login or upon entering a new dimension, the plugin automatically opens a GUI prompting you to choose an ability for that dimension.

- **Reset Abilities:**  
  - To reset your abilities, use:
    ```
    /resetabilities
    ```
  - To reset another player's abilities (requires `manhunteffects.reset.others` permission), use:
    ```
    /resetabilities <username>
    ```

- **Default Heart Reset:**  
  If your max health is altered by the plugin, resetting will return your max health to the default value (20).

---

## Permissions

- `manhunteffects.reset` – Allows a player to reset their own abilities.
- `manhunteffects.reset.others` – Allows a player to reset another player's abilities.

---

## Compatibility

- **Minecraft Version:** Designed for Minecraft 1.21.
- **Server Type:** Works with Paper/Spigot servers.

---

## Contributing

Contributions are welcome! Feel free to fork the repository and submit pull requests for bug fixes or new features.

---

Feel free to reach out or open an issue if you encounter any problems or have suggestions for improvements. Enjoy your enhanced Manhunt experience!
