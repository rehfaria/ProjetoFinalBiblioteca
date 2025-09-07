package br.biblioteca.modelo;
public class Livro extends Publicacao {
    private final int paginas;

    public Livro(String id, String titulo, String autor, int paginas) {
        super(id, titulo, autor);
        this.paginas = paginas;
    }

    @Override public int prazoDiasEmprestimo() { return 14; }
    @Override public double multaPorDia() { return 1.50; }
}
