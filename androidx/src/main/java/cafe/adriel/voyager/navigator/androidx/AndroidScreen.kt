package cafe.adriel.voyager.navigator.androidx

import cafe.adriel.voyager.navigator.core.AndroidScreenLifecycleOwner
import cafe.adriel.voyager.navigator.core.Screen
import cafe.adriel.voyager.navigator.core.ScreenKey
import cafe.adriel.voyager.navigator.core.ScreenLifecycleOwner
import cafe.adriel.voyager.navigator.core.ScreenLifecycleProvider
import cafe.adriel.voyager.navigator.core.uniqueScreenKey

abstract class AndroidScreen : Screen, ScreenLifecycleProvider {

    override val key: ScreenKey = uniqueScreenKey

    override fun getLifecycleOwner(): ScreenLifecycleOwner = AndroidScreenLifecycleOwner.get(this)
}
