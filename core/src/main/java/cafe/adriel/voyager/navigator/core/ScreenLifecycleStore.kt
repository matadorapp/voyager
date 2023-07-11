package cafe.adriel.voyager.navigator.core

object ScreenLifecycleStore {

    private val owners = ThreadSafeMap<ScreenKey, ScreenLifecycleOwner>()

    fun get(
        screen: Screen,
        factory: (ScreenKey) -> ScreenLifecycleOwner
    ): ScreenLifecycleOwner =
        owners.getOrPut(screen.key) { factory(screen.key) }

    fun remove(screen: Screen) {
        owners.remove(screen.key)?.onDispose(screen)
    }
}
