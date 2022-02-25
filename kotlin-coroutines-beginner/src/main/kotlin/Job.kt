import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val HALF_SECOND = 500L
private val ONE_SECOND = HALF_SECOND * 2
private val THREE_SECOND = ONE_SECOND * 3

fun main() {
    runBlocking {
        val jobOne = launch {
            //delay(THREE_SECOND)
            println("Job 1 started!")

            val jobTwo = launch {
                delay(THREE_SECOND)
                println("Job 2 started!")
            }
            jobTwo.invokeOnCompletion {
                println("Job 2 complete")
            }

            val jobThree = launch {
                delay(THREE_SECOND)
                println("Job 3 started!")
            }
            jobThree.invokeOnCompletion {
                println("Job 3 complete")
            }
        }

        jobOne.invokeOnCompletion {
            println("Job 1 complete")
        }

        delay(500L)
        println("Job 1 will be canceled")
        jobOne.cancel()
    }
}