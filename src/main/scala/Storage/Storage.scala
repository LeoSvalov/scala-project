package Storage

import org.apache.spark.ml.linalg.Vectors

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import doobie._
import doobie.implicits._

class Storage {

  val xa = Transactor.fromDriverManager[IO](
    "org.sqlite.JDBC",
    "jdbc:sqlite:players.db",
    "",
    ""
  )
  val y = xa.yolo

  def get_by_id(id: Int): Option[Player] = {
    sql"""SELECT * FROM "players.player_info" x WHERE x.id=${id}"""
      .query[Player]
      .option
      .transact(xa)
      .unsafeRunSync()
  }

  def get_ids: Seq[Int] = {
    sql"""SELECT x.id FROM "players.player_info" x """
      .query[Int]
      .to[Seq]
      .transact(xa)
      .unsafeRunSync()
  }
  def get_skills(id: Int): (Int, org.apache.spark.ml.linalg.Vector) = {
    (
      id,
      Vectors.dense(
        sql"""SELECT x.skill_value FROM "players.skills" x WHERE x.player_id=${id}"""
          .query[Double]
          .to[Array]
          .transact(xa)
          .unsafeRunSync()
      )
    )
  }

}
