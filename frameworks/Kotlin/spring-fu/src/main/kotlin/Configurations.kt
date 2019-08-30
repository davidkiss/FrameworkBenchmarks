import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.runBlocking
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.fu.kofu.configuration
import org.springframework.fu.kofu.r2dbc.r2dbcPostgresql
import org.springframework.fu.kofu.webflux.mustache
import org.springframework.fu.kofu.webflux.webFlux

val dataConfig = configuration {
    beans {
        bean<DbRepository>()
    }
    r2dbcPostgresql {
        host = "tfb-database"
        port = 5432
        database = "hello_world"
        username = "benchmarkdbuser"
        password = "benchmarkdbpass"
    }
    listener<ApplicationReadyEvent> {
        runBlocking {
            ref<DbRepository>().init()
        }
    }
}

@FlowPreview
@ExperimentalCoroutinesApi
val webConfig = configuration {
    beans {
        bean<WebHandler>()
        bean(::routes)
    }
    webFlux {
        port = 8080
        mustache()
        codecs {
            string()
            jackson()
        }
    }
}