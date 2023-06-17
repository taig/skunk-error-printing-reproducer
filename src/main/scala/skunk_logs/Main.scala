package skunk_logs

import cats.effect.{IO, IOApp, Resource, ResourceApp}
import cats.syntax.all.*
import com.github.dockerjava.api.model.{ExposedPort, HostConfig, PortBinding, Ports}
import org.testcontainers.containers.PostgreSQLContainer
import skunk.Session
import natchez.Trace.Implicits.noop

def session(port: Option[Int]): Resource[IO, Session[IO]] = for
  container <- container(port)
  port <- Resource.eval(IO(Int.unbox(container.getFirstMappedPort)))
  sessions <- Session.single[IO](
    "localhost",
    port,
    container.getUsername,
    container.getDatabaseName,
    container.getPassword.some
  )
yield sessions

def container(port: Option[Int]): Resource[IO, PostgreSQLContainer[?]] =
  val postgres = IO.blocking:
    val postgres = new PostgreSQLContainer(s"postgres:15")
    postgres.withUsername("postgres")
    postgres.withPassword("postgres")
    postgres.withDatabaseName("app")
    port.foreach: port =>
      postgres.withExposedPorts(port)
      postgres.withCreateContainerCmdModifier: cmd =>
        val binding = new PortBinding(Ports.Binding.bindPort(port), new ExposedPort(port))
        cmd.withHostConfig(new HostConfig().withPortBindings(binding))
        ()
      ()
    postgres

  Resource.fromAutoCloseable(postgres).evalTap(container => IO.blocking(container.start()))

object Main extends ResourceApp.Simple:
  def dangerousQuery(session: Session[IO]): IO[Unit] = session.transaction.use: _ =>
    IO.raiseError(new RuntimeException("This error will be handled and shouldn't be printed"))

  override def run: Resource[IO, Unit] = for
    session <- session(port = none)
    _ <- Resource.eval(dangerousQuery(session).handleErrorWith { _ =>
      IO.println("Handling the query error, so it definitely shouldn't be printed")
    })
  yield ()
