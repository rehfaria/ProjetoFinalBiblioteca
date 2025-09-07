package br.biblioteca.modelo;
public class Revista extends Publicacao {
    private final int edicao;

    public Revista(String id, String titulo, String autor, int edicao) {
        super(id, titulo, autor);
        this.edicao = edicao;
    }

    @Override public int prazoDiasEmprestimo() { return 7; }
    @Override public double multaPorDia() { return 1.00; }
}
