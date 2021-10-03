---
layout: page
title: Configurations
---

## Example Configuration

```yml
############################################################
# +------------------------------------------------------+ #
# |                  Main Configurations                 | #
# +------------------------------------------------------+ #
############################################################

raid:
  # The number of waves must be at least 1. The length of the arrays under the `mobs`
  # configuration section below must also be at least the number of waves. Each wave
  # must have at least one raider.
  waves: 20
  # To disable sounds, leave the sound fields blank.
  sounds:
    summon: ITEM_TRIDENT_THUNDER
    victory: ENTITY_ENDER_DRAGON_DEATH
    defeat: ENTITY_ENDER_DRAGON_DEATH
  announce-waves:
    title: true
    message: false
  # Do not add any other mobs, they will not spawn...
  mobs:
    pillager: [5, 7, 7, 8, 8, 8, 8, 9, 9, 9, 9, 9, 9, 9, 9, 10, 10, 10, 10, 10]
    vindicator: [0, 3, 5, 5, 5, 5, 5, 6, 7, 7, 7, 8, 8, 8, 9, 9, 9, 10, 10, 10]
    ravager: [0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3]
    witch: [0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3]
    evoker: [0, 0, 0, 0, 1, 2, 2, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4]
    illusioner: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 2, 3, 3, 3]

hero-of-the-village:
  # Level 12 is enough to grant a player a cost of 1 emerald for any default trades.
  # Players will always receive the hero of the village effect with level of this value,
  # or the omen level of the large raid, whichever is lower.
  level: 12
  # In minutes.
  duration: 40

rewards:
  # Leave this section blank to disable the feature.
  items:
    1:
      material: BOOK
      # Amount must be more than 0 but no more than the maximum stack size of the item
      amount: 1
      display-name: "&6Example Reward"
      lore:
        # - "&5Drop the item into lava in a village"
        # - "&5to summon a large raid!"
      custom-model-data:
      # Avoid using armor/tools for material if this is enabled. It applies mending to
      # the item for the glint.
      enchantments:
        1:
          type: MENDING
          level: 1
        2:
          type: DURABILITY
          level: 1
  # Leave this section blank to disable the feature. The plugin provides `<player>` as a
  # placeholder for the console to execute a command with the target player's name. Note
  # that every command listed here will be executed once per player.
  commands:
    # - lrgive <player> 1
    # - give <player> stick

############################################################
# +------------------------------------------------------+ #
# |                   Trigger Mechanism                  | #
# +------------------------------------------------------+ #
#  We present the following triggering mechanisms. It's    #
#  advised that you only enable one of them at any point   #
#  in time although they should technically work with      #
#  each other.                                             #
############################################################

trigger:
  # Players get bad omen of level higher than 5 if enabled. Raids will all be large
  # raids, number of waves will be 5 or the accumulative omen level of all players
  # entering the village, whichever is higher. Maximum number of waves will be the
  # number of waves mentioned above in `raid.waves`.
  omen:
    enabled: true
    max-level: 10
  # Dropping summoning items in lava will trigger a large raid if in vicinity of a
  # village.
  drop-item-in-lava:
    enabled: false
    # Note: The summoning item stated here will not lose its primary functionality. For
    # example, totems will still be consumed and revive the player holding it.
    item:
      # Totem by default if the input material is invalid/left blank. Instead of totems,
      # you can use rarer items for summoning (e.g NETHER_STAR).
      material: TOTEM_OF_UNDYING
      display-name: "&6Large Raid Summoner"
      lore:
        # - "&5Drop the item into lava in a village"
        # - "&5to summon a large raid!"
      custom-model-data:
      # Avoid using armor/tools for material if this is enabled. It applies mending to
      # the item for the glint.
      enchantment-glint: true
  # Raids will trigger for players staying up in villages till midnight on a new moon.
  new-moon:
    enabled: false

# If enabled, large raids can only be triggered for artificial village centers registered
# by the plugin. Make sure that your artificial village centers are at least 128 blocks/
# 8 chunks away from each other and other villager-claimed village blocks (e.g. job block,
# bed, bell).
artificial-only: false

############################################################
# +------------------------------------------------------+ #
# |                       Messages                       | #
# +------------------------------------------------------+ #
############################################################

attempt-peaceful: "&eAttempted to spawn large raid but failed due to world's peaceful difficulty..."
receive-rewards: "&aReceiving rewards..."
wave-broadcast:
  title:
    default: "&6Wave %s"
    final: "&6Final Wave"
  message:
    default: "&6Spawning wave %s..."
    final: "&6Spawning final wave..."
```