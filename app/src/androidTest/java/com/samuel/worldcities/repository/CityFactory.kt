import com.samuel.worldcities.models.City
import com.samuel.worldcities.models.Country
import java.util.concurrent.atomic.AtomicInteger

class CityFactory {
    private val counter = AtomicInteger(0)
    fun createCity(name: String): City {
        val id = counter.incrementAndGet()
        return City(
            name = name,
            id = id,
            lat = 10.toDouble(),
            lng = 10.toDouble(),
            country = Country(id, name, "", "", "", id),
            country_id = id,
            created_at = "",
            local_name = name,
            updated_at = ""
        )
    }
}