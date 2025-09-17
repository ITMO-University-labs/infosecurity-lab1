package itmo.infosecurity.lab1.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
@ToString(exclude = "user")
public class RefreshToken {

    @Id
    @Column(name = "user_id")
    private long userId;

    @Column(name = "refresh_token")
    private String token;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    public RefreshToken(String token) {
        this.token = token;
    }
}
