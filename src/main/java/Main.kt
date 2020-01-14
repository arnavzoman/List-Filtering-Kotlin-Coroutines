import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach

private fun getItems() = listOf<Item>(
    Item("Ecratic", "Forest Place"),
    Item("Besto", "Linden Boulevard"),
    Item("Essensia", "Bay Parkway"),
    Item("Medesign", "Revere Place"),
    Item("Mitroc", "Windsor Place"),
    Item("Lumbrex", "Hopkins Street"),
    Item("Eclipsent", "Jamaica Avenue"),
    Item("Quintity", "Crosby Avenue"),
    Item("Imkan", "Arion Place"),
    Item("Pearlesex", "Montgomery Street"),
    Item("Unisure", "Meeker Avenue"),
    Item("Biotica", "Bristol Street"),
    Item("Calcu", "Wyona Street"),
    Item("Egypto", "Highland Boulevard"),
    Item("Coash", "Ridge Court"),
    Item("Unq", "Friel Place"),
    Item("Comtrek", "Woodrow Court"),
    Item("Kongle", "Atkins Avenue"),
    Item("Fossiel", "Havemeyer Street"),
    Item("Magneato", "Applegate Court"),
    Item("Vidto", "Granite Street"),
    Item("Signidyne", "Menahan Street"),
    Item("Kyaguru", "John Street"),
    Item("Elpro", "Morton Street"),
    Item("Orbiflex", "Karweg Place"),
    Item("Twiist", "Bethel Loop"),
    Item("Buzzworks", "Lincoln Place"),
    Item("Empirica", "Tudor Terrace"),
    Item("Telpod", "Bleecker Street"),
    Item("Valpreal", "Girard Street"),
    Item("Adornica", "Robert Street"),
    Item("Kidstock", "McClancy Place"),
    Item("Keengen", "Cook Street"),
    Item("Steeltab", "Banner Avenue"),
    Item("Printspan", "Apollo Street"),
    Item("Dogspa", "Malbone Street"),
    Item("Ecosys", "Dakota Place"),
    Item("Qaboos", "Miller Avenue"),
    Item("Telequiet", "Brightwater Avenue"),
    Item("Rubadub", "Goodwin Place"),
    Item("Talae", "Belmont Avenue"),
    Item("Naxdis", "Gold Street"),
    Item("Trasola", "Borinquen Pl"),
    Item("Harmoney", "Hudson Avenue"),
    Item("Hydrocom", "Williams Place"),
    Item("Zillanet", "Luquer Street"),
    Item("Globoil", "Glenmore Avenue"),
    Item("Futurize", "Narrows Avenue"),
    Item("Comverges", "McKinley Avenue"),
    Item("Rocklogic", "Branton Street"),
    Item("Xplor", "Doughty Street"),
    Item("Geekology", "Prince Street"),
    Item("Isostream", "Debevoise Street"),
    Item("Zilch", "Wolcott Street"),
    Item("Icology", "Beadel Street"),
    Item("Snips", "Autumn Avenue"),
    Item("Photobin", "Harrison Place"),
    Item("Snowpoke", "Seton Place"),
    Item("Calcu", "Suydam Place"),
    Item("Quilk", "Garden Street"),
    Item("Barkarama", "Legion Street"),
    Item("Skyplex", "Decatur Street"),
    Item("Limozen", "Shale Street"),
    Item("Flyboyz", "Arion Place"),
    Item("Tetratrex", "Division Avenue"),
    Item("Roughies", "Pulaski Street"),
    Item("Amril", "Albemarle Terrace"),
    Item("Zizzle", "Midwood Street"),
    Item("Nspire", "Lawrence Avenue"),
    Item("Netility", "Knapp Street"),
    Item("Rodemco", "Waldane Court"),
    Item("Zilidium", "Nixon Court"),
    Item("Frosnex", "Verona Place"),
    Item("Corpulse", "George Street"),
    Item("Remotion", "Nautilus Avenue"),
    Item("Intergeek", "John Street"),
    Item("Apextri", "Luquer Street"),
    Item("Quordate", "Clifford Place"),
    Item("Digirang", "Front Street"),
    Item("Wazzu", "Matthews Court"),
    Item("Isotrack", "Clinton Street"),
    Item("Boilicon", "Preston Court"),
    Item("Furnafix", "Colby Court"),
    Item("Idetica", "Poplar Street"),
    Item("Syntac", "Elliott Place"),
    Item("Musanpoly", "Lyme Avenue"),
    Item("Quantasis", "Howard Place"),
    Item("Gadtron", "Granite Street"),
    Item("Slax", "Gerritsen Avenue"),
    Item("Artiq", "Lott Street"),
    Item("Zillidium", "Exeter Street"),
    Item("Cubicide", "Chase Court"),
    Item("Kenegy", "Wythe Place"),
    Item("Kengen", "Dakota Place"),
    Item("Accusage", "Duryea Place"),
    Item("Viagreat", "Lorraine Street"),
    Item("Interodeo", "Ridge Boulevard"),
    Item("Egypto", "Independence Avenue"),
    Item("Geekola", "Abbey Court"),
    Item("Qiao", "School Lane"),
    Item("Acruex", "Truxton Street"),
    Item("Exodoc", "Hastings Street"),
    Item("Brainquil", "Little Street"),
    Item("Intrawear", "Opal Court"),
    Item("Terrasys", "Harway Avenue"),
    Item("Daisu", "Ash Street"),
    Item("Vortexaco", "Martense Street"),
    Item("Silodyne", "Hillel Place"),
    Item("Canopoly", "Madison Place"),
    Item("Biohab", "Conduit Boulevard"),
    Item("Elemantra", "Locust Street"),
    Item("Miracula", "Ingraham Street"),
    Item("Idealis", "Polhemus Place"),
    Item("Medicroix", "Guider Avenue"),
    Item("Isologics", "Norfolk Street"),
    Item("Ramjob", "Bushwick Place"),
    Item("Zensor", "Visitation Place"),
    Item("Extragen", "Dunne Place"),
    Item("Mantrix", "Homecrest Court"),
    Item("Animalia", "Taaffe Place"),
    Item("Blanet", "Hyman Court"),
    Item("Dancerity", "Keen Court"),
    Item("Cytrek", "Brighton Avenue"),
    Item("Slumberia", "Dewey Place"),
    Item("Virxo", "Rugby Road"),
    Item("Surelogic", "Dooley Street"),
    Item("Enervate", "Lefferts Place"),
    Item("Ludak", "Boerum Street"),
    Item("Geekwagon", "Riverdale Avenue"),
    Item("Ginkogene", "Flatlands Avenue"),
    Item("Pasturia", "Wallabout Street"),
    Item("Portica", "Adams Street"),
    Item("Genekom", "Monitor Street"),
    Item("Xeronk", "Herzl Street")
)

