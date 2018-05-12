package de.tipgame.backend.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "final_results")
public class FinalResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    private String winner;

    @Column(name="result_germany")
    private String resultGermany;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getResultGermany() {
        return resultGermany;
    }

    public void setResultGermany(String resultGermany) {
        this.resultGermany = resultGermany;
    }
}
