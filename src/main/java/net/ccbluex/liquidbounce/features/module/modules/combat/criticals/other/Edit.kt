package net.ccbluex.liquidbounce.features.module.modules.combat.criticals.other


import net.ccbluex.liquidbounce.features.module.modules.combat.criticals.CriticalMode
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.AttackEvent
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C07PacketPlayerDigging


class Edit: CriticalMode("Edit") {
    private var readyCrits = false

    override fun onAttack(event: AttackEvent) {
        readyCrits = true
    }
	override fun onPacket(event: PacketEvent) {
        val packet = event.packet
		if (readyCrits) {
            if (packet is C03PacketPlayer) {
                packet.onGround = false
            }
            readyCrits = false
        }
	}
}
