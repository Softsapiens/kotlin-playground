package coding.coroutines

import kotlin.coroutines.*
import kotlin.coroutines.intrinsics.*

// ARTICLE
// https://elizarov.medium.com/deep-recursion-with-coroutines-7c53e15993e3

class Tree(val left: Tree?, val right: Tree?)

const val n = 100_000

val deepTree = generateSequence(Tree(null, null)) { prev ->
    Tree(prev, null)
}.take(n).last()

class DeepRecursiveFunction<T, R>(
    val block: suspend DeepRecursiveScope<T, R>.(T) -> R
)

class DeepRecursiveScope<T, R>(
    block: suspend DeepRecursiveScope<T, R>.(T) -> R,
    private var value: T
) : Continuation<R> {
    @Suppress("UNCHECKED_CAST")
    private val function = block as Function3<DeepRecursiveScope<T,R>, T, Continuation<R>, R?>
    private var result = Result.success<R?>(null)
    private var cont: Continuation<R>? = this

    suspend fun callRecursive(value: T): R =
        suspendCoroutineUninterceptedOrReturn { cont ->
            this.cont = cont
            this.value = value
            return@suspendCoroutineUninterceptedOrReturn COROUTINE_SUSPENDED
        }

    fun runCallLoop(): R {
        while (true) {
            val result = this.result
            val cont = this.cont // null means done
                ?: return result.getOrThrow()!!
            val r = try {
                function(this, value, cont)
            } catch (e: Throwable) {
                cont.resumeWithException(e)
                continue
            }
            if (r !== COROUTINE_SUSPENDED)
                cont.resume(r!!)
        }
    }

    override val context: CoroutineContext
        get() = EmptyCoroutineContext

    override fun resumeWith(result: Result<R>) {
        this.cont = null
        this.result = result
    }
}

operator fun <T, R> DeepRecursiveFunction<T, R>.invoke(value: T): R =
    DeepRecursiveScope(block, value).runCallLoop()

val depth = DeepRecursiveFunction<Tree?, Int> { t ->
    if (t == null) 0 else maxOf(
        callRecursive(t.left),
        callRecursive(t.right)
    ) + 1
}

fun main() {
    println(depth(deepTree))
}