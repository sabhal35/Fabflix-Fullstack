package domain;

public class CartItem {
    private String movieId;
    private String title;
    private int quantity;
    private double price;

    public CartItem(String movieId, String title, int quantity, double price) {
        this.movieId = movieId;
        this.title = title;
        this.quantity = quantity;
        this.price = price;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Calculate total price for this item (price * quantity)
    public double getTotalPrice() {
        return this.price * this.quantity;
    }
}
