/*
 * LiquidBounce++ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/PlusPlusMC/LiquidBouncePlusPlus/
 */
package net.ccbluex.liquidbounce.features.module

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.KeyEvent
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.features.module.modules.combat.*
import net.ccbluex.liquidbounce.features.module.modules.exploit.*
import net.ccbluex.liquidbounce.features.module.modules.misc.*
import net.ccbluex.liquidbounce.features.module.modules.movement.*
import net.ccbluex.liquidbounce.features.module.modules.player.*
import net.ccbluex.liquidbounce.features.module.modules.render.*
import net.ccbluex.liquidbounce.features.module.modules.world.*
import net.ccbluex.liquidbounce.features.module.modules.world.Timer
import net.ccbluex.liquidbounce.features.module.modules.color.ColorMixer
import net.ccbluex.liquidbounce.utils.ClientUtils
import java.util.*


class ModuleManager : Listenable {

    public val modules = TreeSet<Module> { module1, module2 -> module1.name.compareTo(module2.name) }
    private val moduleClassMap = hashMapOf<Class<*>, Module>()

    public var shouldNotify : Boolean = false
    public var toggleSoundMode = 0
    public var toggleVolume = 0F

    init {
        LiquidBounce.eventManager.registerListener(this)
    }

    /**
     * Register all modules
     */
    fun registerModules() {
        ClientUtils.getLogger().info("[ModuleManager] Loading modules...")

        registerModules(
                Patcher::class.java,
                AutoWeapon::class.java,
                BowAimbot::class.java,
                Aimbot::class.java,
                Criticals::class.java,
                KillAura::class.java,
                Velocity::class.java,
                Fly::class.java,
                ClickGUI::class.java,
                InvMove::class.java,
                NoSlow::class.java,
                Strafe::class.java,
                Sprint::class.java,
                NoRotateSet::class.java,
                AntiBot::class.java,
                ChestStealer::class.java,
                Scaffold::class.java,
                FastPlace::class.java,
                ESP::class.java,
                Speed::class.java,
                NameTags::class.java,
                FastUse::class.java,
                Fullbright::class.java,
                Projectiles::class.java,
                PingSpoof::class.java,
                Step::class.java,
                AutoTool::class.java,
                NoWeb::class.java,
                Spammer::class.java,
                NoFall::class.java,
                Blink::class.java,
                NameProtect::class.java,
                NoHurtCam::class.java,
                Timer::class.java,
                FreeCam::class.java,     
                HitBox::class.java,
                Plugins::class.java,
                LongJump::class.java,
                AutoClicker::class.java,
                Clip::class.java,
                Phase::class.java,
                NoFOV::class.java,
                Animations::class.java,
                InvManager::class.java,
                TrueSight::class.java,
                AntiBlind::class.java,
                CameraClip::class.java,
                SuperKnockback::class.java,
                Reach::class.java,
                Rotations::class.java,
                NoJumpDelay::class.java,
                HUD::class.java,
                Ambience::class.java,
                Cape::class.java,
                NoRender::class.java,
                ItemPhysics::class.java,
                AutoLogin::class.java,
                Gapple::class.java,
                ColorMixer::class.java,
                Disabler::class.java,
                AutoDisable::class.java,
                SpinBot::class.java,
                MultiActions::class.java,
                AntiVoid::class.java,
                TargetStrafe::class.java,
                TargetMark::class.java,
                KeepSprint::class.java,
                SafeWalk::class.java,
                NoAchievements::class.java,
                Freeze::class.java,
                Eagle::class.java,
                AntiDesync::class.java,
                PacketFixer::class.java,
                SuperheroFX::class.java,
                NewGUI::class.java,
                PackSpoofer::class.java,
                AutoPlay::class.java,
                Insults::class.java,
                JelloArraylist::class.java,
        )

        ClientUtils.getLogger().info("[ModuleManager] Successfully loaded ${modules.size} modules.")
    }

    /**
     * Register [module]
     */
    fun registerModule(module: Module) {
        modules += module
        moduleClassMap[module.javaClass] = module

        module.onInitialize()
        generateCommand(module)
        LiquidBounce.eventManager.registerListener(module)
    }

    /**
     * Register [moduleClass]
     */
    private fun registerModule(moduleClass: Class<out Module>) {
        try {
            registerModule(moduleClass.newInstance())
        } catch (e: Throwable) {
            ClientUtils.getLogger().error("Failed to load module: ${moduleClass.name} (${e.javaClass.name}: ${e.message})")
        }
    }

    /**
     * Register a list of modules
     */
    @SafeVarargs
    fun registerModules(vararg modules: Class<out Module>) {
        modules.forEach(this::registerModule)
    }

    /**
     * Unregister module
     */
    fun unregisterModule(module: Module) {
        modules.remove(module)
        moduleClassMap.remove(module::class.java)
        LiquidBounce.eventManager.unregisterListener(module)
    }

    /**
     * Generate command for [module]
     */
    internal fun generateCommand(module: Module) {
        val values = module.values

        if (values.isEmpty())
            return

        LiquidBounce.commandManager.registerCommand(ModuleCommand(module, values))
    }

    /**
     * Legacy stuff
     *
     * TODO: Remove later when everything is translated to Kotlin
     */

    /**
     * Get module by [moduleClass]
     */
    fun <T : Module> getModule(moduleClass: Class<T>): T? = moduleClassMap[moduleClass] as T?

    operator fun <T : Module> get(clazz: Class<T>) = getModule(clazz)

    /**
     * Get module by [moduleName]
     */
    fun getModule(moduleName: String?) = modules.find { it.name.equals(moduleName, ignoreCase = true) }

    /**
     * Module related events
     */

    /**
     * Handle incoming key presses
     */
    @EventTarget
    private fun onKey(event: KeyEvent) = modules.filter { it.keyBind == event.key }.forEach { it.toggle() }

    override fun handleEvents() = true
}
