package itmo.infosecurity.lab1.controllers;

import itmo.infosecurity.lab1.services.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/data")
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @DeleteMapping("/delete-user-by-email/{email}")
    public ResponseEntity<?> deleteUserByEmail(@PathVariable @Email @Size(max = 64) String email) {
        userService.deleteUserByEmail(email);
        return null;
    }
}
