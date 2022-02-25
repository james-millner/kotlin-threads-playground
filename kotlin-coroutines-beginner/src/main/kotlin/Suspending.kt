import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val HALF_SECOND = 500L
private const val ONE_SECOND = HALF_SECOND * 2
private const val TWO_SECOND = ONE_SECOND * 2
var functionCalls = 0

fun main() {
    GlobalScope.launch { processMessage() }
    GlobalScope.launch { improveMessage() }
    print("Hello, ")
    Thread.sleep(TWO_SECOND)
    println("$functionCalls number of function calls")
}

suspend fun processMessage() {
    delay(HALF_SECOND)
    println("World!")
    functionCalls++
}

suspend fun improveMessage() {
    delay(ONE_SECOND)
    println("More sus funcs yay!")
    functionCalls++
}