package net.ccbluex.liquidbounce.features.module.modules.combat.criticals.other


import net.ccbluex.liquidbounce.features.module.modules.combat.criticals.CriticalMode
import net.ccbluex.liquidbounce.event.AttackEvent
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
class NonCalculable: CriticalMode("NonCalculable") {
	override fun onAttack(event: AttackEvent) {
		val y = mc.thePlayer.posY
		mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, y + 1E-5, mc.thePlayer.posZ, false))
        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, y + 1E-7, mc.thePlayer.posZ, false))
        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, y - 1E-6, mc.thePlayer.posZ, false))
        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, y - 1E-4, mc.thePlayer.posZ, false))
	}
}
