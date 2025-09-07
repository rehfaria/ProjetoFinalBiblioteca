package br.biblioteca.modelo;

public class MidiaDigital extends Publicacao {
    private static final long serialVersionUID = 1L;

    private final double tamanhoMB;

    public MidiaDigital(String id, String titulo, String autor, double tamanhoMB) {
        super(id, titulo, autor);
        this.tamanhoMB = tamanhoMB;
    }

    public double getTamanhoMB() {
        return tamanhoMB;
    }

    @Override
    public int prazoDiasEmprestimo() { 
        return 30; 
    }

    @Override
    public double multaPorDia() { 
        return 0.50; 
    }

    @Override
    public String toString() {
        return "%s (%.2f MB) - %s".formatted(getTitulo(), tamanhoMB, getId());
    }
}
