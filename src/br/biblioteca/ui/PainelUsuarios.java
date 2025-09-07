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
        topo.add(new JLabel("Nome:"));   topo.add(txtNome);
        topo.add(new JLabel("Doc:"));    topo.add(txtDoc);
        topo.add(new JLabel("Cartão:")); topo.add(txtCartao);
        JButton btnAdd    = new JButton("Cadastrar");
        JButton btnEmitir = new JButton("Emitir Cartão");
        topo.add(btnAdd); topo.add(btnEmitir);
        add(topo, BorderLayout.NORTH);

        // habilita ordenação para a tabela (importante junto com convertRowIndexToModel)
        tabela.setAutoCreateRowSorter(true);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Cadastrar usuário (opcionalmente já emite cartão)
        btnAdd.addActionListener(e -> {
            try {
                String nome = txtNome.getText().trim();
                String doc  = txtDoc.getText().trim();
                String num  = txtCartao.getText().trim();
                if (nome.isEmpty() || doc.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Informe nome e documento.");
                    return;
                }
                service.cadastrarUsuario(nome, doc);

                // emite cartão se informado e ainda não tiver
                // busca o usuário recém-cadastrado na lista atualizada
                atualizarTabela();
                for (int i = 0; i < model.getRowCount(); i++) {
                    Usuario u = model.getAt(i);
                    if (u.getDocumento().equals(doc) && !num.isBlank() && !u.possuiCartao()) {
                        u.emitirCartao(num);
                        break;
                    }
                }

                atualizarTabela();
                limpar();
                service.salvar();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        });

        // Emitir cartão para o usuário selecionado
        btnEmitir.addActionListener(e -> {
            int viewRow = tabela.getSelectedRow();
            if (viewRow < 0) {
                JOptionPane.showMessageDialog(this, "Selecione um usuário na tabela.");
                return;
            }
            int modelRow = tabela.convertRowIndexToModel(viewRow); // <-- CONVERTE VIEW->MODEL
            Usuario u = model.getAt(modelRow);                     // <-- PEGA DO MODEL

            String numero = JOptionPane.showInputDialog(this, "Número do cartão:");
            if (numero == null || numero.isBlank()) return;

            try {
                if (!u.possuiCartao()) {
                    u.emitirCartao(numero);
                } else {
                    JOptionPane.showMessageDialog(this, "Usuário já possui cartão.");
                    return;
                }
                atualizarTabela();
                service.salvar();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
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

