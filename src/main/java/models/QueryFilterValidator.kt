package models
interface QueryFilterValidator<T> {
    fun isItemValid(item: T, queryString: String): Boolean
}

