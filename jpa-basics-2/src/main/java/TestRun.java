import pl.eurekin.ttt.entities.Player;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;

/**
 * @author Rekin
 */
public class TestRun {

    private static EntityManager entityManager;

    public static void main(String[] args) {
        PersistenceProvider persistenceProvider = new org.eclipse.persistence.jpa.PersistenceProvider();
        EntityManagerFactory entityManagerFactory = persistenceProvider.createEntityManagerFactory("NewPersistenceUnit", null);
        entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();
        persist();
        entityManager.getTransaction().commit();
        entityManager.close();

    }

    private static void persist() {
        Player player = new Player();
        player.setName("name");
        entityManager.persist(player);
    }
}

