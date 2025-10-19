package com.assessment.ragchat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class MessageDto {
    private Long id;
    @NotBlank(message = "sender must not be blank")
    @Size(max = 255, message = "sender must be at most 255 characters")
    private String sender;
    @NotBlank(message = "sender must not be blank")
    private String content;
    private String context;
    private Long sessionId;
}