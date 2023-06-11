import java.util.logging.{Level, Logger}
import java.net.URL
import scala.collection.parallel.CollectionConverters._
import scala.jdk.CollectionConverters._
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors
import scala.concurrent.duration.Duration
import java.io.{File, StringReader}
import com.github.bhlangonijr.chesslib.pgn.PgnIterator
import play.api.libs.json._
import com.github.luben.zstd.Zstd
import org.apache.commons.io.IOUtils



val engineUrl: URL =  new URL("http://localhost:8108")


object Main {
    val logger: Logger = Logger.getLogger(this.getClass.getName)
    logger.info(f"Connecting to engine")
    val client: Engine = new Typesense(engineUrl, "xyz")
    

    @main
    def main(): Unit = {
        loadPgns()
        client.close()
    }

    def loadPgns(): Unit = {

        logger.info("Starting reading pgn files")
        val pgns = new File("../pgns")
            .listFiles()
            .filter(file => file.isFile && file.getName.endsWith(".pgn"))
        logger.info(f"Found ${pgns.length} pgn files")

        logger.info(f"Starting traversing games")
        pgns.par.foreach(file =>
            logger.fine(f"Starting traversing games from file ${file.getName}")
            val pgnIterator = new PgnIterator(file.getPath).asScala//.grouped(10)
            //noinspection LanguageFeature
            logger.info(f"Starting indexing games from file ${file.getName}")
            pgnIterator.foreach(client.indexGame/*s*/)
        )
        //noinspection LanguageFeature
        logger.info("Finished loading")
    }
    
}
