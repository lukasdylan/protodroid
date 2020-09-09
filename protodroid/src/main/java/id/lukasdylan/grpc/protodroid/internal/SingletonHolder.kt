package id.lukasdylan.grpc.protodroid.internal

internal open class SingletonHolder<out T : Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T {
        val checkInstance = instance
        if (checkInstance != null) {
            return checkInstance
        }

        return synchronized(this) {
            val checkInstanceAgain = instance
            if (checkInstanceAgain != null) {
                checkInstanceAgain
            } else {
                val created = creator?.let { it(arg) }
                instance = created
                creator = null
                created ?: throw Throwable("Not initialized")
            }
        }
    }

    fun getInstance(): T? = instance
}