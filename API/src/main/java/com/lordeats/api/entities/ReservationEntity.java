package com.lordeats.api.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.*;
import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="reservation")
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="address", nullable = false)
    private String address;

    @Column(name="fonNumber")
    private String fonNumber;

    @Column(name="emailAddress")
    private String emailAddress;

    @Column(name="ratingPoints")
    private String ratingPoints;

    @Column(name="webPage")
    private String webPage;

    @Column(name="price")
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    public ReservationEntity(String name, String address, BigDecimal price, CustomerEntity customer) {
        this.name = name;
        this.address = address;
        this.price = price;
        this.customer = customer;
    }

    public JSONObject reservationToJsonObject(){
        JSONObject reservationJ = new JSONObject();
        try {
            reservationJ.put("id", this.id);
            reservationJ.put("name", this.name);
            reservationJ.put("address", this.address);
            reservationJ.put("fonNumber", this.fonNumber);
            reservationJ.put("emailAddress", this.emailAddress);
            reservationJ.put("ratingPoints", this.ratingPoints);
            reservationJ.put("webPage", this.webPage);
            reservationJ.put("price", this.price);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reservationJ;
    }
}