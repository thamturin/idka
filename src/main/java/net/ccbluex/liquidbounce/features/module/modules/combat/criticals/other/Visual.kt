package net.ccbluex.liquidbounce.features.module.modules.combat.criticals.other


import net.ccbluex.liquidbounce.features.module.modules.combat.criticals.CriticalMode
import net.ccbluex.liquidbounce.event.AttackEvent

class Visual: CriticalMode("Visual") {
	override fun onAttack(event: AttackEvent) {
        mc.thePlayer.onCriticalHit(criticals.entity)
	}
}
