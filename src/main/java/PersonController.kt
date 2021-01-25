import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes


class PersonController(private val personRepository: PersonRepository) {
    private val mapper = jacksonObjectMapper()

    fun endpoints() = "person" bind routes(
        "/" bind Method.POST to ::createPerson,
        "/" bind Method.GET to ::getAllPersons,
        "/{personId}" bind Method.GET to { Response(OK).body("you GET bob") },
        "/" bind Method.PUT to { Response(OK).body("you GET bob") },
        "/" bind Method.DELETE to { Response(OK).body("you DELETE sue") }
    )

    private fun createPerson(request: Request): Response {
        val newPerson = readBody(request)
        personRepository.create(newPerson)
        return Response(OK).body(mapper.writeValueAsString(newPerson))
    }

    private fun getAllPersons(request: Request): Response {
        val persons = personRepository.findAll()
        return Response(OK).body(mapper.writeValueAsString(persons))
    }

    private fun getPersonById(request: Request): Response {
        val personId = request.path("personId")?.toInt()
        val person = personRepository.findById(personId!!)
        return Response(OK).body(mapper.writeValueAsString(person))
    }

    private fun updatePerson(request: Request): Response {
        var newPerson = readBody(request)
        newPerson = personRepository.update(newPerson)
        return Response(OK).body(mapper.writeValueAsString(newPerson))
    }

    private fun readBody(request: Request) = mapper.readValue(request.body.stream, Person::class.java)
}