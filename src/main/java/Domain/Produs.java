package Domain;

import java.util.Objects;

/**
 * Product entity representing items in the system
 */
public class Produs extends Entity {
    private String categorie;
    private String nume;
    private String pret;

    public Produs() {
        this(0, "", "", 0.0);
    }

    public Produs(int id, String categorie, String nume, double pret) {
        super(id);
        setCategorie(categorie);
        setNume(nume);
        setPret(pret);
    }

    public Produs(int id, String categorie, String nume, String pret) {
        this(id, categorie, nume, parsePrice(pret));
    }

    private static double parsePrice(String pret) {
        try {
            return Double.parseDouble(pret.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid price format", e);
        }
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        if (categorie == null || categorie.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
        this.categorie = categorie.trim();
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        if (nume == null || nume.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.nume = nume.trim();
    }

    public String getPret() {
        return pret;
    }

    public void setPret(double pret) {
        if (pret < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.pret = String.valueOf(Math.round(pret * 100) / 100.0);
    }

    public String getPretString() {
        return String.format("%.2f", pret);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        Produs produs = (Produs) obj;
        return CharSequence.compare(produs.pret, pret) == 0 &&
                Objects.equals(categorie, produs.categorie) &&
                Objects.equals(nume, produs.nume);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), categorie, nume, pret);
    }

    @Override
    public String toString() {
        return "Produs{" +
                "id=" + getId() +
                ", categorie='" + categorie + '\'' +
                ", nume='" + nume + '\'' +
                ", pret=" + pret +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int id;
        private String categorie;
        private String nume;
        private double pret;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder categorie(String categorie) {
            this.categorie = categorie;
            return this;
        }

        public Builder nume(String nume) {
            this.nume = nume;
            return this;
        }

        public Builder pret(double pret) {
            this.pret = pret;
            return this;
        }

        public Produs build() {
            return new Produs(id, categorie, nume, pret);
        }
    }
}