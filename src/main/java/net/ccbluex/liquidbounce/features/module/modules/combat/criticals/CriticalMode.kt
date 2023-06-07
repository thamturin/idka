package net.ccbluex.liquidbounce.features.module.modules.combat.criticals

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.features.module.modules.combat.Criticals
import net.ccbluex.liquidbounce.value.Value
import net.ccbluex.liquidbounce.utils.ClassUtils


abstract class CriticalMode(val modeName: String): MinecraftInstance() {
	protected val criticals: Criticals
		get() = LiquidBounce.moduleManager[Criticals::class.java]!!

	open val values: List<Value<*>>
		get() = ClassUtils.getValues(this.javaClass, this)

	open fun onEnable() {}

	open fun onDisable() {}

    open fun onUpdate() {}
    open fun onPacket(event: PacketEvent) {}
    open fun onAttack(event: AttackEvent) {}
}
