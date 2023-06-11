import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch.core.{IndexRequest, IndexResponse}
import co.elastic.clients.transport.rest_client.RestClientTransport
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.elasticsearch.core.BulkRequest
import com.github.bhlangonijr.chesslib.game.Game
import org.apache.http.HttpHost
import org.apache.http.entity.ContentType
import org.elasticsearch.client.RestClient
import play.api.libs.json.{JsValue, Json}

import java.io.StringReader
import java.net.URL
import java.util.logging.{Level, Logger}

class ElasticSearch(url: URL) extends Engine {
    private val logger: Logger = Logger.getLogger(this.getClass.getName)
    private val host = url.getHost
    private val port = url.getPort
    private val restClient: RestClient = RestClient.builder(new HttpHost(host, port)).build()
    private val esClient: ElasticsearchClient = {
        val transport = new RestClientTransport(restClient, new JacksonJsonpMapper)
        new ElasticsearchClient(transport)
    }

    def indexGame(game: Game): Boolean =
        esClient.index(idx => idx.index("chess").document(GameData(game).asMap)).version() > 0


    def indexGames(games: Iterable[Game]): Boolean =
        val br = new BulkRequest.Builder()
        games.foreach(game =>
            br.operations(op => op.index(idx => idx.index("chess").document(GameData(game).asMap)))
        )
        esClient.bulk(br.build()).errors()

    def indexDocument(document: JsValue, index: String): Boolean =
        indexDocument(document.toString(), index)


    def indexDocument(document: String, index: String): Boolean = {
        val request = IndexRequest.of(_.index(index).withJson(new StringReader(document)))
        esClient.index(request).version() > 0
    }

    def close(): Unit = restClient.close()
}
