package Domain;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Comanda extends Entity implements Serializable {
    private Map<Produs, Integer> produseCantitate; // Product -> Quantity
    private String data;

    public Comanda() {
        super(0);
        this.produseCantitate = new HashMap<>();
        this.data = "";
    }

    public Comanda(int id, Map<Produs, Integer> produseCantitate, String data) {
        super(id);
        this.produseCantitate = new HashMap<>(produseCantitate);
        this.data = data;
    }

    public void addProdus(Produs produs, int quantity) {
        produseCantitate.merge(produs, quantity, Integer::sum);
    }

    public void removeProdus(Produs produs) {
        produseCantitate.remove(produs);
    }

    public double getTotal() {
        double total = 0.0;

        for (Map.Entry<Produs, Integer> intrare : produseCantitate.entrySet()) {
            Produs produs = intrare.getKey();
            int cantitate = intrare.getValue();
            double pretProdus = Double.parseDouble(produs.getPret());

            total += pretProdus * cantitate;
        }

        return total;
    }

    public Map<Produs, Integer> getProduseCantitate() {
        return new HashMap<>(produseCantitate);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s", getId(), produseCantitate, data);
    }
}