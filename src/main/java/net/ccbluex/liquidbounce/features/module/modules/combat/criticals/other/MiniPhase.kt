package net.ccbluex.liquidbounce.features.module.modules.combat.criticals.other


import net.ccbluex.liquidbounce.features.module.modules.combat.criticals.CriticalMode
import net.ccbluex.liquidbounce.event.AttackEvent
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
class MiniPhase: CriticalMode("MiniPhase") {
	override fun onAttack(event: AttackEvent) {
		val y = mc.thePlayer.posY
		mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, y - 0.0125, mc.thePlayer.posZ, false))
        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, y + 0.01275, mc.thePlayer.posZ, false))
        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, y - 0.00025, mc.thePlayer.posZ, true))
	}
}
