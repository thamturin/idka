package net.ccbluex.liquidbounce.features.module.modules.movement.speeds

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.value.Value
import net.ccbluex.liquidbounce.utils.ClassUtils
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedType


abstract class SpeedMode(val modeName: String, val typeName: SpeedType): MinecraftInstance() {
	open val prefix = "${typeName.typeName}"

	protected val speed: Speed
		get() = LiquidBounce.moduleManager[Speed::class.java]!!

	open val values: List<Value<*>>
		get() = ClassUtils.getValues(this.javaClass, this)

	open val isActive
		get() = speed != null && !mc.thePlayer.isSneaking && speed.state && speed.mode.equals(modeName)

	open fun onEnable() {}

	open fun onDisable() {}

    open fun onUpdate() {}
    open fun onPacket(event: PacketEvent) {}
    open fun onMotion(event: MotionEvent) {}
    open fun onMove(event: MoveEvent) {}
    open fun onTick(event: TickEvent) {}
}
