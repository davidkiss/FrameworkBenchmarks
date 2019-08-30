import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.springframework.web.reactive.function.server.coRouter

@FlowPreview
@ExperimentalCoroutinesApi
fun routes(webHandler: WebHandler) = coRouter {
    GET("/plaintext", webHandler::plaintext)
    GET("/json", webHandler::json)
    GET("/db", webHandler::db)
    GET("/queries", webHandler::queries)
}