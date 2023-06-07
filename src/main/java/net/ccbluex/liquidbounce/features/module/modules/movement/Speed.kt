package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.utils.ClassUtils
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedType
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils

@ModuleInfo(name = "Speed", description = "Run faster.", category = ModuleCategory.MOVEMENT)
class Speed: Module() {
	private val modes = ClassUtils.resolvePackage("${this.javaClass.`package`.name}.speeds", SpeedMode::class.java)
		.map{it.newInstance() as SpeedMode}
		.sortedBy{it.modeName}

	val mode: SpeedMode
		get() = modes.find{modeValue.get().equals(it.modeName)} ?: throw NullPointerException()

	private val typeValue: ListValue = object: ListValue("Type", SpeedType.values().map{it.typeName}.toTypedArray(), "AAC") {
		override fun onChanged(oldValue: String, newValue: String) {
			modeValue.changeListValues(modes.filter{it.typeName.typeName == newValue}.map{it.modeName}.toTypedArray())
		}
		override fun onChange(oldValue: String, newValue: String) {
			modeValue.changeListValues(modes.filter{it.typeName.typeName == newValue}.map{it.modeName}.toTypedArray())
		}
	}

	private val modeValue: ListValue = object: ListValue("Mode", modes.filter{it.typeName == SpeedType.AAC}.map{it.modeName}.toTypedArray(), "AAC4Hop") {
		override fun onChange(oldValue: String, newValue: String) {
			if (state) onDisable()
		}
		override fun onChanged(oldValue: String, newValue: String) {
			if (state) onEnable()
		}
	}	

	private val noWater = BoolValue("NoWater", false)
	private val alwaysSprint = BoolValue("AlwaysSprint", true)


	override fun onEnable() {mode.onEnable()}

	override fun onDisable() {mode.onDisable()}

	@EventTarget
	fun onUpdate(event: UpdateEvent) {
		if (mc.thePlayer.isSneaking) return
		if (MovementUtils.isMoving() && alwaysSprint.get()) mc.thePlayer.isSprinting = true
		mode.onUpdate()
	}

	@EventTarget
	fun onPacket(event: PacketEvent) {
		mode.onPacket(event)
	}

	@EventTarget
	fun onMotion(event: MotionEvent) {
		if (mc.thePlayer.isSneaking() || event.eventState != EventState.PRE) return
		if (MovementUtils.isMoving() && alwaysSprint.get()) mc.thePlayer.isSprinting = true
		mode.onMotion(event)
	}

	@EventTarget
	fun onMove(event: MoveEvent) {
		if (mc.thePlayer.isSneaking) return
		mode.onMove(event)
	}

	override val tag: String?
		get() = "${typeValue.get()} - ${modeValue.get()}"

}
