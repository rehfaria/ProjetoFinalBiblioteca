package br.biblioteca.modelo;
import java.io.Serializable;

public class Endereco implements Serializable {
	private static final long serialVersionUID = 1L;
    private final String rua, numero, cidade;

    public Endereco(String rua, String numero, String cidade) {
        this.rua = rua;
        this.numero = numero;
        this.cidade = cidade;
    }

    @Override
    public String toString() {
        return rua + ", " + numero + " - " + cidade;
    }
}
