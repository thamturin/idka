package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.utils.ClassUtils
import net.ccbluex.liquidbounce.features.module.modules.misc.autoplays.AutoPlayMode
import net.ccbluex.liquidbounce.value.ListValue

@ModuleInfo(name = "AutoPlay", spacedName = "Auto Play", description = "Auto play minigames in several servers", category = ModuleCategory.MISC)
class AutoPlay: Module() {
	private val modes = ClassUtils.resolvePackage("${this.javaClass.`package`.name}.autoplays", AutoPlayMode::class.java)
		.map{ it.newInstance() as AutoPlayMode }
		.sortedBy{ it.modeName }

	private val mode: AutoPlayMode
		get() = modes.find{modeValue.get().equals(it.modeName)} ?: throw NullPointerException()

	private val modeValue: ListValue = object: ListValue("Mode", modes.map{ it.modeName }.toTypedArray(), "HeroMC_Skywars") {
		override fun onChange(oldValue: String, newValue: String) {
			if (state) onDisable()
		}
		override fun onChanged(oldValue: String, newValue: String) {
			if (state) onEnable()
		}
	}

	@EventTarget
	fun onUpdate(event: UpdateEvent) {mode.onUpdate()}

	@EventTarget
	fun onPacket(event: PacketEvent) {mode.onPacket(event)}

	override fun onEnable() {mode.onEnable()}

}
