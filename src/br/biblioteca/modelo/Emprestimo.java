package br.biblioteca.modelo;
import java.time.LocalDate;

public class Emprestimo extends Transacao {
    private final Usuario usuario;
    private final Publicacao item;
    private LocalDate dataPrevista;

    public Emprestimo(Usuario u, Publicacao p) {
        this.usuario = u;
        this.item = p;
    }

    @Override
    public void executar() throws Exception {
        if (usuario.isBloqueado())
            throw new UsuarioBloqueadoException();
        dataPrevista = LocalDate.now().plusDays(item.prazoDiasEmprestimo());
    }

    public LocalDate getDataPrevista() { return dataPrevista; }
    public Usuario getUsuario() { return usuario; }
    public Publicacao getItem() { return item; }
}
