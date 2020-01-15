import data.Filters
import data.Items
import kotlinx.coroutines.*

val collector = newSingleThreadContext("collector")
val filteringDispatcher = newFixedThreadPoolContext(4, "filter")

fun main() {
    val items = Items.getItems()
    val filterValidators = Filters.getQueryFilterValidators()
    val queryString = "mo lo"

    runBlocking {
        launch(Dispatchers.IO) {
            delay(1000)
            val start = System.currentTimeMillis()
            repeat(100) {
                filterListOrdered(items, queryString, *(filterValidators.toTypedArray()))
            }
            println("TIME TAKEN  = = = = ${System.currentTimeMillis() - start}")
        }
    }
}

