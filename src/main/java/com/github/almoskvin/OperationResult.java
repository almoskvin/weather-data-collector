package com.github.almoskvin;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OperationResult<T> {
    private String error;
    private T value;
    private Status status;

    public enum Status {
        OK, Error
    }

    public static <T> OperationResult<T> ok(T value) {
        OperationResult<T> result = new OperationResult<>();
        result.setStatus(Status.OK);
        result.setValue(value);
        return result;
    }

    public static <T> OperationResult<T> error(String error) {
        OperationResult<T> result = new OperationResult<>();
        result.setStatus(Status.Error);
        result.setError(error);
        return result;
    }
}
