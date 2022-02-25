import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val ONE_SECOND = 1000L
private val TWO_SECOND = ONE_SECOND * 2

fun main() {

    GlobalScope.launch {
        delay(ONE_SECOND)
        println("World")
    }
    print("Hello, ")
    Thread.sleep(TWO_SECOND)
}