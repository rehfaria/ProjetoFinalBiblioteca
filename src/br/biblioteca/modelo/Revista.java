package br.biblioteca.modelo;

public class Revista extends Publicacao {
    private static final long serialVersionUID = 1L;
    private final int edicao;

    public Revista(String id, String titulo, String autor, int edicao) {
        super(id, titulo, autor);
        this.edicao = edicao;
    }

    public int getEdicao() {
        return edicao;
    }

    @Override
    public int prazoDiasEmprestimo() {
        return 7;
    }

    @Override
    public double multaPorDia() {
        return 1.00;
    }

    @Override
    public String toString() {
        return "%s (Edição %d) - %s".formatted(getTitulo(), edicao, getId());
    }
}
