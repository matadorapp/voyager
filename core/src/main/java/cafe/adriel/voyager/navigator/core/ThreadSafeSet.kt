package cafe.adriel.voyager.navigator.core

import java.util.concurrent.CopyOnWriteArraySet

class ThreadSafeSet<T> : MutableSet<T> by CopyOnWriteArraySet()
