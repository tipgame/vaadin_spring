package de.tipgame.backend.data.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "disable_elements")
public class DisableElementsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "element_to_disable")
    private String elementToDisable;

    @Column(name = "date_element_is_disabled")
    private LocalDateTime dateElementIsDisabled;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getElementToDisable() {
        return elementToDisable;
    }

    public void setElementToDisable(String elementToDisable) {
        this.elementToDisable = elementToDisable;
    }

    public LocalDateTime getDateElementIsDisabled() {
        return dateElementIsDisabled;
    }

    public void setDateElementIsDisabled(LocalDateTime dateElementIsDisabled) {
        this.dateElementIsDisabled = dateElementIsDisabled;
    }
}
