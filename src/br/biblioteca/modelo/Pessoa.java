package br.biblioteca.modelo;
import java.io.Serializable;

public abstract class Pessoa implements Serializable {
	private static final long serialVersionUID = 1L;
    private final String nome, documento;

    protected Pessoa(String nome, String documento) {
        this.nome = nome;
        this.documento = documento;
    }

    public String getNome() { return nome; }
    public String getDocumento() { return documento; }
}
