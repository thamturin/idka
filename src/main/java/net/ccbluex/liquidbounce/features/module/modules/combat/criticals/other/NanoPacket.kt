package net.ccbluex.liquidbounce.features.module.modules.combat.criticals.other


import net.ccbluex.liquidbounce.features.module.modules.combat.criticals.CriticalMode
import net.ccbluex.liquidbounce.event.AttackEvent
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
class NanoPacket: CriticalMode("NanoPacket") {
	override fun onAttack(event: AttackEvent) {
		val y = mc.thePlayer.posY
		mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posZ, y + 0.00973333333333, mc.thePlayer.posZ, false))
        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posZ, y + 0.001, mc.thePlayer.posZ, false))
        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posZ, y - 0.01200000000007, mc.thePlayer.posZ, false))
        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posZ, y - 0.0005, mc.thePlayer.posZ, false))
	}
}
