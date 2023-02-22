package br.com.nexfar.teste.teste_bakcend_nexfar.controller;

import br.com.nexfar.teste.teste_bakcend_nexfar.request.DadosRelatorioRequest;
import br.com.nexfar.teste.teste_bakcend_nexfar.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/report/generate")
public class ReportOrderController {

    @Autowired
    OrderService orderService;

    @PostMapping
    public void gerarRelatorio(@RequestBody DadosRelatorioRequest dadosRequest){
        orderService.generateReport(dadosRequest);
    }
}
