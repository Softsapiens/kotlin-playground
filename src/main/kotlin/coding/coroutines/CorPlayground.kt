package coding.coroutines

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import kotlinx.coroutines.*

object CorPlayground {
    @Test
    fun play1() {
        runBlocking {
            val c1 = launch { co1() }
            val c2 = async { co2() }
            co3()
        }
    }

    @Test
    fun playCanc1() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0
            while (i < 5) { // computation loop, just wastes CPU
                // print a message twice a second
                if (System.currentTimeMillis() >= nextPrintTime) {
                    println("job: I'm sleeping ${i++} ...")
                    // A yield here, enables checking if we are cancelled
                    // yield()
                    nextPrintTime += 500L
                }
            }
        }
        delay(1300L) // delay a bit
        println("main: I'm tired of waiting!")
        job.cancelAndJoin() // cancels the job and waits for its completion
        println("main: Now I can quit.")
    }

    @Test
    fun playCanc2() = runBlocking {
        val job = launch {
            try {
                repeat(1000) { i ->
                    println("job: I'm sleeping $i ...")
                    delay(500L)
                }
            } finally {
                println("job: I'm running finally")
            }
        }
        delay(1300L) // delay a bit
        println("main: I'm tired of waiting!")
        job.cancelAndJoin() // cancels the job and waits for its completion
        println("main: Now I can quit.")
    }
}


suspend fun co1(): Long {
    delay(500L)
    println("${Thread.currentThread()} -  col1")
    return System.currentTimeMillis()
}

suspend fun co2(): Long {
    delay(400L)
    println("${Thread.currentThread()} -  col2")
    return System.currentTimeMillis()
}

suspend fun co3(): Long {
    delay(200L)
    println("${Thread.currentThread()} -  col3")
    return System.currentTimeMillis()
}

