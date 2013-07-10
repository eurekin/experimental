package pl.eurekin.ttt.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Rekin
 */
@Entity
public class Match {
    private Long id;

    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
