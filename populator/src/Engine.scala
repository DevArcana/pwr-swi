import com.github.bhlangonijr.chesslib.game.Game
import play.api.libs.json.JsValue

trait Engine {
    def indexGame(game: Game): Boolean

    def indexGames(games: Iterable[Game]): Boolean

    def indexDocument(document: JsValue, index: String): Boolean

    def indexDocument(document: String, index: String): Boolean

    def close(): Unit
}
