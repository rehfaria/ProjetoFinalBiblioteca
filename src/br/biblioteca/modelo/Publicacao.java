package br.biblioteca.modelo;
import java.io.Serializable;

public abstract class Publicacao implements Serializable {
	private static final long serialVersionUID = 1L;
    private final String id, titulo, autor;

    protected Publicacao(String id, String titulo, String autor) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }

    // polimórfico: cada tipo de publicação define prazo e multa diferentes
    public abstract int prazoDiasEmprestimo();
    public abstract double multaPorDia();

    @Override
    public String toString() {
        return "%s (%s) - %s".formatted(titulo, autor, id);
    }
}
