package br.biblioteca.modelo;

public class Bibliotecario extends Pessoa {
    private static final long serialVersionUID = 1L;

    public Bibliotecario(String nome, String doc) {
        super(nome, doc);
    }

    @Override
    public String toString() {
        return getNome() + " (" + getDocumento() + ")";
    }
}
