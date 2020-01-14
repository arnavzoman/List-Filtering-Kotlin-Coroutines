package data

import models.QueryFilterValidator
import models.SampleItem

object Filters {
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

    @JvmStatic
    fun getQueryFilterValidators(): List<QueryFilterValidator<SampleItem>> = listOf(
        titleContainsValidator,
        subtitleContainsValidator
    )

}