import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.r2dbc.core.*

class DbRepository(private val client: DatabaseClient) {

    @FlowPreview
    fun findAllFortunes() =
            client.execute().sql("SELECT * FROM fortune").asType<Fortune>().fetch().flow()

    suspend fun getWorld(id: Int) =
            client.execute().sql("SELECT * FROM world WHERE id = ?").bind(1, id).asType<World>().fetch().awaitOne()

    suspend fun updateWorld(world: World): World =
            client.execute()
                    .sql("UPDATE world SET randomnumber=$2 WHERE id = $1")
                    .bind("$1", world.id)
                    .bind("$2", world.randomNumber)
                    .fetch()
                    .rowsUpdated()
                    .map { world }
                    .awaitSingle()

    suspend fun findAndUpdateWorld(id: Int, randomNumber: Int): World? {
        val world = getWorld(id);
        world.randomNumber = randomNumber;
        return updateWorld(world);
    }

    suspend fun init() {
        client.execute().sql("CREATE TABLE IF NOT EXISTS users (id number PRIMARY KEY, randomNumber number)").await()
        for (i in (0..1000)) {
            client.insert().into<World>().table("world").using(World(id = i, randomNumber = i)).await()
        }
    }
}