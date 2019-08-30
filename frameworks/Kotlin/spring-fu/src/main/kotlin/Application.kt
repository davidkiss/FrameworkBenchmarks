import kotlinx.coroutines.FlowPreview
import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application

@FlowPreview
val app = application(WebApplicationType.REACTIVE) {
    configurationProperties<DbProperties>(prefix = "database")
    enable(dataConfig)
    enable(webConfig)
}

@FlowPreview
fun main() {
    app.run()
}