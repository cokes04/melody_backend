package com.melody.melody.adapter.web;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@RequiredArgsConstructor
@RestController
@Profile("dev")
public class TestController {

    @PostMapping("/test")
    public ResponseEntity<?> test(HttpServletRequest request){
        try (ServletInputStream stream = request.getInputStream()){
            BufferedReader input = new BufferedReader(new InputStreamReader(stream));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = input.readLine()) != null) {
                sb.append(line);
            }

            return ResponseEntity.ok().body(sb.toString());

        }catch (IOException e){

            return ResponseEntity.badRequest().body("실패!");
        }

    }
}
