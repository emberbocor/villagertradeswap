# Villager Trade Swap

Don't like one of a villager's trades? Swap just that one.

Villager Trade Swap adds a small button next to every trade in the vanilla trading screen. Clicking it replaces that single trade with a new one from the vanilla trade pool, while every other trade, the villager's level, its experience and your reputation stay exactly as they were. Each swap costs one emerald by default.

Available for Forge (also runs on NeoForge 1.20.1) and Fabric on Minecraft 1.20.1. This is the 1.20.1 version; Minecraft 1.21.1 is supported by a separate set of jars.

## Features

- **One trade at a time.** Only the trade you click changes. No job site block breaking, no resetting the whole villager, no losing a good trade to get another one.
- **Works on traded offers too.** Any unlocked trade can be swapped, whether you have used it before or not.
- **Same level, vanilla pool.** The new trade is rolled from the vanilla trade pool of the villager's profession at the level that trade was unlocked at, so a Master trade stays a Master trade.
- **No duplicates.** A swap never gives you a trade of the same kind (same items bought and sold) as another trade of that level. Getting the same kind back in the same slot is allowed, so an enchanted book can become a book with a different enchantment.
- **Fair cost.** Each swap costs emeralds from your inventory (1 by default, configurable, free in Creative). You only pay when the swap succeeds. If there is no valid trade left for that slot, nothing is charged and you get a short message.
- **Your prices.** The new trade immediately shows the price you pay, including reputation and Hero of the Village discounts.
- **Cartographer friendly.** Explorer map searches are cached per area, so swapping a cartographer's map trades again and again does not lag the server.
- **Out of the way.** The buttons float on the left side of the trading screen, follow the list when you scroll, and can be hidden with a small toggle next to the "Trades" label.
- **Survives zombification.** The mod remembers which level each trade belongs to, and keeps that through zombification and curing.
- **Existing worlds.** Villagers that existed before the mod was installed can be swapped right away; the level of each trade is worked out from its position in the list.
- **Safe to remove.** Without the mod, villagers keep their current trades and behave like vanilla again.

Only regular villagers can be swapped: not wandering traders, baby villagers, nitwits or unemployed villagers.

## Requirements

| | Forge / NeoForge | Fabric |
|---|---|---|
| Minecraft | 1.20.1 | 1.20 or 1.20.1 |
| Loader | Forge 47.1.0 or newer, or NeoForge 1.20.1 | Fabric Loader 0.14.21 or newer |
| Other | | Fabric API 0.83.0 or newer |
| Jar | `villagertradeswap-forge-1.20.1-<version>.jar` | `villagertradeswap-fabric-1.20-1.20.1-<version>.jar` |

NeoForge 1.20.1 still runs Forge mods, so it uses the Forge jar. There is no separate NeoForge jar for 1.20.1.

### Client and server

The server decides the cost and tells the client when a villager's trading screen opens. The swap buttons only appear in a villager's trading screen on a server that runs the mod.

- **Forge and NeoForge:** the mod is required on both the client and the server. Players without it cannot join a server that has it; the connection is refused at login.
- **Fabric:** install it on both sides to swap trades. Players without the mod can still join a server that has it and trade normally, they just do not get the swap buttons. A client with the mod on a server without it also works; the buttons simply do not appear.

## Configuration

### Server: `config/villagertradeswap.json`

```json
{
  "rerollCost": 1
}
```

- `rerollCost`: emeralds per swap, a whole number from 0 to 64. `0` makes swapping free. Players in Creative mode never pay.

The file is created with the default value when it is missing and is read every time a world or server starts. An invalid value is replaced by the default (with a warning in the log); the file itself is left untouched.

Emeralds are taken from the main inventory and the hotbar (the slots shown in the trading screen), not from the trade's payment slots.

### Client: `config/villagertradeswap-client.json`

```json
{
  "showRerollButtons": true
}
```

- `showRerollButtons`: whether the swap buttons are shown. This is the toggle next to the "Trades" label, saved automatically.

## Upgrading a world to Minecraft 1.21.1

Use the 1.21.1 jar of this mod after the upgrade. Swapped trades are ordinary villager trades, so the game upgrades them like any other trade. The level recorded for each trade is stored next to the trades on the villager (and on zombie villagers) under the same key in both versions, and the game keeps it during the upgrade. Because the number of trades does not change, the recorded levels still match the trades in 1.21.1 and swapping keeps using the right level. If a trade could not be upgraded and the number of trades no longer matches, the mod falls back to working out the levels from the order of the trades.

## Compatibility notes

- **Villager Trade Peek** (1.20.1 version): works together. Swap buttons appear only on unlocked trades, never on the greyed-out locked rows Trade Peek adds below them. Swapping does not change the previewed future trades, and the trades Trade Peek unlocks on level-up are recognized as trades of that level.
- **Trade Cycling and similar mods:** works together. When such a mod rerolls all of a villager's trades, the new trades are recorded as trades of the villager's current level.
- Trades added by other mods through the standard villager trade event are part of the pool automatically.
- Mods that change how many trades a villager gets per level, or that add trades in unusual ways, still work; when the mod cannot tell which level such a trade came from, it assumes two trades per level in list order.

## Building from source

```
./gradlew build
```

The jars are written to `forge/build/libs` and `fabric/build/libs`. The project follows the [MultiLoader-Template](https://github.com/jaredlll08/MultiLoader-Template) layout: shared code lives in `common`, loader-specific code in `forge` and `fabric`.

## License

MIT. See [LICENSE](LICENSE).
