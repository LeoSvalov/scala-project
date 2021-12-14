package Server
import cats._
import cats.effect._
import org.http4s.circe._
import org.http4s._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.dsl._
import org.http4s.dsl.impl._
import org.http4s.implicits._
import Search._
import Storage._
import org.http4s.blaze.server.BlazeServerBuilder
object Server extends IOApp {
  object PlayerIDQueryParamMatcher extends QueryParamDecoderMatcher[Int]("id")
  object KQueryParamMatcher extends OptionalValidatingQueryParamDecoderMatcher[Int]("k")
  object N_clustersQueryParamMatcher extends OptionalValidatingQueryParamDecoderMatcher[Int]("n")

  def playerRoutes[F[_]: Monad]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._
    val storage = new Storage()
    HttpRoutes.of[F] {
      case GET -> Root / "nextRonaldo" =>
        val ronaldo = storage.get_by_id(0).get
        val players = ronaldo +: SearchSimiliar.kNN(ronaldo, 3)
        Ok(players.asJson)
      case GET -> Root / "nextMessi" =>
        val messi = storage.get_by_id(1).get
        val players = messi +: SearchSimiliar.kNN(messi, 3)
        Ok(players.asJson)
      case GET -> Root / "player" :? PlayerIDQueryParamMatcher(playerID) +& KQueryParamMatcher(maybeK) =>
        val maybeTargetPlayer = storage.get_by_id(playerID)
        maybeTargetPlayer match {
          case Some(targetPlayer) =>
            maybeK match {
              case Some(validated_k) =>
                validated_k.fold(
                  _ => BadRequest("Given k is not valid."),
                  k => {
                    val suggested_players = targetPlayer +: SearchSimiliar.kNN(targetPlayer, k)
                    Ok(suggested_players.asJson)
                  }
                )
              case None =>
                val suggested_players = targetPlayer +: SearchSimiliar.kNN(targetPlayer, 5)
                Ok(suggested_players.asJson)

            }

          case None => BadRequest("Given player id is not valid.")
        }

      case GET -> Root / "cluster" :? N_clustersQueryParamMatcher(maybe_N_clusters) =>
        maybe_N_clusters match {
          case Some(validated_n) =>
            validated_n.fold(
              _ => BadRequest("Given number of clusters is not valid"),
              n => Ok(SearchSimiliar.cluster(n).asJson)
            )
          case None => Ok(SearchSimiliar.cluster(5).asJson)
        }

    }
  }
  def allRoutesComplete[F[_]: Monad]: HttpApp[F] = {
    playerRoutes[F].orNotFound
  }

  override def run(args: List[String]): IO[ExitCode] = {
    BlazeServerBuilder[IO](runtime.compute)
      .bindHttp(8080, "localhost")
      .withHttpApp(allRoutesComplete)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }
}

/*
http://localhost:8080/nextRonaldo
http://localhost:8080/nextMessi
http://localhost:8080/player?id=ID&k=K
http://localhost:8080/cluster?n=N
 * */
