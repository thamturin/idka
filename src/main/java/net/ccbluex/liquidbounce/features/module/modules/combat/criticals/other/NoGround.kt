package net.ccbluex.liquidbounce.features.module.modules.combat.criticals.other


import net.ccbluex.liquidbounce.features.module.modules.combat.criticals.CriticalMode
import net.ccbluex.liquidbounce.event.PacketEvent
import net.minecraft.network.play.client.C03PacketPlayer

class NoGround: CriticalMode("NoGround") {
	override fun onEnable() {
        mc.thePlayer.jump()
	}
	override fun onPacket(event: PacketEvent) {
		val packet = event.packet
		if (packet is C03PacketPlayer) {
            packet.onGround = false
        }
	}
}
