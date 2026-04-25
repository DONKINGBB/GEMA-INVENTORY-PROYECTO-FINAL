package com.example.demo.model;
 
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
import java.time.LocalDateTime;
 
@Entity
@Table(name = "password_reset_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false)
    private String email;
 
    @Column(nullable = false)
    private String code;
 
    @Column(nullable = false)
    private LocalDateTime expiryDate;
 
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}
