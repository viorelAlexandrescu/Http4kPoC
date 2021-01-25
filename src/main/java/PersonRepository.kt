import javax.persistence.EntityManager
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root


class PersonRepository(private val em: EntityManager) {

    fun create(person: Person): Person {
        em.transaction.begin()
        em.persist(person)
        em.transaction.commit()
        return person
        //em.close()
    }

    fun findById(id: Int): Person? {
        return em.find(Person::class.java, id)
    }

    fun findAll(): List<Person>? {
        val cb: CriteriaBuilder = em.criteriaBuilder
        val cq: CriteriaQuery<Person> = cb.createQuery(Person::class.java)
        val rootEntry: Root<Person> = cq.from(Person::class.java)
        val all: CriteriaQuery<Person> = cq.select(rootEntry)

        val allQuery: TypedQuery<Person> = em.createQuery(all)
        return allQuery.resultList
    }

    fun update(person: Person): Person? {
        em.transaction.begin()
        val updatedPerson = em.merge(person)
        em.transaction.commit()
        return updatedPerson
        //em.close()
    }

    fun delete(person: Person): Person {
        em.transaction.begin()
        em.remove(person)
        em.transaction.commit()
        return person
        //em.close()
    }
}