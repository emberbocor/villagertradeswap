# Villager Trade Swap

Don't like one of a villager's trades? Swap just that one.

Villager Trade Swap adds a small button next to every trade in the vanilla trading screen. Clicking it replaces that single trade with a new one from the vanilla trade pool, while every other trade, the villager's level, its experience and your reputation stay exactly as they were. Each swap costs one emerald by default; the price and the item you pay with can be changed in the server config.

Available for NeoForge, Forge and Fabric.

## Features

- **One trade at a time.** Only the trade you click changes. No job site block breaking, no resetting the whole villager, no losing a good trade to get another one.
- **Works on traded offers too.** Any unlocked trade can be swapped, whether you have used it before or not.
- **Same level, vanilla pool.** The new trade is rolled from the vanilla trade pool of the villager's profession at the level that trade was unlocked at, so a Master trade stays a Master trade. Supports the Villager Trade Rebalance experiment.
- **Always something new.** A swap never gives you the trade you just swapped away, and never a trade identical to another trade of the same villager, at any level. Identical means the same items bought and sold with the same enchantment, potion or map destination; amounts and prices do not count, so a Mending book for 10 emeralds and one for 25 are the same trade.
- **Two of a kind, if you like.** By default a level can end up with two trades of the same kind as long as they differ, for example two enchanted books with different enchantments or two explorer maps to different structures. Server owners who prefer one trade of each kind per level can turn that off.
- **Fair cost, your currency.** Each swap costs 1 emerald from your inventory by default. The amount and the item (any item, also from other mods) are set in the server config; Creative is always free. You only pay when the swap succeeds. If there is no valid trade left for that slot, nothing is charged and you get a short message.
- **Your prices.** The new trade immediately shows the price you pay, including reputation and Hero of the Village discounts.
- **Cartographer friendly.** Explorer map searches are cached per area, and a map is only searched for when its destination is allowed, so swapping a cartographer's map trades again and again does not lag the server.
- **Out of the way.** The buttons float on the left side of the trading screen, follow the list when you scroll, and can be hidden with a small toggle next to the "Trades" label.
- **Survives zombification.** The mod remembers which level each trade belongs to, and keeps that through zombification and curing.
- **Existing worlds.** Villagers that existed before the mod was installed can be swapped right away; the level of each trade is worked out from its position in the list.
- **Safe to remove.** Without the mod, villagers keep their current trades and behave like vanilla again.

Only regular villagers can be swapped: not wandering traders, baby villagers, nitwits or unemployed villagers.

## Requirements

| | NeoForge | Forge | Fabric |
|---|---|---|---|
| Minecraft | 1.21.1 | 1.21.1 | 1.21 or 1.21.1 |
| Loader | NeoForge 21.1.1 or newer | Forge 52.0.0 or newer | Fabric Loader 0.15.11 or newer |
| Other | | | Fabric API 0.102.0 or newer |
| Jar | `villagertradeswap-neoforge-1.21.1-<version>.jar` | `villagertradeswap-forge-1.21.1-<version>.jar` | `villagertradeswap-fabric-1.21-1.21.1-<version>.jar` |

On Minecraft 1.21.1, NeoForge and Forge are separate loaders that do not run each other's mods, so each has its own jar. Pick the jar that matches your loader.

The Fabric jar bundles [NightConfig](https://github.com/TheElectronWill/night-config) (core and toml, LGPL-3.0) to read the TOML config. NeoForge and Forge already ship it.

### Client and server

The server decides the price and tells the client when a villager's trading screen opens. The swap buttons only appear in a villager's trading screen on a server that runs the mod.

- **NeoForge and Forge:** the mod is required on both the client and the server. Players without it cannot join a server that has it; the connection is refused at login.
- **Fabric:** install it on both sides to swap trades. Players without the mod can still join a server that has it and trade normally, they just do not get the swap buttons. A client with the mod on a server without it also works; the buttons simply do not appear.

### Updating from 1.0.0

Version 1.1.0 changed what the server sends to the client, so the client and the server must both run 1.1.0.

- **NeoForge and Forge:** a 1.0.0 client cannot join a 1.1.0 server and the other way round; the disconnect screen names Villager Trade Swap and the channel version that does not match.
- **Fabric:** the two versions do not see each other's mod. The player can join and trade normally, but gets no swap buttons until both sides run 1.1.0.

The old `config/villagertradeswap.json` is replaced by `config/villagertradeswap-server.toml` (see below). Your `rerollCost` is taken over automatically the first time 1.1.0 starts.

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

## Compatibility notes

- **Villager Trade Peek:** works together. Swap buttons appear only on unlocked trades, never on the greyed-out locked rows Trade Peek adds below them. Swapping does not change the previewed future trades, and the trades Trade Peek unlocks on level-up are recognized as trades of that level.
- **Trade Cycling and similar mods:** works together. When such a mod rerolls all of a villager's trades, the new trades are recorded as trades of the villager's current level.
- Trades added by other mods through the standard villager trade event are part of the pool automatically.
- Mods that change how many trades a villager gets per level, or that add trades in unusual ways, still work; when the mod cannot tell which level such a trade came from, it assumes two trades per level in list order.

## Building from source

```
./gradlew build
```

The jars are written to `neoforge/build/libs`, `forge/build/libs` and `fabric/build/libs`. The project follows the [MultiLoader-Template](https://github.com/jaredlll08/MultiLoader-Template) layout: shared code lives in `common`, loader-specific code in `neoforge`, `forge` and `fabric`.

## License

MIT. See [LICENSE](LICENSE). The Fabric jar includes NightConfig, which is licensed under the LGPL-3.0.
