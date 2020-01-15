package data

import models.QueryFilterValidator
import models.SampleItem

object Filters {
    private val titleStartsWithValidator = object: QueryFilterValidator<SampleItem> {
        override fun isItemValid(item: SampleItem, queryString: String): Boolean {
            return (item.title.toLowerCase().startsWith(queryString.toLowerCase()))
        }
    }

    private val subtitleStartsWithValidator = object: QueryFilterValidator<SampleItem> {
        override fun isItemValid(item: SampleItem, queryString: String): Boolean {
            return (item.title.toLowerCase().startsWith(queryString.toLowerCase()))
        }
    }

    private val titleContainsValidator = object: QueryFilterValidator<SampleItem> {
        override fun isItemValid(item: SampleItem, queryString: String): Boolean {
            return (item.title.toLowerCase().contains(queryString.toLowerCase()))
        }
    }

    private val subtitleContainsValidator = object : QueryFilterValidator<SampleItem> {
        override fun isItemValid(item: SampleItem, queryString: String): Boolean {
            return (item.subtitle.toLowerCase().contains(queryString.toLowerCase()))
        }

    }

    private val subtitleSkipWordValidator = object: QueryFilterValidator<SampleItem> {
        override fun isItemValid(item: SampleItem, queryString: String): Boolean {
            val subtitleWords = item.subtitle.toLowerCase().split(" ")
            val queryWords = queryString.toLowerCase().split(" ")
            var matchedTill = -1
            for (i in subtitleWords.indices) {
                if (subtitleWords[i].startsWith(queryWords[matchedTill + 1])) matchedTill++
                if (matchedTill == queryWords.lastIndex) break
            }
            return matchedTill != -1
        }

    }

    @JvmStatic
    fun getQueryFilterValidators(): List<QueryFilterValidator<SampleItem>> = listOf(
        titleStartsWithValidator,
        subtitleStartsWithValidator,
        titleContainsValidator,
        subtitleContainsValidator,
        subtitleSkipWordValidator
    )

}