import com.github.bhlangonijr.chesslib.game.Game

import java.net.URL
import java.util.logging.Logger
import org.typesense.api.{Client, Configuration, FieldTypes}
import org.typesense.model.{CollectionSchema, Field, ImportDocumentsParameters}
import org.typesense.resources.Node
import play.api.libs.json.JsValue

import java.time.Duration
import java.util
import scala.jdk.CollectionConverters._

class Typesense(url: URL, apiKey: String) extends Engine {
    private val logger: Logger = Logger.getLogger(this.getClass.getName)
    private val protocol = url.getProtocol
    private val host = url.getHost
    private val port = url.getPort
    private val node = new Node(protocol, host, port.toString)
    private val client: Client = {
        val nodeList = new util.ArrayList[Node]()
        nodeList.add(node)
        val configuration = new Configuration(nodeList, Duration.ofSeconds(2), apiKey)
        new Client(configuration)
    }

    private def createIndex(name: String, fields: Map[String, String]): Boolean = {
        if(client.collections().retrieve().toList.exists(_.getName == name))
            client.collections(name).delete()
        val schema = new CollectionSchema()
        schema.name(name)
        schema.fields(fields.map(entry => new Field().name(entry._1).`type`(entry._2)).toList.asJava)
        client.collections().create(schema)
        true
    }

    def indexGame(game: Game): Boolean =
        client.collections("chess").documents().create(GameData(game).asMap)
        true

    def indexGames(games: Iterable[Game]): Boolean =
        val queryParameters = new ImportDocumentsParameters()
        queryParameters.action("create")
        val jsonL = games.foldLeft("")((acc, game) =>
            acc + GameData(game).asJsObject.toString() + "\n"
        )
        client.collections("countries").documents().import_(jsonL, queryParameters)
        true

    def indexDocument(document: JsValue, index: String): Boolean =
        indexDocument(document.toString(), index)


    def indexDocument(document: String, index: String): Boolean = {
        client.collections(index).documents().create(document)
        true
    }

    def close(): Unit = {}

    createIndex("chess", Map(
        "black" -> "string",
        "blackElo" -> "int32",
        "event" -> "string",
        "link" -> "string",
        "mainlineMoves" -> "string",
        "opening" -> "string",
        "positions" -> "string[]",
        "result" -> "string",
        "termination" -> "string",
        "time" -> "string",
        "timestamp" -> "int64",
        "white" -> "string",
        "whiteElo" -> "int32"
    ))
}