package coding

fun main() {

    A().B().run {
        123.foo()
        A().withContextB()
    }
}

class A { // implicit label @A
    inner class B { // implicit label @B
        fun Int.foo() { // implicit label @foo
            val a = this@A // A's this
            val b = this@B // B's this

            val c = this // foo()'s receiver, an Int
            val c1 = this@foo // foo()'s receiver, an Int

            println("this@A=$a this@B=$b this=$c this@foo=$c1")
        }
    }

    context(B)
    fun withContextB() { println("this@B= $this@B") }
}


// kotlin + webflux + coroutines
// kotlin + mvc

// webflux -> reactor in Java but in Kotlin could be done using coroutines
// https://www.baeldung.com/kotlin/spring-boot-kotlin-coroutines
// https://github.com/AdevintaSpain/ms-test--hello-twitch
// https://www.youtube.com/watch?v=pYK5KkuZ3aU


// Ni component, ni service, ni repository

/**
JdbcTemplate Spring --> dependencia jdbc spring
RestTemplate
Datasource Spring
 flyway
 junit 5 -> anotacions de @test, @sql amb el flyway,...
 testcontainers
 slice test -> per aixecar només part
 **/