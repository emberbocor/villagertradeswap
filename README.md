# Villager Trade Swap

Don't like one of a villager's trades? Swap just that one.

Villager Trade Swap adds a small button next to every trade in the vanilla trading screen. Clicking it replaces that single trade with a new one from the villager's trade pool, while every other trade, the villager's level, its experience and your reputation stay exactly as they were. Each swap costs one emerald by default; the price and the item you pay with can be changed in the server config.

Available for NeoForge, Forge and Fabric on Minecraft 26.2. This is the 26.2 version; Minecraft 26.1-26.1.2, 1.21.11, 1.21.1 and 1.20.1 are supported by separate sets of jars.

## Features

- **One trade at a time.** Only the trade you click changes. No job site block breaking, no resetting the whole villager, no losing a good trade to get another one.
- **Works on traded offers too.** Any unlocked trade can be swapped, whether you have used it before or not.
- **Same level, same pool.** The new trade is rolled from the trade set of the villager's profession at the level that trade was unlocked at, so a Master trade stays a Master trade. Villager trades are data driven since Minecraft 26.1, so the pool is whatever the game uses for that level: trades that only some villager types get (such as the explorer maps of each biome), trades that datapacks or mods add to the villager trade tags or trade sets, and the Villager Trade Rebalance experiment.
- **Always something new.** A swap never gives you the trade you just swapped away, and never a trade identical to another trade of the same villager, at any level. Identical means the same items bought and sold with the same enchantment, potion or map destination; amounts and prices do not count, so a Mending book for 10 emeralds and one for 25 are the same trade.
- **Two of a kind, if you like.** By default a level can end up with two trades of the same kind as long as they differ, for example two enchanted books with different enchantments or two explorer maps to different structures. Server owners who prefer one trade of each kind per level can turn that off.
- **Fair cost, your currency.** Each swap costs 1 emerald from your inventory by default. The amount and the item (any item, also from other mods) are set in the server config; Creative is always free. You only pay when the swap succeeds. If there is no valid trade left for that slot, nothing is charged and you get a short message.
- **Your prices.** The new trade immediately shows the price you pay, including reputation and Hero of the Village discounts.
- **Cartographer friendly.** Explorer map searches are cached per area, and a map is only searched for when its destination is allowed, so swapping a cartographer's map trades again and again does not lag the server, and a map you do not get never uses up a structure. This works for every explorer map trade, also from datapacks and mods.
- **Out of the way.** The buttons float on the left side of the trading screen, follow the list when you scroll, and can be hidden with a small toggle next to the "Trades" label.
- **Survives zombification.** The mod remembers which level each trade belongs to, and keeps that through zombification and curing.
- **Existing worlds.** Villagers that existed before the mod was installed can be swapped right away; the level of each trade is worked out from its position in the list.
- **Safe to remove.** Without the mod, villagers keep their current trades and behave like vanilla again.

Only regular villagers can be swapped: not wandering traders, baby villagers, nitwits or unemployed villagers.

## Requirements

| | NeoForge | Forge | Fabric |
|---|---|---|---|
| Minecraft | 26.2 | 26.2 | 26.2 |
| Java | 25 | 25 | 25 |
| Loader | NeoForge 26.2.0.0-beta or newer | Forge 65.0.0 or newer | Fabric Loader 0.18.4 or newer |
| Other | | | Fabric API 0.152.1 or newer |
| Jar | `villagertradeswap-neoforge-26.2-<version>.jar` | `villagertradeswap-forge-26.2-<version>.jar` | `villagertradeswap-fabric-26.2-<version>.jar` |

Minecraft 26.x needs Java 25. The first stable NeoForge build for 26.2 is 26.2.0.57; the earlier beta builds work too. Forge uses the 65.x series for 26.2.

NeoForge and Forge are separate loaders that do not run each other's mods, so each has its own jar. Pick the jar that matches your loader.

