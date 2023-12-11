import com.linecorp.armeria.common.SessionProtocol
import com.linecorp.armeria.server.Server
import com.linecorp.armeria.server.logging.AccessLogWriter
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.server.armeria.zio.ArmeriaZioServerInterpreter
import sttp.tapir.ztapir.RichZEndpoint
import zio._

object TapirApp extends ZIOAppDefault {
  implicit val r = zio.Runtime.default

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = f(8888)

  final case class AuthInputData(secret: Option[String], clientIp: Option[String])

  val secureEndpoint =
    endpoint
      .securityIn(cookie[Option[String]]("secret_cookie"))
      .securityIn(clientIp)
      .mapSecurityInTo[AuthInputData]
      .errorOut(statusCode(StatusCode.Unauthorized).and(stringBody("UTF-8")))
      .zServerSecurityLogic(in => ZIO.log(s"Input: $in").as(in))

  val hellocurl =
    secureEndpoint
      .in("hello" / "here" / path[String]("some_id"))
      .out(stringBody("UTF-8"))
      .serverLogic { ctx => in =>
        ZIO.succeed(s"You asked for id $in, yout security context is $ctx")
      }
  val service   = ArmeriaZioServerInterpreter().toService(hello)

  private def acquire(port: Int): Task[Server] = {
    val server = Server
      .builder()
      .port(port, SessionProtocol.HTTP)
      .service(service)
      .accessLogWriter(AccessLogWriter.common, true)
      .build()
    ZIO.fromFutureJava(server.start()).as(server)
  }

  private def release(server: Server): Task[Unit] = ZIO.fromFutureJava(server.stop()) *> ZIO.logInfo("Server Stopped")

  def f(port: Int)(implicit r: zio.Runtime[Any]): Task[Unit] = ZIO.acquireReleaseWith(acquire(port))(release(_).orDie)(_ => ZIO.never)

}
