package net.ccbluex.liquidbounce.features.module.modules.combat.criticals.other


import net.ccbluex.liquidbounce.features.module.modules.combat.criticals.CriticalMode
import net.ccbluex.liquidbounce.event.AttackEvent
class Jump: CriticalMode("Jump") {
	override fun onAttack(event: AttackEvent) {
		if (mc.thePlayer.onGround) {
			mc.thePlayer.motionY = criticals.jumpHeightValue.get().toDouble()
		} else {
			mc.thePlayer.motionY -= criticals.downYValue.get()
		}
	}
}
