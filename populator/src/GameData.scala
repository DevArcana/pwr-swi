import scala.jdk.CollectionConverters.*
import com.github.bhlangonijr.chesslib.game.Game
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.move.MoveList
import play.api.libs.json.*

import java.beans.BeanProperty
import java.time.{LocalDateTime, ZoneOffset}


case class GameData(game: Game) {
    @BeanProperty
    val date: Long = LocalDateTime.parse(
        f"${game.getProperty.get("UTCDate").replace(".", "-")}T${game.getProperty.get("UTCTime")}"
    ).toEpochSecond(ZoneOffset.UTC)

    @BeanProperty val site: String = game.getRound.getEvent.getSite
    @BeanProperty val time: String = game.getRound.getEvent.getTimeControl.toString
    @BeanProperty val white: String = game.getWhitePlayer.getName
    @BeanProperty val whiteElo: Int = game.getWhitePlayer.getElo
    @BeanProperty val black: String = game.getBlackPlayer.getName
    @BeanProperty val blackElo: Int = game.getBlackPlayer.getElo
    @BeanProperty val moves: String = game.getCurrentMoveList.toSanWithMoveNumbers
    @BeanProperty val result: String = game.getResult.getDescription
    @BeanProperty val opening: String = game.getOpening.replace(":", "-")
    @BeanProperty val event: String = game.getRound.getEvent.getName
    @BeanProperty val termination: String = game.getTermination.value()

    def asJsObject: JsObject =
        JsObject(Seq(
            "timestamp" -> JsNumber(date),
            "white" -> JsString(white),
            "whiteElo" -> JsNumber(whiteElo),
            "black" -> JsString(black),
            "blackElo" -> JsNumber(blackElo),
            "link" -> JsString(site),
            "time" -> JsString(time),
            "mainlineMoves" -> JsString(moves),
            "positions" -> {
                var fens = new JsArray
                game.loadMoveText()
                val moves = game.getHalfMoves.asScala
                val board = new Board
                fens = fens :+ JsString(board.getFen)
                //Replay all the moves from the game and print the final position in FEN format
                for (move <- moves) {
                    board.doMove(move)
                    fens = fens :+ JsString(board.getFen)
                }
                fens
            },
            "opening" -> JsString(opening),
            "result" -> JsString(result),
            "event" -> JsString(event),
            "termination" -> JsString(termination)
        ))

    def asMap: java.util.Map[String, Any] = Map(
        "timestamp" -> date,
        "white" -> white,
        "whiteElo" -> whiteElo,
        "black" -> black,
        "blackElo" -> blackElo,
        "link" -> site,
        "time" -> time,
        "mainlineMoves" -> moves,
        "positions" -> {
            var fens = List.empty[String]
            game.loadMoveText()
            val moves = game.getHalfMoves.asScala
            val board = new Board
            fens ::= board.getFen
            //Replay all the moves from the game and print the final position in FEN format
            for (move <- moves) {
                board.doMove(move)
                fens ::= board.getFen
            }
            fens.reverse.toArray
        },
        "opening" -> opening,
        "result" -> result,
        "event" -> event,
        "termination" -> termination
    ).asJava

}