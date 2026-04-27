package com.soumadeep.SpringEcommerce.Controller;

import com.soumadeep.SpringEcommerce.Service.ChatBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ChatBotController {

    @Autowired
    private ChatBotService chatBotService;

    @GetMapping("/ask")
    public ResponseEntity<String> chatBot(@RequestParam String message){
        String response=chatBotService.getBotResponse(message);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
