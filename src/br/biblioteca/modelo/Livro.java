package br.biblioteca.modelo;

public class Livro extends Publicacao {
    private static final long serialVersionUID = 1L;
    private final int paginas;

    public Livro(String id, String titulo, String autor, int paginas) {
        super(id, titulo, autor);
        this.paginas = paginas;
    }

    public int getPaginas() {
        return paginas;
    }

    @Override
    public int prazoDiasEmprestimo() {
        return 14;
    }

    @Override
    public double multaPorDia() {
        return 1.50;
    }

    @Override
    public String toString() {
        return "%s (%d páginas) - %s".formatted(getTitulo(), paginas, getId());
    }
}
