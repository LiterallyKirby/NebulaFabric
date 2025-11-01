# Nebula Helper Functions Documentation

This document provides an overview of all helper classes and their functions to make module development easier.

## Table of Contents
1. [PlayerHelper](#playerhelper)
2. [RotationHelper](#rotationhelper)
3. [BlockHelper](#blockhelper)
4. [InventoryHelper](#inventoryhelper)
5. [TimerHelper](#timerhelper)
6. [CrystalHelper](#crystalhelper)
7. [MovementHelper](#movementhelper)
8. [CombatHelper](#combathelper)

---

## PlayerHelper
**Package:** `com.kirby.nebula.util`

Handles all player-related operations.

### Basic Player Info
- `getPlayer()` - Get the local player
- `isPlayerValid()` - Check if player exists and is valid
- `getPlayerPos()` - Get player's current position
- `getEyePos()` - Get player's eye position
- `getRotation()` - Get player's rotation (yaw, pitch)
- `getYaw()` - Get player's yaw
- `getPitch()` - Get player's pitch

### Health & Status
- `getHealth()` - Get player's current health
- `getMaxHealth()` - Get player's max health
- `getHealthPercentage()` - Get health as percentage (0.0 - 1.0)
- `isOnGround()` - Check if player is on ground
- `isInWater()` - Check if player is in water
- `isInLava()` - Check if player is in lava

### Movement
- `isMoving()` - Check if player is moving
- `getSpeed()` - Get player's movement speed

### Entity Finding
- `getEntitiesInRange(range)` - Get all entities within range
- `getLivingEntitiesInRange(range)` - Get all living entities within range
- `getPlayersInRange(range)` - Get all players within range
- `getClosestEntity(range)` - Get the closest entity to player
- `getClosestLivingEntity(range)` - Get the closest living entity
- `getClosestPlayer(range)` - Get the closest player

### Entity Interaction
- `isFriend(entity)` - Check if entity is a friend
- `canSeeEntity(entity)` - Check if player can see entity
- `getDistanceTo(entity)` - Get distance to entity
- `getDistanceToSq(entity)` - Get squared distance (faster)
- `isHolding(itemClass)` - Check if holding item of specific class

---

## RotationHelper
**Package:** `com.kirby.nebula.util`

Handles rotation calculations and aiming.

### Rotation Calculation
- `getRotationTo(target)` - Calculate rotation to look at position
- `getRotationToEntity(entity)` - Calculate rotation to look at entity
- `getRotationToBlock(pos)` - Calculate rotation to look at block
- `getVectorFromRotation(yaw, pitch)` - Get vector from rotation angles
- `getLookVector()` - Get look vector for current rotation

### Rotation Comparison
- `getAngleDifference(angle1, angle2)` - Get angle difference
- `getRotationDifference(target)` - Get rotation difference from target
- `wrapAngleTo180(angle)` - Wrap angle to -180 to 180 range

### Smooth Rotation
- `smoothRotation(current, target, smoothness)` - Smoothly rotate towards target

### Looking Detection
- `isLookingAt(entity, maxAngle)` - Check if looking at entity
- `isLookingAt(pos, maxAngle)` - Check if looking at position

### Direct Rotation
- `rotateToPosition(pos)` - Rotate player to look at position
- `rotateToEntity(entity)` - Rotate player to look at entity

---

## BlockHelper
**Package:** `com.kirby.nebula.util`

Handles block operations and detection.

### Block Info
- `getBlock(pos)` - Get block at position
- `getBlockState(pos)` - Get block state at position
- `isAir(pos)` - Check if block is air
- `isReplaceable(pos)` - Check if block is replaceable
- `isSolid(pos)` - Check if block is solid
- `isBlock(pos, block)` - Check if block is specific type

### Block Finding
- `getBlocksInSphere(center, radius)` - Get all blocks in sphere
- `getBlocksInBox(pos1, pos2)` - Get all blocks in box
- `findBlocks(block, center, radius)` - Find blocks of specific type
- `findClosestBlock(block, center, radius)` - Find closest block of type

### Block Interaction
- `getSurroundingBlocks(pos)` - Get all 6 surrounding blocks
- `canReach(pos, reachDistance)` - Check if player can reach block
- `getFacing(pos)` - Get the face to click on block
- `canPlace(pos)` - Check if block can be placed at position
- `getDistanceToBlock(pos)` - Get distance to block

### Utility
- `hasLineOfSight(from, to)` - Check line of sight between positions
- `isValidPos(pos)` - Check if position is valid
- `getBlockBox(pos)` - Get block bounding box

---

## InventoryHelper
**Package:** `com.kirby.nebula.util`

Handles inventory management and item operations.

### Basic Inventory
- `getInventory()` - Get player's inventory
- `getMainHandItem()` - Get item in main hand
- `getOffhandItem()` - Get item in offhand
- `getItemInSlot(slot)` - Get item in specific slot

### Item Finding
- `findItem(item)` - Find item in inventory (returns slot)
- `findItemInHotbar(item)` - Find item in hotbar (0-8)
- `findAllItems(item)` - Find all slots containing item
- `hasItem(item)` - Check if inventory has item
- `countItem(item)` - Count items in inventory

### Item Checking
- `isHolding(item)` - Check if holding specific item
- `isHoldingType(itemClass)` - Check if holding item of type

### Hotbar Operations
- `getSelectedSlot()` - Get current selected hotbar slot
- `switchToSlot(slot)` - Switch to hotbar slot
- `switchToItem(item)` - Switch to item in hotbar
- `getHotbarItems()` - Get all hotbar items
- `isHotbarSlot(slot)` - Check if slot is in hotbar

### Inventory Status
- `getEmptySlots()` - Get empty slots count
- `isInventoryFull()` - Check if inventory is full

### Armor
- `getArmor(slot)` - Get armor in slot (0=feet, 1=legs, 2=chest, 3=head)
- `hasFullArmor()` - Check if wearing full armor
- `getArmorDurability()` - Get total armor durability percentage

---

## TimerHelper
**Package:** `com.kirby.nebula.util`

Handles timing operations for delays and cooldowns.

### Timer Control
- `reset()` - Reset timer to current time
- `setTime(time)` - Set timer to specific time in the past
- `getLastTime()` - Get last reset time

### Time Checking
- `hasReached(time)` - Check if time has passed (milliseconds)
- `hasReachedSeconds(seconds)` - Check if time has passed (seconds)
- `hasReachedAndReset(time)` - Check and reset if time passed

### Time Getting
- `getTimePassed()` - Get time passed since reset (milliseconds)
- `getTimePassedSeconds()` - Get time passed in seconds
- `getTimeRemaining(target)` - Get time remaining until target

### Example Usage
```java
private TimerHelper attackTimer = new TimerHelper();

public void onTick() {
    if (attackTimer.hasReached(500)) { // 500ms delay
        // Do attack
        attackTimer.reset();
    }
}
```

---

## CrystalHelper
**Package:** `com.kirby.nebula.util`

Specialized helper for Auto Crystal operations.

### Crystal Position
- `isValidCrystalPos(pos)` - Check if position valid for crystal
- `getValidCrystalPositions(range)` - Get all valid crystal positions
- `getBestCrystalPos(target, range)` - Get best position to place crystal

### Crystal Finding
- `getCrystalsInRange(range)` - Get all End Crystals in range
- `getClosestCrystal(pos, range)` - Get closest crystal to position
- `getBestCrystalToBreak(target, range)` - Get best crystal to break

### Damage Calculation
- `calculateCrystalDamage(crystalPos, target)` - Calculate damage to target
- `wouldDamageSelf(pos, minDamage)` - Check if would damage player
- `getTotalDamageToPlayers(pos, range)` - Calculate total damage to players
- `isInCrystalRange(pos, range)` - Check if in blast radius of crystal

### Crystal Inventory
- `hasCrystals()` - Check if player has crystals
- `getCrystalCount()` - Get crystal count in inventory

---

## MovementHelper
**Package:** `com.kirby.nebula.util`

Handles movement operations and speed control.

### Movement Status
- `isMoving()` - Check if player is moving
- `isMovingForward()` - Check if moving forward
- `isMovingBackward()` - Check if moving backward
- `isStrafingLeft()` - Check if strafing left
- `isStrafingRight()` - Check if strafing right

### Speed Operations
- `getSpeed()` - Get horizontal speed
- `getTotalSpeed()` - Get total speed (including vertical)
- `setSpeed(speed)` - Set horizontal speed
- `getBlocksPerSecond()` - Get blocks per second
- `getDirection()` - Get movement direction in radians

### Vertical Movement
- `isOnGround()` - Check if on ground
- `isInAir()` - Check if in air
- `isFalling()` - Check if falling
- `isAscending()` - Check if ascending
- `getDistanceToGround()` - Get distance to ground
- `getVerticalSpeed()` - Get vertical velocity
- `setVerticalSpeed(speed)` - Set vertical velocity
- `jump()` - Make player jump

### Advanced Movement
- `strafeTowards(target, speed)` - Strafe towards position
- `strafeAway(target, speed)` - Strafe away from position
- `stop()` - Stop all movement
- `canStepUp()` - Check if can step up

### Prediction & Distance
- `predictPosition(ticks)` - Predict position after ticks
- `getDistanceTo(pos)` - Get distance to position

---

## CombatHelper
**Package:** `com.kirby.nebula.util`

Handles combat operations and targeting.

### Basic Combat
- `attack(entity)` - Attack an entity
- `getAttackCooldown()` - Get attack cooldown progress (0.0 - 1.0)
- `isAttackReady()` - Check if attack is ready
- `canAttack(entity, maxRange)` - Check if can attack entity
- `isInCombat()` - Check if player is in combat

### Entity Finding
- `getHostileEntities(range)` - Get all hostile entities
- `getPassiveEntities(range)` - Get all passive entities
- `getClosestHostile(range)` - Get closest hostile entity
- `isHostile(entity)` - Check if entity is hostile

### Targeting
- `getBestTarget(range)` - Get best target (health + distance)
- `getTargetByPriority(range, players, hostiles, animals)` - Get target by priority
- `getMostThreatening(range)` - Get most threatening entity
- `getEntitiesLookingAtPlayer(range)` - Get entities targeting player

### Combat Analysis
- `getThreatLevel(entity)` - Get threat level (0-1)
- `wouldWinFight(target)` - Check if would win fight
- `getTimeToKill(entity, damage)` - Calculate time to kill

### FOV & Prediction
- `isInFOV(entity, fov)` - Check if entity is in FOV
- `getEntitiesInFOV(range, fov)` - Get entities in FOV
- `predictEntityPosition(entity, ticks)` - Predict entity position

---

## Tips for Module Development

1. **Use Timers**: Always use `TimerHelper` for delays instead of tick counters
2. **Check Validity**: Always call `PlayerHelper.isPlayerValid()` before operations
3. **Use Squared Distance**: Use `getDistanceToSq()` for performance when comparing distances
4. **Combine Helpers**: Combine multiple helpers for complex operations
5. **Cache Results**: Cache expensive calculations like entity lists
6. **Handle Nulls**: Always check for null returns from helper methods

---

