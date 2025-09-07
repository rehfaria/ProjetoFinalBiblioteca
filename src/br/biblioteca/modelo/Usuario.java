package br.biblioteca.modelo;

public class Usuario extends Pessoa {
    private static final long serialVersionUID = 1L;
    private CartaoBiblioteca cartao; // encapsulado no modelo

    public Usuario(String nome, String doc) {
        super(nome, doc);
    }

    /** Emite um cartão para o usuário. */
    public void emitirCartao(String numero) {
        if (cartao != null) {
            throw new IllegalStateException("Usuário já possui cartão.");
        }
        cartao = new CartaoBiblioteca(numero, this);
    }

    /** Verifica se o usuário já possui cartão. */
    public boolean possuiCartao() {
        return cartao != null;
    }

    /** Retorna o número do cartão, se existir. */
    public String getNumeroCartao() {
        return cartao == null ? "" : cartao.getNumero();
    }

    @Override
    public String toString() {
        return getNome() + " (" + getDocumento() + ")";
    }
}
