package Search
import Storage.Storage, Storage.Player
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.feature.BucketedRandomProjectionLSH
import org.apache.spark.sql.SparkSession

object SearchSimiliar {
  val rootLogger = Logger.getRootLogger()
  rootLogger.setLevel(Level.WARN)
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
  Logger.getLogger("org.spark-project").setLevel(Level.WARN)
  val spark: SparkSession = SparkSession
    .builder()
    .master("local[*]")
    .getOrCreate()
  val storage = new Storage()
  lazy val players_ids = storage.get_ids.slice(200, 2000) //only part of dataset, for performance reasons
  lazy val dataset = spark.createDataFrame(players_ids.map(storage.get_skills)).toDF("id", "features")
  def kNN(targetPlayer: Player, k: Int): Seq[Player] = {
    val target_skills = storage.get_skills(targetPlayer.id)._2
    val brp = new BucketedRandomProjectionLSH()
      .setBucketLength(15.0)
      .setNumHashTables(30)
      .setInputCol("features")
      .setOutputCol("hashes")
    val model = brp.fit(dataset)
    val nearest_players = model
      .approxNearestNeighbors(dataset, target_skills, k + 1)
      .select("id")
      .collect()
      .map(_(0))
      .toList
      .map(_.toString.toInt)
      .filter(_ != targetPlayer.id)
      .take(k)
    nearest_players.map(id => storage.get_by_id(id).get)
  }
  def cluster(n_clusters: Int): Seq[(Int, Seq[Player])] = {
    val kmeans = new KMeans()
      .setK(n_clusters)
      .setFeaturesCol("features")
      .setPredictionCol("prediction")
    val model = kmeans.fit(dataset)
    model
      .transform(dataset)
      .select("prediction", "id")
      .rdd
      .map(x => (x(0), x(1)))
      .collect()
      .toList
      .map(x => (x._1.toString.toInt, x._2.toString.toInt))
      .groupMap(_._1)(x => storage.get_by_id(x._2).get)
      .toList
      .map(x => (x._1, x._2.take(3)))
  }

}
