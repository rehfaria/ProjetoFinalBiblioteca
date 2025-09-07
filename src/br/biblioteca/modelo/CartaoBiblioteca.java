package br.biblioteca.modelo;
import java.io.Serializable;

class CartaoBiblioteca implements Serializable {
    private final String numero;
    private final Usuario dono;

    CartaoBiblioteca(String numero, Usuario dono) {
        this.numero = numero;
        this.dono = dono;
    }

    public String getNumero() { return numero; }
}