val DEBUG = false
private fun debugln(message: Any) = if (DEBUG) println(message) else { /* noop*/ }

fun main() {
    val items = getItems()
    val nameFilter = object : QueryFilterValidator<Item> {
        override fun isItemValid(item: Item, queryString: String): Boolean {
            debugln("title = ${item.title}, query = ${queryString}, thread = ${Thread.currentThread().name}")
            return item.title.endsWith(queryString)
        }
    }
    val descFilter = object : QueryFilterValidator<Item> {
        override fun isItemValid(item: Item, queryString: String): Boolean {
            debugln("desc = ${item.description}, query = ${queryString}, thread = ${Thread.currentThread().name}")
            return item.description.endsWith(queryString)
        }
    }
    val descFilter2 = object : QueryFilterValidator<Item> {
        override fun isItemValid(item: Item, queryString: String): Boolean {
            debugln("desc = ${item.description}, query = ${queryString}, thread = ${Thread.currentThread().name}")
            return item.description.endsWith(queryString)
        }
    }
    val descFilter3 = object : QueryFilterValidator<Item> {
        override fun isItemValid(item: Item, queryString: String): Boolean {
            debugln("desc = ${item.description}, query = ${queryString}, thread = ${Thread.currentThread().name}")
            return item.description.endsWith(queryString)
        }
    }

    runBlocking {
        launch(Dispatchers.IO) {
            filterList(items, "Street", nameFilter, descFilter, descFilter2, descFilter3)
        }
    }
}

@Suppress("EXPERIMENTAL_API_USAGE")
suspend fun <T> filterList(list: List<T>, queryString: String, vararg filters: QueryFilterValidator<T>) {
    println("Start")
    val finalList = mutableListOf<T>()
    val filterChannels = hashMapOf(
        *(filters.map { Pair(it, Channel<T>(8)) }).toTypedArray()
    )
    coroutineScope {
        val collector = newSingleThreadContext("collector")
        val filteringDispatcher = newFixedThreadPoolContext(4, "filter")

        filterChannels.forEach { (_, channel) ->
            launch {
                channel.consumeEach {
                    launch(collector) {
                        println("adding to list $it on ${Thread.currentThread().name}")
                        finalList.add(it)
                    }
                }
                channel.invokeOnClose {
                    collector.close()
                }
            }
        }

        list.mapIndexed { index, item ->
            launch(filteringDispatcher) {
                for (filter in filters) {
                    if (filter.isItemValid(item, queryString)) {
                        println("${item}, ${filter}, ${Thread.currentThread().name}")
                        filterChannels[filter]?.send(item)
                        break
                    }
                }
                if (index == list.lastIndex) {
                    filters.forEach { filter ->
                        println("Closing channel $filter ${Thread.currentThread().name}")
                        filterChannels[filter]?.close()
                    }
                    filteringDispatcher.close()
                }
            }


        }
    }

    println("done ${finalList.size}")
    println(finalList)
}

data class Item(val title: String, val description: String)