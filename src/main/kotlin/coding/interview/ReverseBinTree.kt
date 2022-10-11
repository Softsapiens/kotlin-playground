package coding.interview

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

fun main() {


}

sealed class Tree(value: Int)
data class Node(var value: Int, var left: Tree, var right: Tree): Tree(value)
data class Leaf(var value: Int): Tree(value)


// Recursive version, NOT tail recursion
fun Tree.reverse1(): Tree {

    var n = when(this) {
        is Node -> {
            val (v, l, r) = this
            Node(v, r.reverse1(), l.reverse1())
        }
        is Leaf -> this
    }

    return n
}



object TreeTests {

    val tree1 = Node(1, Node(2, Leaf(4), Leaf(5)), Node(3, Leaf(6), Leaf(7)))
    val tree1Reversed = Node(1, Node(3, Leaf(7), Leaf(6)), Node(2, Leaf(5), Leaf(4)))

    val tree2 = Node(1, Node(2, Leaf(4), Leaf(5)), Node(3, Leaf(6), Node( 8, Leaf(7), Leaf(9))))
    val tree2Reversed = Node(1, Node(3, Node(8, Leaf(9), Leaf(7)), Leaf(6)), Node(2, Leaf(5), Leaf(4)))

    @Test
    fun `tree1 reverse1 == tree1Reversed`() {
        Assertions.assertEquals(tree1Reversed, tree1.reverse1())
    }

    @Test
    fun `tree2 reverse1 == tree2Reversed`() {
        Assertions.assertEquals(tree2Reversed, tree2.reverse1())
    }
}