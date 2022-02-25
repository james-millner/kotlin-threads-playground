import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        launch(CoroutineName("test")) {
            println("This is run from ${this.coroutineContext[CoroutineName.Key]}")
        }

        GlobalScope.launch {
                //Only put this in to check out the global scope attributes using cmd+click
        }
    }
}