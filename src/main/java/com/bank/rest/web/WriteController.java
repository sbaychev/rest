package com.bank.rest.web;

import com.bank.rest.services.IAccountWriteServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.Callable;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WriteController {

    private static final Logger LOG = LoggerFactory.getLogger(WriteController.class);

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Init binder.
     *
     * @param webDataBinder the web data binder
     */
    @InitBinder
    void initBinder(WebDataBinder webDataBinder) {

//        webDataBinder.registerCustomEditor(TypeOfCoverageIndex.class, new TypeOfCoverageIndexBinder());
//        webDataBinder.registerCustomEditor(ComputeCoverageIndex.class, new ComputeCoverageIndexBinder());
    }

    @Autowired
    IAccountWriteServices iAccountWriteServices;

    @PostMapping(path = "/make-payment")
    @ResponseBody
    public Callable<ResponseEntity> getAccountBalance(
        @Valid @RequestParam(value = "accountNumber") String accountNumber,
        @Valid @RequestParam(value = "toAccountNumber") String toAccountNumber,
        @Valid @RequestParam(value = "amount") String amount,
        @Valid @RequestParam(value = "customerID") String customerID) {

        try {
            if (iAccountWriteServices.performCreditOperation(accountNumber, toAccountNumber, amount, customerID)) {
                return () -> new ResponseEntity(true, HttpStatus.OK);
            }
        } catch (Exception e) {
            return () -> new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return () -> new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

//    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(value = {HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
//        MethodArgumentNotValidException.class})
//    private @ResponseBody
//    SearchError handleInvalidPayload(Exception e) {
//
//        LOG.error("Data Integrity Violation: ", e);
//
//        SearchError error = new SearchError();
//
//        error.setCode("0001");
//        error.setMessage("Data Integrity Violation! Check Log for exception information");
//
//        return error;
//    }
}
