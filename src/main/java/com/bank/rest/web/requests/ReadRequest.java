package com.bank.rest.web.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReadRequest {

    private String accountId;

    private String accountHolderName;

    private Long accountNumberID;

    private Long accountHolderID;

}
