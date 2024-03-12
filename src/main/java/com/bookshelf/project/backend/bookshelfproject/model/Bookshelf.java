package com.bookshelf.project.backend.bookshelfproject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="bookshelf")
public class Bookshelf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "confirmed")
    private boolean confirmed;

     public Bookshelf(String firstName, String lastName, boolean confirmed){
         this.firstName = firstName;
         this.lastName = lastName;
         this.confirmed = confirmed;
     }
}
