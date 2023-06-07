package net.ccbluex.liquidbounce.features.module.modules.combat.criticals.other


import net.ccbluex.liquidbounce.features.module.modules.combat.criticals.CriticalMode
import net.ccbluex.liquidbounce.event.AttackEvent
class Hop: CriticalMode("Hop") {
	override fun onAttack(event: AttackEvent) {
		mc.thePlayer.motionY = 0.1
        mc.thePlayer.fallDistance = 0.1f
        mc.thePlayer.onGround = false
	}
}
