import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.cio.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

fun main() {
    embeddedServer(CIO, port = 8080) {
        install(WebSockets) {
            pingPeriodMillis = 20_000
        }
        routing {
            get("/") {
                call.respondText("Hello, world!")
            }
            webSocket("/echo") {
                send("Please enter your name")
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    if (receivedText.equals("bye", ignoreCase = true)) {
                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                    } else {
                        send(Frame.Text("Hi, $receivedText!"))
                    }
                }
            }
        }
    }.start(wait = true)
}