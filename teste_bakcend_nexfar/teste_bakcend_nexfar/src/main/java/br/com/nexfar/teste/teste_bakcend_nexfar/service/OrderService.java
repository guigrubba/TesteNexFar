package br.com.nexfar.teste.teste_bakcend_nexfar.service;

import br.com.nexfar.teste.teste_bakcend_nexfar.util.UsefulMethods;
import br.com.nexfar.teste.teste_bakcend_nexfar.request.Filter;
import br.com.nexfar.teste.teste_bakcend_nexfar.model.Items;
import br.com.nexfar.teste.teste_bakcend_nexfar.model.Order;
import br.com.nexfar.teste.teste_bakcend_nexfar.request.DatasFilterRequest;

import br.com.nexfar.teste.teste_bakcend_nexfar.response.ReportOrderResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    MongoTemplate mongoTemplate;

    public ReportOrderResponse createReport(DatasFilterRequest dadosRequest) throws ParseException {
        String key = dadosRequest.key;
        String format = dadosRequest.getFormat();
        ReportOrderResponse reportOrderResponse;

        //Fazendo a filtragem e passando os dados em uma lista
        List<Order> orders = findWithFilters(dadosRequest);

       //Criar um relatorio com o formato solicitado
        if ("XLS".equals(format)) {
            reportOrderResponse = createReportXLS(orders, key);
        } else {
            reportOrderResponse = createReportCSV(orders, key);
        }

        return reportOrderResponse;
    }

    public List<Order> findWithFilters(DatasFilterRequest dataFilters) throws ParseException {
        String filterKey;
        String filterOperation;
        String filterValue1;
        String filterValue2;
        Query query = new Query();

        for (Filter filter : dataFilters.getFilters()) {
            //Atribuindo os valores para variaveis dos filtros
            filterKey = filter.getKey();
            filterOperation = filter.getOperation();
            filterValue1 = filter.getValue1();
            filterValue2 = filter.getValue2();

            if ("cnpj".equals(filterKey)) {
                query.addCriteria(Criteria.where("client.cnpj").is(filterValue1));
            } else if ("status".equals(filterKey)) {
                query.addCriteria(Criteria.where("status").is(filterValue1));
            } else if ("netTotal".equals(filterOperation)) {
                if ("LTE".equals(filterOperation)) {
                    query.addCriteria(Criteria.where("netTotal").lte(filterValue1));
                } else {
                    query.addCriteria(Criteria.where("netTotal").gte(filterValue1));
                }
                query.addCriteria(Criteria.where(filterKey).gte(filterValue1));
            } else {
                DateFormat formattter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date startDate1 = formattter.parse(filterValue1);
                Date endDate1 = formattter.parse(filterValue2);
                query.addCriteria(Criteria.where("createdAt").gte(startDate1).lte(endDate1));
            }
        }

        return mongoTemplate.find(query, Order.class);
    }

    public ReportOrderResponse createReportXLS(List<Order> orders, String key) {
        ReportOrderResponse reportOrderResponse = new ReportOrderResponse();
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Orders");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("id");
            headerRow.createCell(1).setCellValue("client.cnpj");
            headerRow.createCell(2).setCellValue("client.name");
            headerRow.createCell(3).setCellValue("createdAt");
            headerRow.createCell(4).setCellValue("status");

            if ("ORDER_SIMPLE".equals(key)) {
                headerRow.createCell(5).setCellValue("netTotal");
                headerRow.createCell(6).setCellValue("totalWithTaxes");

                reportOrderResponse.setFileName("PedidoResumido.xlsx");

            } else {
                headerRow.createCell(5).setCellValue("items.product.sku");
                headerRow.createCell(6).setCellValue("items.product.name");
                headerRow.createCell(7).setCellValue("items.quantity");
                headerRow.createCell(8).setCellValue("items.finalPrice.price");
                headerRow.createCell(9).setCellValue("items.finalPrice.finalPrice");

                reportOrderResponse.setFileName("PedidoDetalhado.xlsx");

            }
            int rowIndex = 1;
            Row row = sheet.createRow(rowIndex++);
            for (Order order : orders) {

                String formatteDateCreatedAt = UsefulMethods.formatDate(order.getCreatedAt());

                if ("ORDER_SIMPLE".equals(key)) {
                    row.createCell(0).setCellValue(order.getId());
                    row.createCell(1).setCellValue(order.getClient().getCnpj());
                    row.createCell(2).setCellValue(order.getClient().getName());
                    row.createCell(3).setCellValue(order.getCreatedAt());
                    row.createCell(4).setCellValue(order.getStatus());
                    row.createCell(5).setCellValue(order.getNetTotal());
                    row.createCell(6).setCellValue(order.getTotalWithTaxes());
                    row = sheet.createRow(rowIndex++);
                } else {
                    for (Items item : order.getItems()) {
                        row.createCell(0).setCellValue(order.getId());
                        row.createCell(1).setCellValue(order.getClient().getCnpj());
                        row.createCell(2).setCellValue(order.getClient().getName());
                        row.createCell(3).setCellValue(formatteDateCreatedAt);
                        row.createCell(4).setCellValue(order.getStatus());
                        row.createCell(5).setCellValue(item.getProduct().getSku());
                        row.createCell(6).setCellValue(item.getProduct().getName());
                        row.createCell(7).setCellValue(item.getQuantity());
                        row.createCell(8).setCellValue(item.getFinalPrice().getPrice());
                        row.createCell(9).setCellValue(item.getFinalPrice().getFinalPrice());
                        row = sheet.createRow(rowIndex++);
                    }
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()));
            reportOrderResponse.setResource(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reportOrderResponse;
    }

    public ReportOrderResponse createReportCSV(List<Order> orders, String key) {

        ReportOrderResponse reportOrderResponse = new ReportOrderResponse();
        List<String[]> rows = new ArrayList<>();
        StringBuilder csv = new StringBuilder();

            if ("ORDER_SIMPLE".equals(key)) {
                reportOrderResponse.setFileName("PedidoResumido.csv");

                rows.add(new String[]{
                        "id",
                        "client.cnpj",
                        "client.name",
                        "createdAt",
                        "status",
                        "netTotal",
                        "totalWithTaxes"
                });

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

                    rows.add(new String[]{id, cnpj, name, createdAt, status, netTotal, totalWithTaxes});
                }
            } else {
                reportOrderResponse.setFileName("PedidoDetalhado.csv");

                rows.add(new String[]{
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
                });

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
                    for (Items item : order.getItems()) {
                        sku = item.getProduct().getSku();
                        productName = item.getProduct().getName();
                        quantity = String.valueOf(item.getQuantity());
                        price = String.valueOf(item.getFinalPrice().getPrice());
                        finalPrice = String.valueOf(item.getFinalPrice().getFinalPrice());
                        rows.add(new String[]{id, cnpj, name, createdAt, status, sku, productName, quantity, price, finalPrice});
                    }
                }
            }
        for (String[] row : rows) {
            csv.append(String.join(",", row)).append("\n");
        }

        // Cria um objeto InputStreamResource a partir do conteúdo do arquivo CSV
        ByteArrayResource resource = new ByteArrayResource(csv.toString().getBytes(StandardCharsets.UTF_8));
        reportOrderResponse.setResource(resource);

        return reportOrderResponse;
    }



}

