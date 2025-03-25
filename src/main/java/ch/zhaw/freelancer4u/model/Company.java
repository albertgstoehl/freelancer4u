package ch.zhaw.freelancer4u.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Document("company")
public class Company {
    @Id 
    private String id;
    @NonNull
    private String name;
    @NonNull
    private String email;

    public Company(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
