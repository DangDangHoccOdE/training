package com.hoanghaidang.social_network.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các trường null khi serializing
public class ApiResponse<T> {
    private int status = HttpStatus.OK.value();
    private String message = "success";
    private T data;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now(); // Đảm bảo timestamp có giá trị hiện tại mặc định
}
