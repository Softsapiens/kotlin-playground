package coding.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import mu.KotlinLogging
import org.junit.jupiter.api.Test
import java.lang.Thread.sleep


val logger = KotlinLogging.logger {}

object CorPlayground {
    @Test
    // https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-dispatcher/
    fun playDispatchers() {
        logger.info {
            kotlin.runCatching {
                runBlocking(Default) {
                    logger.info { "Starting" }
                    launch {
                        logger.info { "Starting co launched" }
                        co("co-launched", 101L)
                    }
                    async {
                        // This is equivalent of using _launch_ because this async block isn't returning any value and it is not awaited, neither.
                        logger.info { "Starting co async" }
                        co("co-async", 100L)
                    }
                    val a = async {
                        logger.info { "sleeping async" }
                        sleep(1000) // Can't be cancelled
                        // throw Exception("Boom!") // this exception will stop parent scope too, so _co-normal_ will not execute
                        yield() // Give a chance to be cancelled
                        //delay(1000) // Could be cancelled if it is used instead of sleep
                        logger.info { "awaked async" }
                    }
                    delay(100L) // this delay give chance to schedule _a_ coroutine
                    a.cancel(CancellationException("kill it"))
                    a.join()
                    co("co-normal", 50L)
                }
            }
        }
    }

    @Test
    // https://www.geeksforgeeks.org/launch-vs-async-in-kotlin-coroutines
    // When running on Default dispatcher, there is enough workers to kill the job on its 3third iteration
    // But without specify any dispatcher, default one will be an event loop over the current thread, then the job can only be killed
    // on the fourth iteration only if yield is called
    fun playCanc1() = runBlocking(Default) {
        val startTime = System.currentTimeMillis()
        val job = launch {
            var nextPrintTime = startTime
            var i = 0
            while (i < 10) { // computation loop, just wastes CPU
                // print a message twice a second
                if (System.currentTimeMillis() >= nextPrintTime) {
                    logger.info("job: I'm sleeping ${i++} ...")
                    // A yield here, enables checking if we are cancelled else cancel will not be processed.
                    yield()
                    nextPrintTime += 500L

                }
            }
        }
        delay(1300L) // delay a bit
        logger.info("main: I'm tired of waiting!")
        job.cancelAndJoin() // cancels the job and waits for its completion
        logger.info("main: Now I can quit.")
    }

    @Test
    fun playCanc2() = runBlocking {
        val job = launch {
            try {
                repeat(1000) { i ->
                    logger.info("job: I'm sleeping $i ...")
                    delay(500L)
                }
            } finally {
                logger.info("job: I'm running finally")
            }
        }
        delay(1300L) // delay a bit
        logger.info("main: I'm tired of waiting!")
        job.cancelAndJoin() // cancels the job and waits for its completion
        logger.info("main: Now I can quit.")
    }
}

suspend fun co(n: String, w: Long): Long {
    delay(w)
    logger.info { "$n has waited $w millis" }
    return System.currentTimeMillis()
}
