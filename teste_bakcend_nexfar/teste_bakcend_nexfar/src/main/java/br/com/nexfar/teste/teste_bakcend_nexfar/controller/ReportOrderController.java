package br.com.nexfar.teste.teste_bakcend_nexfar.controller;

import br.com.nexfar.teste.teste_bakcend_nexfar.service.OrderService;
import br.com.nexfar.teste.teste_bakcend_nexfar.response.ReportOrderResponse;
import br.com.nexfar.teste.teste_bakcend_nexfar.request.DatasFilterRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.text.ParseException;



@RestController
@RequestMapping("/report/generate")
public class ReportOrderController {

    @Autowired
    OrderService orderService;

    @PostMapping
    public ResponseEntity<Resource> genarateReport(@RequestBody DatasFilterRequest dadosRequest) throws ParseException{

        ReportOrderResponse reportOrderResponse = orderService.createReport(dadosRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
        headers.setContentDispositionFormData("attachment", reportOrderResponse.getFileName());

        return ResponseEntity.ok()
                .headers(headers)
                .body(reportOrderResponse.getResource());
    }
}
