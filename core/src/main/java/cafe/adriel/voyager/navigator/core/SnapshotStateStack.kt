package cafe.adriel.voyager.navigator.core

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList

fun <Item> List<Item>.toMutableStateStack(
    minSize: Int = 0
): SnapshotStateStack<Item> =
    SnapshotStateStack(this, minSize)

class SnapshotStateStack<Item>(
    items: List<Item>,
    minSize: Int = 0
) : Stack<Item> {

    init {
        require(minSize >= 0) { "Min size $minSize is less than zero" }
        require(items.size >= minSize) { "Stack size ${items.size} is less than the min size $minSize" }
    }

    @PublishedApi
    internal val stateStack: SnapshotStateList<Item> = items.toMutableStateList()

    public override var lastEvent: StackEvent by mutableStateOf(StackEvent.Idle, neverEqualPolicy())
        private set

    override val items: List<Item> by derivedStateOf {
        stateStack.toList()
    }

    override val lastItemOrNull: Item? by derivedStateOf {
        stateStack.lastOrNull()
    }

    @Deprecated(
        message = "Use 'lastItemOrNull' instead. Will be removed in 1.0.0.",
        replaceWith = ReplaceWith("lastItemOrNull")
    )
    override val lastOrNull: Item? by derivedStateOf {
        lastItemOrNull
    }

    override val size: Int by derivedStateOf {
        stateStack.size
    }

    override val isEmpty: Boolean by derivedStateOf {
        stateStack.isEmpty()
    }

    override val canPop: Boolean by derivedStateOf {
        stateStack.size > minSize
    }

    override infix fun push(item: Item) {
        stateStack += item
        lastEvent = StackEvent.Push
    }

    override infix fun push(items: List<Item>) {
        stateStack += items
        lastEvent = StackEvent.Push
    }

    public override infix fun replace(item: Item) {
        if (stateStack.isEmpty()) {
            push(item)
        } else {
            stateStack[stateStack.lastIndex] = item
        }
        lastEvent = StackEvent.Replace
    }

    override infix fun replaceAll(item: Item) {
        stateStack.clear()
        stateStack += item
        lastEvent = StackEvent.Replace
    }

    override infix fun replaceAll(items: List<Item>) {
        stateStack.clear()
        stateStack += items
        lastEvent = StackEvent.Replace
    }

    override fun pop(): Boolean =
        if (canPop) {
            stateStack.removeAt(stateStack.lastIndex)
            lastEvent = StackEvent.Pop
            true
        } else {
            false
        }

    override fun popAll() {
        popUntil { false }
    }

    override infix fun popUntil(predicate: (Item) -> Boolean): Boolean {
        var success = false
        val shouldPop = {
            lastItemOrNull
                ?.let(predicate)
                ?.also { success = it }
                ?.not()
                ?: false
        }

        while (canPop && shouldPop()) {
            stateStack.removeAt(stateStack.lastIndex)
        }

        lastEvent = StackEvent.Pop

        return success
    }

    override operator fun plusAssign(item: Item) {
        push(item)
    }

    override operator fun plusAssign(items: List<Item>) {
        push(items)
    }

    override fun clearEvent() {
        lastEvent = StackEvent.Idle
    }
}
