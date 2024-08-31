package kr.co.polycube.backendtest.Domain.user;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class PolyUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // 이름 중복은 가능
    private String name;

    public void setPolyUser(String name) {
        this.name = name;
    }
}