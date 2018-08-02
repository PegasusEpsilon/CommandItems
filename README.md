# CommandItems
Voodoo Magic for Minecraft 1.12.2 server admins and map makers.

Basically, lets you add NBT Data to items, so that you can trigger sequences of commands when you left-and-right-click
them on blocks, entities, or thin air. It's already capable of a lot, but it's not capable of everything I want, yet.

Have some debug items:

```give @p diamond_sword 1 0 {commandItem:{useCommands:{ENTITY:["say sword useCommands ENTITY $TARGET$"],BLOCK:["say sword useCommands BLOCK $TARGET$"],MISS:["say sword useCommands MISS"]},digCommands:{ENTITY:["say sword digCommands ENTITY $TARGET$"],BLOCK:["say sword digCommands BLOCK $TARGET$"],MISS:["say sword digCommands MISS"]}}}```

```give @p diamond_axe 1 0 {commandItem:{useCommands:{ENTITY:["say axe useCommands ENTITY $TARGET$"],BLOCK:["say axe useCommands BLOCK $TARGET$"],MISS:["say axe useCommands MISS"]},digCommands:{ENTITY:["say axe digCommands ENTITY $TARGET$"],BLOCK:["say axe digCommands BLOCK $TARGET$"],MISS:["say axe digCommands MISS"]}}}```
