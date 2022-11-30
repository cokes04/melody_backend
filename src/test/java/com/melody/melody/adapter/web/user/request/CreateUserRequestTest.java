package com.melody.melody.adapter.web.user.request;

import com.melody.melody.adapter.web.user.TestUserWebGenerator;
import com.melody.melody.application.service.user.CreateUserService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateUserRequestTest {

    @Test
    void toCommand_ReturnCreatedCommand() {
        CreateUserRequest request = TestUserWebGenerator.randomCreateUserRequest();
        CreateUserService.Command command = request.toCommand();

        assertEquals(request.getNickName(), command.getNickName());
        assertEquals(request.getEmail(), command.getEmail());
        assertEquals(request.getPassword(), command.getPassword());
    }
}