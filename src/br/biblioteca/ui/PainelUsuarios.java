package br.biblioteca.ui;

import javax.swing.*;
import java.awt.*;
import br.biblioteca.modelo.Usuario;
import br.biblioteca.servico.BibliotecaService;

public class PainelUsuarios extends JPanel {
    private static final long serialVersionUID = 1L;

    private final BibliotecaService service;
    private final JTextField txtNome   = new JTextField(14);
    private final JTextField txtDoc    = new JTextField(10);
    private final JTextField txtCartao = new JTextField(10);

    private final UsuariosTableModel model = new UsuariosTableModel(new java.util.ArrayList<>());
    private final JTable tabela = new JTable(model);

    public PainelUsuarios(BibliotecaService service) {
        this.service = service;
        setLayout(new BorderLayout());

        // Topo (form + botões)
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.add(new JLabel("Nome:"));   topo.add(txtNome);
        topo.add(new JLabel("Doc:"));    topo.add(txtDoc);
        topo.add(new JLabel("Cartão:")); topo.add(txtCartao);

        JButton btnAdd       = new JButton("Cadastrar");
        JButton btnRemover   = new JButton("Remover selecionado");
        JButton btnBloquear  = new JButton("Bloquear/Desbloquear");

        topo.add(btnAdd);
        topo.add(btnRemover);
        topo.add(btnBloquear);
        add(topo, BorderLayout.NORTH);

        // Tabela
        tabela.setAutoCreateRowSorter(true);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Ações
        btnAdd.addActionListener(e -> cadastrar());
        btnRemover.addActionListener(e -> removerSelecionado());
        btnBloquear.addActionListener(e -> bloquearOuDesbloquearSelecionado());

        atualizarTabela();
    }

    private void cadastrar() {
        try {
            String nome = txtNome.getText().trim();
            String doc  = txtDoc.getText().trim();
            String cart = txtCartao.getText().trim();

            if (nome.isEmpty() || doc.isEmpty() || cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha Nome, Documento e Cartão.");
                return;
            }

            service.cadastrarUsuario(nome, doc, cart);
            atualizarTabela();
            limpar();
            service.salvar();
            JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removerSelecionado() {
        int viewRow = tabela.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário.");
            return;
        }
        int modelRow = tabela.convertRowIndexToModel(viewRow);
        Usuario u = model.getAt(modelRow);

        int resp = JOptionPane.showConfirmDialog(this,
                "Remover o usuário:\n" + u.getNome() + " (" + u.getDocumento() + ")?",
                "Confirmar remoção", JOptionPane.YES_NO_OPTION);

        if (resp != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = service.removerUsuarioPorDocumento(u.getDocumento());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Usuário removido.");
                atualizarTabela();
                service.salvar();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bloquearOuDesbloquearSelecionado() {
        int viewRow = tabela.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário.");
            return;
        }
        int modelRow = tabela.convertRowIndexToModel(viewRow);
        Usuario u = model.getAt(modelRow);

        boolean bloquear = !u.isBloqueado();
        String titulo = bloquear ? "Bloquear usuário" : "Desbloquear usuário";
        String msg    = (bloquear ? "Bloquear " : "Desbloquear ") + u.getNome() + "?";

        int resp = JOptionPane.showConfirmDialog(this, msg, titulo, JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) return;

        try {
            if (bloquear) u.bloquear(); else u.desbloquear();
            atualizarTabela();
            service.salvar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarTabela() {
        model.setDados(service.getUsuarios());
    }

    private void limpar() {
        txtNome.setText("");
        txtDoc.setText("");
        txtCartao.setText("");
    }
}

