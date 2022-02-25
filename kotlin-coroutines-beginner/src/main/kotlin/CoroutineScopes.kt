import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val ONE_SECOND = 1000L
private val TWO_SECOND = ONE_SECOND * 2
private val HALF_SECOND = ONE_SECOND / 2

fun main() {
    runBlocking {
        launch {
            delay(ONE_SECOND)
            println("Task from run blocking")
        }
        GlobalScope.launch {
            delay(HALF_SECOND)
            println("Task from global score")
        }
        coroutineScope {
            launch {
                delay(ONE_SECOND + HALF_SECOND)
                println("Task from coroutineScope")
            }
        }
    }

    println("Carry on my wayword son.")
}