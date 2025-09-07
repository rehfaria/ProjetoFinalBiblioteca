package br.biblioteca.modelo;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Devolucao extends Transacao {
    private final Usuario usuario;
    private final Publicacao item;
    private LocalDate dataReal;

    public Devolucao(Usuario u, Publicacao p) {
        this.usuario = u;
        this.item = p;
    }

    @Override
    public void executar() {
        dataReal = LocalDate.now();
    }

    public long diasAtraso(LocalDate dataPrevista) {
        if (dataReal == null) return 0;
        long diff = ChronoUnit.DAYS.between(dataPrevista, dataReal);
        return Math.max(diff, 0);
    }

    public double calcularMulta(LocalDate dataPrevista) {
        return diasAtraso(dataPrevista) * item.multaPorDia();
    }
}
