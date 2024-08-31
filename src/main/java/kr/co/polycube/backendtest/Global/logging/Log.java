package kr.co.polycube.backendtest.Global.logging;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String methodName;

    @Lob
    private String arguments;

    @Lob
    private String result;

    private LocalDateTime timestamp;

    public Log(String methodName, String arguments, String result) {
        this.methodName = methodName;
        this.arguments = arguments;
        this.result = result;
        this.timestamp = LocalDateTime.now();
    }

}
