package br.com.nexfar.teste.teste_bakcend_nexfar.response;
import org.springframework.core.io.Resource;

public class ReportOrderResponse {
    private String fileName;
    private Resource resource;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
