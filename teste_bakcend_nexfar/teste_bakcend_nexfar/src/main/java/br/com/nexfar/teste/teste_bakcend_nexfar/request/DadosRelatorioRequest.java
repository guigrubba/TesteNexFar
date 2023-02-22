package br.com.nexfar.teste.teste_bakcend_nexfar.request;

import java.util.List;

public class DadosRelatorioRequest {
    public String key = "ORDER_DETAILED";
    private String format;
    private List<Filter> filters;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }
}
