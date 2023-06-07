package net.ccbluex.liquidbounce.features.module.modules.misc.autoplays

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.features.module.modules.misc.AutoPlay
import net.ccbluex.liquidbounce.value.Value
import net.ccbluex.liquidbounce.utils.ClassUtils


abstract class AutoPlayMode(val modeName: String): MinecraftInstance() {
	protected val autoplay: AutoPlay
		get() = LiquidBounce.moduleManager[AutoPlay::class.java]!!

	open val values: List<Value<*>>
		get() = ClassUtils.getValues(this.javaClass, this)

	open fun onEnable() {}

	open fun onDisable() {}

    open fun onUpdate() {}
    open fun onPacket(event: PacketEvent) {}
}