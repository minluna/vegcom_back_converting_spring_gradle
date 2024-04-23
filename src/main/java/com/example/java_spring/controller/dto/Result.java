package com.example.java_spring.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@Builder
public class Result<T> {
    private String statusCode;
    private String resultMsg;
    private T resultData;

    public Result(final String statusCode, final String resultMsg) {
        this.statusCode = statusCode;
        this.resultMsg = resultMsg;
    }

    public static<T> Result<T> res(final String statusCode, final String resultMsg) {
        ResultBuilder<T> builder = Result.<T>builder();

        builder.statusCode(statusCode);
        builder.resultMsg(resultMsg);

        return builder.build();
    }

    public static<T> Result<T> res(final String statusCode, final String resultMsg, final T resultData) {
        ResultBuilder<T> builder = Result.<T>builder()
                .statusCode(statusCode)
                .resultMsg(resultMsg)
                .resultData(resultData);


        return builder.build();
    }
}
