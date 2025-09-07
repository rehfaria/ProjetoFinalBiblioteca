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
    private final JTextField txtCartao = new JTextField(8);

    private final UsuariosTableModel model = new UsuariosTableModel(new java.util.ArrayList<>());
    private final JTable tabela = new JTable(model);

    public PainelUsuarios(BibliotecaService service) {
        this.service = service;
        setLayout(new BorderLayout());

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.add(new JLabel("Nome:"));    topo.add(txtNome);
        topo.add(new JLabel("Doc:"));     topo.add(txtDoc);
        topo.add(new JLabel("Cartão*:")); topo.add(txtCartao); // * obrigatório

        JButton btnAdd       = new JButton("Cadastrar");
        JButton btnEmitir    = new JButton("Emitir Cartão");
        JButton btnRemover   = new JButton("Remover selecionado");

        topo.add(btnAdd);
        topo.add(btnEmitir);
        topo.add(btnRemover);
        add(topo, BorderLayout.NORTH);

        // Tabela
        tabela.setAutoCreateRowSorter(true); // importante para convertRowIndexToModel
        tabela.setFillsViewportHeight(true);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // ===== Cadastro com CARTÃO OBRIGATÓRIO =====
        btnAdd.addActionListener(e -> {
            try {
                String nome = txtNome.getText().trim();
                String doc  = txtDoc.getText().trim();
                String num  = txtCartao.getText().trim();

                if (nome.isEmpty() || doc.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Informe nome e documento.");
                    return;
                }
                if (num.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "O número do cartão é obrigatório.");
                    return;
                }

                // 1) cadastra o usuário
                service.cadastrarUsuario(nome, doc);

                // 2) emite cartão imediatamente para o usuário recém-cadastrado
                atualizarTabela(); // atualiza para garantir referência atual
                Usuario novo = null;
                for (int i = 0; i < model.getRowCount(); i++) {
                    Usuario u = model.getAt(i);
                    if (u.getDocumento().equals(doc)) { novo = u; break; }
                }
                if (novo != null) {
                    if (!novo.possuiCartao()) {
                        novo.emitirCartao(num);
                    } else if (novo.getNumeroCartao() != null && !novo.getNumeroCartao().isBlank()) {
                        // Em tese não deve acontecer no fluxo de criação, mas deixamos a proteção
                        JOptionPane.showMessageDialog(this, "Aviso: usuário já possuía cartão (" + novo.getNumeroCartao() + ").");
                    }
                }

                atualizarTabela();
                limpar();
                service.salvar();
                JOptionPane.showMessageDialog(this, "Usuário cadastrado e cartão emitido com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        });

        // Emitir cartão (segue existindo para reemissão manual, se necessário)
        btnEmitir.addActionListener(e -> {
            int viewRow = tabela.getSelectedRow();
            if (viewRow < 0) {
                JOptionPane.showMessageDialog(this, "Selecione um usuário na tabela.");
                return;
            }
            int modelRow = tabela.convertRowIndexToModel(viewRow);
            Usuario u = model.getAt(modelRow);

            String numero = JOptionPane.showInputDialog(this, "Número do cartão:");
            if (numero == null || numero.isBlank()) return;

            try {
                if (!u.possuiCartao()) {
                    u.emitirCartao(numero);
                } else {
                    // Se quiser permitir “reemissão”, troque a lógica aqui conforme sua regra.
                    JOptionPane.showMessageDialog(this, "Usuário já possui cartão.");
                    return;
                }
                atualizarTabela();
                service.salvar();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        });

        // Remover usuário selecionado
        btnRemover.addActionListener(e -> {
            int viewRow = tabela.getSelectedRow();
            if (viewRow < 0) {
                JOptionPane.showMessageDialog(this, "Selecione um usuário na tabela.");
                return;
            }
            int modelRow = tabela.convertRowIndexToModel(viewRow);
            Usuario u = model.getAt(modelRow);
            String doc = u.getDocumento();

            int resp = JOptionPane.showConfirmDialog(
                    this,
                    "Remover o usuário \"" + u.getNome() + "\" (doc: " + doc + ")?",
                    "Confirmar remoção",
                    JOptionPane.YES_NO_OPTION
            );
            if (resp != JOptionPane.YES_OPTION) return;

            try {
                boolean ok = service.removerUsuarioPorDocumento(doc);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Usuário removido com sucesso.");
                    atualizarTabela();
                    service.salvar();
                } else {
                    JOptionPane.showMessageDialog(this, "Nenhum usuário encontrado com esse documento.",
                            "Não encontrado", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao remover: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        atualizarTabela();
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

