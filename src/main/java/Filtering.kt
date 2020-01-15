import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import models.QueryFilterValidator

fun <T> filterListMainThread(list: List<T>, queryString: String, vararg filters: QueryFilterValidator<T>) {
    val finalList = mutableListOf<T>()
    val filteredLists = hashMapOf(
        *(filters.map { Pair(it, mutableListOf<T>()) }).toTypedArray()
    )

    list.mapIndexed { index, item ->
        for (filter in filters) {
            if (filter.isItemValid(item, queryString)) {
                println("${item}, ${filter}, ${Thread.currentThread().name}")
                filteredLists[filter]?.add(item)
                break
            }
        }
    }
    filters.forEach { filter ->
        filteredLists[filter]?.asSequence()?.forEach {
            println("Adding to final list ${Thread.currentThread().name}")
            finalList.add(it)
        }
    }

    println("done ${finalList.size}")
    println(finalList)

}

@Suppress("EXPERIMENTAL_API_USAGE")
suspend fun <T> filterListUnordered(list: List<T>, queryString: String, vararg filters: QueryFilterValidator<T>) {
    println("Start")
    val finalList = mutableListOf<T>()
    val filterChannels = hashMapOf(
        *(filters.map { Pair(it, Channel<T>(8)) }).toTypedArray()
    )
    coroutineScope {
        filterChannels.forEach { (_, channel) ->
            launch {
                channel.consumeEach {
                    launch(collector) {
                        println("adding to list $it on ${Thread.currentThread().name}")
                        finalList.add(it)
                    }
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

        filterChannels.forEach { (validator, channel) ->
            launch {
                channel.consumeEach {
                    launch(collector) {
                        println("adding to list $it on ${Thread.currentThread().name}")
                        filteredLists[validator]?.add(it)
                    }
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
                }
            }


        }
    }

    coroutineScope {
        filters.forEach { filter ->
            filteredLists[filter]?.asFlow()?.collect {
                println("Adding to final list ${Thread.currentThread().name}")
                finalList.add(it)
            }
        }
    }

    println("done ${finalList.size}")
    println(finalList)
}