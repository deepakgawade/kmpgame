package org.example.kmpgame

// ─────────────────────────────────────────────────────────────────────────────
// 1. inline
//    The lambda body is copy-pasted at every call site — no anonymous class,
//    no heap allocation.  Perfect for small wrappers called in hot paths.
// ─────────────────────────────────────────────────────────────────────────────

 inline fun <T> retryOnFailure(times: Int, block: () -> T): T? {
    repeat(times) {
        try { return block() } catch (_: Exception) { }
    }
    return null
}

// Usage example (would live in RocketComponent or a ViewModel):
//   val result = retryOnFailure(3) { httpClient.get(url).body<List<RocketLaunch>>() }


// ─────────────────────────────────────────────────────────────────────────────
// 2. noinline
//    One param is kept as a real lambda object so it can be stored, returned,
//    or passed to another function.  The other param is still inlined.
// ─────────────────────────────────────────────────────────────────────────────

inline fun <T> fetchOrFallback(
    noinline onError: (Exception) -> T,   // stored → must be noinline
    block: () -> T                         // inlined at call site
): T = try {
    block()
} catch (e: Exception) {
    onError(e)                             // onError may be stored/passed elsewhere
}

// Usage example:
//   val phrase = fetchOrFallback(
//       onError = { e -> "Network error: ${e.message}" }
//   ) {
//       httpClient.get(url).body<String>()
//   }


// ─────────────────────────────────────────────────────────────────────────────
// 3. crossinline
//    The lambda is passed into ANOTHER lambda (a different execution context).
//    crossinline still inlines the body but bans non-local returns, because the
//    compiler can't guarantee when/where the inner lambda will run.
// ─────────────────────────────────────────────────────────────────────────────

inline fun scheduleTask(crossinline action: () -> Unit) {
    // Wrapping action inside another lambda is only possible with crossinline.
    // A plain inline lambda cannot be captured like this.
    val task = { action() }
    task()
}

// Usage example:
//   scheduleTask {
//       println("Launching rocket sequence…")
//       // return@scheduleTask is fine, but bare `return` would be a compile error
//   }


// ─────────────────────────────────────────────────────────────────────────────
// 4. reified  (bonus — only possible because the function is inline)
//    Normally type parameters are erased at runtime.  inline + reified lets you
//    use T as a real type: is-checks, class literals, filterIsInstance, etc.
// ─────────────────────────────────────────────────────────────────────────────

inline fun <reified T> List<Any>.filterByType(): List<T> = filterIsInstance<T>()

// Usage example — filter a mixed list from an API response:
//   val launches: List<Any> = listOf(RocketLaunch(1,"Crew-1","2021-01-01T00:00Z",true), "stray string")
//   val onlyLaunches: List<RocketLaunch> = launches.filterByType()


// ─────────────────────────────────────────────────────────────────────────────
// Demonstration function — call this from a ViewModel or test to see all four
// ─────────────────────────────────────────────────────────────────────────────

fun demonstrateInlineFunctions() {

    // 1. inline — retry up to 3 times
    val result = retryOnFailure(times = 3) {
        if ((0..1).random() == 0) error("simulated failure") else "Launch data loaded"
    }
    println("retryOnFailure → $result")

    // 2. noinline — onError lambda is stored and reused
    val errorLogger: (Exception) -> String = { e -> "Logged: ${e.message}" }
    val phrase = fetchOrFallback(onError = errorLogger) {
        error("network unavailable")
    }
    println("fetchOrFallback → $phrase")

    // 3. crossinline — action is wrapped inside another lambda
    scheduleTask {
        println("scheduleTask → task executed inside wrapped lambda")
    }

    // 4. reified — type-safe filter at runtime
    val mixed: List<Any> = listOf(
        RocketLaunch(1, "Crew-1", "2021-01-01T00:00Z", true),
        "stray string",
        RocketLaunch(2, "Crew-2", "2021-04-23T09:49Z", true),
        42
    )
    val launches = mixed.filterByType<RocketLaunch>()
    println("filterByType<RocketLaunch> → ${launches.map { it.missionName }}")
}
