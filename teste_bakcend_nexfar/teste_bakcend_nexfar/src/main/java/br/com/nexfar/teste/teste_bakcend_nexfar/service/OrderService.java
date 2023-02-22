package br.com.nexfar.teste.teste_bakcend_nexfar.service;

import br.com.nexfar.teste.teste_bakcend_nexfar.model.Items;
import br.com.nexfar.teste.teste_bakcend_nexfar.model.Order;
import br.com.nexfar.teste.teste_bakcend_nexfar.repository.OrderRepository;
import br.com.nexfar.teste.teste_bakcend_nexfar.request.DadosRelatorioRequest;
import br.com.nexfar.teste.teste_bakcend_nexfar.request.Filter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;

import java.io.FileOutputStream;
import java.util.Collections;


import static java.lang.System.out;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;


    public void generateReport(DadosRelatorioRequest dadosRequest){
        String key = dadosRequest.key;
        String format = dadosRequest.getFormat();
        List<Order> orders = Collections.singletonList(new Order());
        String ReadyQuery = createQuery(dadosRequest);
        
        orders = orderRepository.findDadesOrder(ReadyQuery);

        if("XLS".equals(format)){
            createReportXLS(orders, key);
        }else {
            createReportCSV(orders, key);
        }
    }

    public void createReportXLS(List<Order> orders, String key){
        try{
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Orders");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("id");
            headerRow.createCell(1).setCellValue("client.cnpj");
            headerRow.createCell(2).setCellValue("client.name");
            headerRow.createCell(3).setCellValue("createdAt");
            headerRow.createCell(4).setCellValue("status");

            if("ORDER_SIMPLE".equals(key)){
                headerRow.createCell(5).setCellValue("netTotal");
                headerRow.createCell(6).setCellValue("totalWithTaxes");
            }else{
                headerRow.createCell(5).setCellValue("items.product.sku");
                headerRow.createCell(6).setCellValue("items.product.name");
                headerRow.createCell(7).setCellValue("items.quantity");
                headerRow.createCell(8).setCellValue("items.finalPrice.price");
                headerRow.createCell(9).setCellValue("items.finalPrice.finalPrice");
            }
            int rowIndex = 1;
            for (Order order : orders) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(order.getId());
                row.createCell(1).setCellValue(order.getClient().getCnpj());
                row.createCell(2).setCellValue(order.getClient().getName());
                row.createCell(3).setCellValue(order.getCreatedAt());
                row.createCell(4).setCellValue(order.getStatus());

                if("ORDER_SIMPLE".equals(key)){
                    row.createCell(5).setCellValue(order.getNetTotal());
                    row.createCell(6).setCellValue(order.getTotalWithTaxes());
                } else{
                    for (Items item : order.getItems()){
                        row.createCell(5).setCellValue(item.getProduct().getSku());
                        row.createCell(6).setCellValue(item.getProduct().getName());
                        row.createCell(7).setCellValue(item.getQuantity());
                        row.createCell(8).setCellValue(item.getFinalPrice().getPrice());
                        row.createCell(9).setCellValue(item.getFinalPrice().getFinalPrice());

                        row = sheet.createRow(rowIndex++);
                    }
                }
            }
            FileOutputStream fileOut;
            if ("ORDER_SIMPLE".equals(key)) {
                fileOut = new FileOutputStream("PedidoResumido.xlsx");
            } else{
                fileOut = new FileOutputStream("PedidoDetalhado.xlsx");
            }

            workbook.write(fileOut);
            fileOut.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createReportCSV(List<Order> orders, String key){
        String csvFilePath;
        if("ORDER_SIMPLE".equals(key)){
            csvFilePath = "PedidoResumido.csv";
        }else{
            csvFilePath = "PedidoDetalhado.csv";
        }

        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFilePath))) {


            List<String[]> rows = new ArrayList<>();

            if("ORDER_SIMPLE".equals(key)){
                String[] header = new String[] {
                        "id",
                        "client.cnpj",
                        "client.name",
                        "createdAt",
                        "status",
                        "netTotal",
                        "totalWithTaxes"
                };
                csvWriter.writeNext(header);

                String id;
                String cnpj;
                String name;
                String createdAt;
                String status;
                String netTotal;
                String totalWithTaxes;

                for (Order order : orders) {
                    id = String.valueOf(order.getId());
                    cnpj = order.getClient().getCnpj();
                    name = order.getClient().getName();
                    createdAt = String.valueOf(order.getCreatedAt());
                    status = order.getStatus();
                    netTotal = String.valueOf(order.getNetTotal());
                    totalWithTaxes = String.valueOf(order.getTotalWithTaxes());

                    rows.add(new String[] {id, cnpj, name, createdAt, status, netTotal, totalWithTaxes});
                }
            } else{
                String[] header = new String[] {
                        "id",
                        "client.cnpj",
                        "client.name",
                        "createdAt",
                        "status",
                        "items.product.sku",
                        "items.product.name",
                        "items.quantity",
                        "items.finalPrice.price",
                        "items.finalPrice.finalPrice"
                };
                csvWriter.writeNext(header);

                String id;
                String cnpj;
                String name;
                String createdAt;
                String status;
                String sku;
                String productName;
                String quantity;
                String price;
                String finalPrice;

                for (Order order : orders) {
                    id = String.valueOf(order.getId());
                    cnpj = order.getClient().getCnpj();
                    name = order.getClient().getName();
                    createdAt = String.valueOf(order.getCreatedAt());
                    status = order.getStatus();
                    for(Items item : order.getItems()){
                        sku = item.getProduct().getSku();
                        productName = item.getProduct().getName();
                        quantity = String.valueOf(item.getQuantity());
                        price = String.valueOf(item.getFinalPrice().getPrice());
                        finalPrice = String.valueOf(item.getFinalPrice().getFinalPrice());
                        rows.add(new String[] {id, cnpj, name, createdAt, status, sku, productName, quantity, price, finalPrice});
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String createQuery(DadosRelatorioRequest dadosRequest) {
        String queryFinal = "";
        String query = "";
        String queryProcess ="";
        List<Filter> filters = dadosRequest.getFilters();
        int contador = 1;

        //Verificando qual o tipo de filtro, e montando a query
        for (Filter filter : filters) {
            if ("cnpj".equals(filter.getKey())) {
                query = "'client.cnpj': '" + filter.getValue1() + "'";
            } else if ("netTotal".equals(filter.getValue1())) {

                if ("LTE".equals(filter.getOperation())) {
                    query = "'netTotal': {$lte: " + filter.getValue1() + "}";
                } else {
                    query = "'netTotal': {$gte: " + filter.getValue1() + "}";
                }

            } else if ("status".equals(filter.getKey())) {
                query = "'status': '" + filter.getValue1() + "'";
            }
            if (contador != filters.size()) {
                queryProcess += query + ", ";
            } else {
                queryProcess += query;
                queryFinal = "{" + queryProcess + "}";
            }

            contador++;
        }
        return queryFinal;
    }
}

