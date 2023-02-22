package br.com.nexfar.teste.teste_bakcend_nexfar.model;

public class Items {
    private Product product;
    private Integer quantity;
    private FinalPrice finalPrice;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public FinalPrice getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(FinalPrice finalPrice) {
        this.finalPrice = finalPrice;
    }
}
