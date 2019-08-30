import kotlinx.coroutines.*
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyAndAwait
import java.util.*
import java.util.concurrent.ThreadLocalRandom

internal const val WORLD_ROWS: Int = 10_000
internal fun randomWorld(): Int = ThreadLocalRandom.current().nextInt(WORLD_ROWS) + 1

@FlowPreview
@ExperimentalCoroutinesApi
@Suppress("UNUSED_PARAMETER")
class WebHandler(
        private val repository: DbRepository) {

    suspend fun plaintext(request: ServerRequest) =
            ok().contentType(MediaType.TEXT_PLAIN).bodyAndAwait("Hello, World!")

    suspend fun json(request: ServerRequest) =
            ok().contentType(MediaType.APPLICATION_JSON).bodyAndAwait(mapOf("message" to "Hello, World!"))

    suspend fun db(request: ServerRequest) =
            ok().contentType(MediaType.APPLICATION_JSON).bodyAndAwait(repository.getWorld(randomWorld()))

    suspend fun queries(request: ServerRequest) = coroutineScope {
        val queries = getQueries(request);

        val worlds = Array(queries) { async { repository.getWorld(randomWorld()) } };

        runBlocking {
            val results = worlds.map { it.await() }
            ok().contentType(MediaType.APPLICATION_JSON).bodyAndAwait(results);
        }
    }

    private fun parseQueryCount(maybeTextValue: Optional<String>): Int {
        if (!maybeTextValue.isPresent) {
            return 1
        }
        val parsedValue: Int
        try {
            parsedValue = Integer.parseInt(maybeTextValue.get())
        } catch (e: NumberFormatException) {
            return 1
        }

        return Math.min(500, Math.max(1, parsedValue))
    }

    private fun getQueries(request: ServerRequest): Int {
        return parseQueryCount(request.queryParam("queries"))
    }
}