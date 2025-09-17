package itmo.infosecurity.lab1.controllers;

import itmo.infosecurity.lab1.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;

    @GetMapping("/data")
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(userService.findAll());
    }
}
