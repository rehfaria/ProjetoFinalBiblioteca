package br.biblioteca.ui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

import br.biblioteca.modelo.Devolucao;
import br.biblioteca.modelo.Emprestimo;
import br.biblioteca.modelo.Publicacao;
import br.biblioteca.modelo.Usuario;
import br.biblioteca.servico.BibliotecaService;

public class PainelEmprestimos extends JPanel {
    private static final long serialVersionUID = 1L;

    private final BibliotecaService service;

    private final JComboBox<Usuario> cbUsuario = new JComboBox<>();
    private final JComboBox<Publicacao> cbItem = new JComboBox<>();
    private LocalDate dataPrevista;

    private final JTextArea log = new JTextArea();

    public PainelEmprestimos(BibliotecaService service) {
        this.service = service;
        setLayout(new BorderLayout());

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.add(new JLabel("Usuário:"));
        topo.add(cbUsuario);
        topo.add(new JLabel("Item:"));
        topo.add(cbItem);

        JButton btnEmprestar = new JButton("Emprestar");
        JButton btnDevolver  = new JButton("Devolver");
        topo.add(btnEmprestar);
        topo.add(btnDevolver);

        add(topo, BorderLayout.NORTH);

        log.setEditable(false);
        add(new JScrollPane(log), BorderLayout.CENTER);

        carregarCombos();

        btnEmprestar.addActionListener(e -> {
            try {
                Usuario u = (Usuario) cbUsuario.getSelectedItem();
                Publicacao p = (Publicacao) cbItem.getSelectedItem();
                if (u == null || p == null) {
                    JOptionPane.showMessageDialog(this, "Selecione um usuário e um item.");
                    return;
                }
                Emprestimo emp = new Emprestimo(u, p);
                emp.executar();
                dataPrevista = emp.getDataPrevista();
                log.append("Emprestado: " + p.getTitulo() + " para " + u + ". Devolver até: " + dataPrevista + "\n");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        });

        btnDevolver.addActionListener(e -> {
            try {
                Usuario u = (Usuario) cbUsuario.getSelectedItem();
                Publicacao p = (Publicacao) cbItem.getSelectedItem();
                if (u == null || p == null) {
                    JOptionPane.showMessageDialog(this, "Selecione um usuário e um item.");
                    return;
                }
                Devolucao dev = new Devolucao(u, p);
                dev.executar();
                double multa = dev.calcularMulta(dataPrevista == null ? LocalDate.now() : dataPrevista);
                log.append("Devolução: " + p.getTitulo() + " de " + u + ". Multa: R$ " + multa + "\n");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        });
    }

    private void carregarCombos() {
        cbUsuario.removeAllItems();
        for (var u : service.getUsuarios()) cbUsuario.addItem(u);

        cbItem.removeAllItems();
        for (var p : service.getAcervo()) cbItem.addItem(p);
    }
}

