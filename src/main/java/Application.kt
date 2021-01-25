import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration
import org.hibernate.cfg.Environment
import org.http4k.server.Netty
import org.http4k.server.asServer
import java.util.*

fun main() {
    val properties = propertiesFromResource("/database.properties")

    val configuration = buildHibernateConfiguration(properties.toHibernateProperties(), Person::class.java)
    val sessionFactory = buildSessionFactory(configuration)
    addHibernateShutdownHook(sessionFactory)

    val personRepository = PersonRepository(sessionFactory.createEntityManager())
    val personController = PersonController(personRepository)
    personController.endpoints().asServer(Netty(9000)).start()
}

fun propertiesFromResource(resource: String): Properties {
    val properties = Properties()
    properties.load(Any::class.java.getResourceAsStream(resource))
    return properties
}

fun Properties.toHibernateProperties(): Properties {
    val hibernateProperties = Properties()
    hibernateProperties[Environment.DRIVER] = this["driver"]
    hibernateProperties[Environment.URL] = this["url"]
    hibernateProperties[Environment.USER] = this["user"]
    hibernateProperties[Environment.PASS] = this["pass"]
    hibernateProperties[Environment.DIALECT] = this["dialect"]
    hibernateProperties[Environment.SHOW_SQL] = this["showSql"]
    hibernateProperties[Environment.FORMAT_SQL] = this["formatSql"]
    hibernateProperties[Environment.CURRENT_SESSION_CONTEXT_CLASS] = this["currentSessionContextClass"]
    hibernateProperties[Environment.HBM2DDL_AUTO] = this["ddlAuto"]

    return hibernateProperties
}

fun buildHibernateConfiguration(hibernateProperties: Properties, vararg annotatedClasses: Class<*>): Configuration {
    val configuration = Configuration()
    configuration.properties = hibernateProperties
    annotatedClasses.forEach { configuration.addAnnotatedClass(it) }
    return configuration
}

fun buildSessionFactory(configuration: Configuration): SessionFactory {
    val serviceRegistry = StandardServiceRegistryBuilder().applySettings(configuration.properties).build()
    return configuration.buildSessionFactory(serviceRegistry)
}

fun addHibernateShutdownHook(sessionFactory: SessionFactory)  {
    Runtime.getRuntime().addShutdownHook(object: Thread() {
        override fun run() {
            println("Closing the sessionFactory...")
            sessionFactory.close()
            println("sessionFactory closed successfully...")
        }
    })
}