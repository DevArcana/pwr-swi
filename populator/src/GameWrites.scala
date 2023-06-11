import com.github.bhlangonijr.chesslib.game.Game
import scala.beans.BeanProperty
import play.api.libs.json.*


object GameWrites {
    implicit val implicitGameWrites: Writes[Game] = (game: Game) => GameData(game).asJsObject
}
