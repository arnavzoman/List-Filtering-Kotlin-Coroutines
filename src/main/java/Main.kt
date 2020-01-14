import data.Filters
import data.Items
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import models.QueryFilterValidator


fun main() {
    val items = Items.getItems()
    val filterValidators = Filters.getQueryFilterValidators()
    val queryString = "ab"

    runBlocking {
        launch(Dispatchers.IO) {
            val start = System.currentTimeMillis()
            filterListUnordered(items, queryString, *(filterValidators.toTypedArray()))
            filterListUnordered(items, queryString, *(filterValidators.toTypedArray()))
            filterListUnordered(items, queryString, *(filterValidators.toTypedArray()))
            filterListUnordered(items, queryString, *(filterValidators.toTypedArray()))
            filterListUnordered(items, queryString, *(filterValidators.toTypedArray()))
            filterListUnordered(items, queryString, *(filterValidators.toTypedArray()))
            println("TIME TAKEN  = = = = ${System.currentTimeMillis() - start}")
        }
    }
}

@Suppress("EXPERIMENTAL_API_USAGE")
suspend fun <T> filterListUnordered(list: List<T>, queryString: String, vararg filters: QueryFilterValidator<T>) {
    println("Start")
    val finalList = mutableListOf<T>()
    val filterChannels = hashMapOf(
        *(filters.map { Pair(it, Channel<T>(8)) }).toTypedArray()
    )
    coroutineScope {
        val collector = newSingleThreadContext("collector")
        val filteringDispatcher = newFixedThreadPoolContext(4, "filter")

        filterChannels.forEach { (_, channel) ->
            launch {
                channel.consumeEach {
                    launch(collector) {
                        println("adding to list $it on ${Thread.currentThread().name}")
                        finalList.add(it)
                    }
                }
                channel.invokeOnClose {
                    collector.close()
                }
            }
        }

        list.mapIndexed { index, item ->
            launch(filteringDispatcher) {
                for (filter in filters) {
                    if (filter.isItemValid(item, queryString)) {
                        println("${item}, ${filter}, ${Thread.currentThread().name}")
                        filterChannels[filter]?.send(item)
                        break
                    }
                }
                if (index == list.lastIndex) {
                    delay(1)
                    filters.forEach { filter ->
                        println("Closing channel $filter ${Thread.currentThread().name}")
                        filterChannels[filter]?.close()
                    }
                    filteringDispatcher.close()
                }
            }


        }
    }

    println("done ${finalList.size}")
    println(finalList)
}

@Suppress("EXPERIMENTAL_API_USAGE")
suspend fun <T> filterListOrdered(list: List<T>, queryString: String, vararg filters: QueryFilterValidator<T>) {
    println("Start")
    val finalList = mutableListOf<T>()
    val filterChannels = hashMapOf(
        *(filters.map { Pair(it, Channel<T>(8)) }).toTypedArray()
    )
    val filteredLists = hashMapOf(
        *(filters.map { Pair(it, mutableListOf<T>()) }).toTypedArray()
    )

    coroutineScope {
        val collector = newSingleThreadContext("collector")
        val filteringDispatcher = newFixedThreadPoolContext(4, "filter")

        filterChannels.forEach { (validator, channel) ->
            launch {
                channel.consumeEach {
                    launch(collector) {
                        println("adding to list $it on ${Thread.currentThread().name}")
                        filteredLists[validator]?.add(it)
                    }
                }
                channel.invokeOnClose {
                    collector.close()
                }
            }
        }

        list.mapIndexed { index, item ->
            launch(filteringDispatcher) {
                for (filter in filters) {
                    if (filter.isItemValid(item, queryString)) {
                        println("${item}, ${filter}, ${Thread.currentThread().name}")
                        filterChannels[filter]?.send(item)
                        break
                    }
                }
                if (index == list.lastIndex) {
                    filters.forEach { filter ->
                        println("Closing channel $filter ${Thread.currentThread().name}")
                        filterChannels[filter]?.close()
                    }
                    filteringDispatcher.close()
                }
            }


        }
    }

    coroutineScope {
        filteredLists.forEach { (_, sublist) ->
            sublist.asFlow().collect {
                println("Adding to final list ${Thread.currentThread().name}")
                finalList.add(it)
            }
        }
    }

    println("done ${finalList.size}")
    println(finalList)
}