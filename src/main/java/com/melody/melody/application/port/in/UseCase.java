package com.melody.melody.application.port.in;


public interface UseCase<I extends UseCase.Command, O extends UseCase.Result> {
    O execute(I input);

    interface Command {
    }

    interface Result {
    }
}