package br.biblioteca.modelo;

public class Usuario extends Pessoa {
    CartaoBiblioteca cartao; // package-private (encapsulado no modelo)
    private boolean bloqueado;

    public Usuario(String nome, String doc) {
        super(nome, doc);
    }

    public void emitirCartao(String numero) {
        if (cartao != null) throw new IllegalStateException("Usuário já possui cartão.");
        cartao = new CartaoBiblioteca(numero, this);
    }

    // auxiliares para UI
    public boolean possuiCartao()        { return cartao != null; }
    public String  getNumeroCartao()     { return cartao == null ? "" : cartao.getNumero(); }

    public boolean isBloqueado()         { return bloqueado; }
    public void bloquear()               { bloqueado = true; }
    public void desbloquear()            { bloqueado = false; }

    @Override public String toString()   { return getNome() + " (" + getDocumento() + ")"; }
}
