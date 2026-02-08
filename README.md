# Ping Offset Miner
### Fabric 1.21.10 Hypixel Skyblock
##### Command: /pom
A mod that takes into account ping offset when mining ores in Hypixel Skyblock, allowing for faster mining in high-ping clients.  
This mod displays a block overlay on the screen when they can safely move to the next ore, as the server has already registered the block as broken.

To use, set your current mining speed with `/pom speed <val>`, and your average ping with `/pom ping <val>`. POM will add a highlighting box on compatible ores that switches green when you can move to the next block.
POM can be toggled with `/pom active <true|false>`. You can unset values with `-1`.

>[!NOTE]
>Tuning your ping value empirically is recommended, as the calculation is imperfect for offset due to non-constant factors. Other factors such as Cold affect Mining Speed and therefore the accuracy of POM.
>
>This mod works best with Mithril, Gemstone, and Pure Ores.

###### Copyright (c) Lucas Bubner 2024, under MIT.
