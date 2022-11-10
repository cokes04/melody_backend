package com.melody.melody.application.port.in;

public interface UseCase<I extends UseCase.Command, O extends UseCase.Result> {
    O execute(I command);

    interface Command {
    }

    interface Result {
    }
}