Side Effects
========
A **extremely lightweight** library for creating server-side mod that works for unmodded clients!

[![JitPack status](https://jitpack.io/v/moe.hertz/side-effects.svg)](https://jitpack.io/#moe.hertz/side-effects)
[![](https://jitci.com/gh/codehz-mcmods/side-effects/svg)](https://jitci.com/gh/codehz-mcmods/side-effects)

Add to project:
```groovy
repositories {
  // other repo
  maven { url "https://jitpack.io" }
}
dependencies {
  // other deps
  modImplementation "moe.hertz:side-effects:(version)"
}
```

## Non-Goal

1. Converting existing mods to server side.
2. Full replacement for similar mod [Polymer](https://modrinth.com/mod/polymer) or [PolyMc](https://github.com/TheEpicBlock/PolyMc) because I want to make it keeping lightweight.

## Status

Currently, only custom(fake) entity is supported.

Docs is **Coming Soon™**

Example usage with this library:

* [Vanilla Laser](https://modrinth.com/mod/vanilla-laser) [(github)](https://github.com/codehz-mcmods/vanilla-laser)
* [(unreleased) Bat Extensions (github)](https://github.com/codehz-mcmods/bat-ext)

Code example:

```java
public class BatTrader extends BatEntity implements IFakeEntity {
  public BatTrader(EntityType<? extends BatEntity> entityType, World world) {
    super(entityType, world);
  }

  @Override
  public EntityType<?> getFakeType() {
    return EntityType.BAT;
  }
}
```