The Fabric jar bundles [NightConfig](https://github.com/TheElectronWill/night-config) (core and toml, LGPL-3.0) to read the TOML config. NeoForge and Forge already ship it.

### Client and server

The server decides the price and tells the client when a villager's trading screen opens. The swap buttons only appear in a villager's trading screen on a server that runs the mod.

- **NeoForge and Forge:** the mod is required on both the client and the server. Players without it cannot join a server that has it; the connection is refused at login.
- **Fabric:** install it on both sides to swap trades. Players without the mod can still join a server that has it and trade normally, they just do not get the swap buttons. A client with the mod on a server without it also works; the buttons simply do not appear.

### Coming from an older Minecraft version

The 26.2 version is 1.1.0 from the start; a client of another Minecraft version cannot join a 26.2 server anyway. If the old `config/villagertradeswap.json` of 1.0.0 is still there, its `rerollCost` is taken over into `config/villagertradeswap-server.toml` the first time the mod starts (see below). A `villagertradeswap-server.toml` from the 26.1, 1.21.11 or 1.21.1 version has the same format and is used as it is.

## Configuration

### Server: `config/villagertradeswap-server.toml`

The file is created with comments on the first server start and read every time a world or server starts (in singleplayer: when the world is opened). The options and their defaults:

```toml
# Price of one trade swap, paid with the rerollCurrency item. Whole number from 0 to 64; 0 makes
# swapping free. Players in Creative mode never pay, and nothing is taken when a swap fails.
rerollCost = 1

# Item that pays for a trade swap, as an item id such as "minecraft:emerald" or "minecraft:diamond".
# Items from other mods work too; item tags (#...) do not. Every item of this type counts, whatever
# its name, enchantments or other data. It is taken from the main inventory and the hotbar, never
# from the trade's payment slots.
rerollCurrency = "minecraft:emerald"

# A swap never gives a trade identical to the one being swapped or to any other trade of the same
# villager. Identical means the same items bought and sold with the same enchantments, potion or
# map destination; amounts and prices do not matter.
# true: a swap may give a trade of the same kind (the same items bought and sold) as another trade
# of that level, as long as it is not identical, for example a second enchanted book with another
# enchantment or a second explorer map to another structure.
# false: a level never holds two trades of the same kind.
allowSameTradeInLevel = true
```

- `rerollCost`: how many currency items one swap costs, a whole number from 0 to 64. `0` makes swapping free.
- `rerollCurrency`: the item id of the currency, for example `"minecraft:diamond"` or an item from another mod. The swap buttons show the price with the item's name, such as "3× Diamond".
- `allowSameTradeInLevel`: `true` (default) lets a level hold two trades of the same kind as long as they are not identical; `false` keeps one trade of each kind per level.

Your own values and comments are never rewritten. Options missing from the file (for example after an update adds a new one) are added at the end of the file with their comment and default value. An invalid value, an unknown item or `minecraft:air` falls back to the default with a warning in the log. A file that cannot be read as TOML is left untouched; the defaults are used and the log says what is wrong.

**From 1.0.0:** when the TOML file does not exist yet but the old `config/villagertradeswap.json` does, its `rerollCost` is copied into the new file. The JSON file is left in place but no longer used; the log says it can be deleted.

### Client: `config/villagertradeswap-client.json`

```json
{
  "showRerollButtons": true
}
```

- `showRerollButtons`: whether the swap buttons are shown. This is the toggle next to the "Trades" label, saved automatically.

## Upgrading a world from Minecraft 26.1

Swapped trades are ordinary villager trades, so the game upgrades them like any other trade. The level the mod recorded for each trade is stored on the villager (and on zombie villagers) under the same key in both versions, and the game keeps it during the upgrade, so swapping keeps using the level each trade was unlocked at, also for villagers that are cured after the upgrade.

The recorded levels are only used when their number matches the villager's trades. If it does not (for example because a trade could not be upgraded, or another mod changed the trades), the mod falls back to working out the levels from the order of the trades, two per level, as it does for villagers that existed before the mod was installed. Nothing breaks either way; at worst a swap rolls from the pool of a different level than the one the trade came from.

## Compatibility notes

- **Villager Trade Peek** (26.2 version): works together. Swap buttons appear only on unlocked trades, never on the greyed-out locked rows Trade Peek adds below them. Swapping does not change the previewed future trades, and the trades Trade Peek unlocks on level-up are recognized as trades of that level. Swaps use the villager's own random source, not the world's trade random sequences, so swapping does not shift what later level-ups or Trade Peek previews roll.
- **Villager Trade Labels** (26.2 version): works together. The labels follow the trade list, so a swapped trade shows the label of its new item.
- **Trade Cycling and similar mods:** works together. When such a mod rerolls all of a villager's trades, the new trades are recorded as trades of the villager's current level.
- Trades that datapacks or other mods add to the villager trade tags (such as `#minecraft:farmer/level_2`) or trade sets are part of the pool automatically. A level whose trade set is missing or empty cannot be swapped; nothing is charged.
- Mods that change how many trades a villager gets per level, or that add trades in unusual ways, still work; when the mod cannot tell which level such a trade came from, it assumes two trades per level in list order.

## Building from source

```
./gradlew build
```

The jars are written to `neoforge/build/libs`, `forge/build/libs` and `fabric/build/libs`. The project follows the [MultiLoader-Template](https://github.com/jaredlll08/MultiLoader-Template) layout: shared code lives in `common`, loader-specific code in `neoforge`, `forge` and `fabric`.

## License

MIT. See [LICENSE](LICENSE). The Fabric jar includes NightConfig, which is licensed under the LGPL-3.0.
