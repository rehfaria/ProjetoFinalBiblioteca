package br.biblioteca.modelo;
public class MidiaDigital extends Publicacao {
    private final double tamanhoMB;

    public MidiaDigital(String id, String titulo, String autor, double tamanhoMB) {
        super(id, titulo, autor);
        this.tamanhoMB = tamanhoMB;
    }

    @Override public int prazoDiasEmprestimo() { return 30; }
    @Override public double multaPorDia() { return 0.50; }
}
