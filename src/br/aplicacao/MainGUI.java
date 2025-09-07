package br.aplicacao;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import br.biblioteca.modelo.Biblioteca;
import br.biblioteca.modelo.Endereco;
import br.biblioteca.servico.BibliotecaService;
import br.biblioteca.ui.MainFrame;

public class MainGUI {
    public static void main(String[] args) {
        // Look & Feel Nimbus (opcional)
        try {
            for (var info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) { UIManager.setLookAndFeel(info.getClassName()); break; }
            }
        } catch (Exception ignore) {}

        SwingUtilities.invokeLater(() -> {
            try {
                var bib = new Biblioteca("Biblioteca", new Endereco("","",""));
                var service = new BibliotecaService(bib);

                // tenta carregar
                try { service.carregar(); } catch (Exception ignored) {}

                // cria janela principal (supondo que MainFrame monta as abas Acervo/Usuarios/Emprestimos)
                var frame = new MainFrame(service);
                frame.setTitle("Sistema de Biblioteca");
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                frame.setVisible(true);
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setSize(960, 600);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                // se não há dados, apenas informa e direciona o usuário para as abas
                if (service.getAcervo().isEmpty()) {
                    JOptionPane.showMessageDialog(frame,
                            "O acervo está vazio.\nVá até a aba \"Acervo\" para cadastrar publicações.",
                            "Acervo vazio", JOptionPane.INFORMATION_MESSAGE);
                    // se seu MainFrame expõe o JTabbedPane, selecione a aba Acervo:
                    frame.selecionarAba("Acervo"); // implemente esse método no MainFrame, ver observação abaixo
                }

                if (service.getUsuarios().isEmpty()) {
                    JOptionPane.showMessageDialog(frame,
                            "Nenhum usuário cadastrado.\nVá até a aba \"Usuários\" para cadastrar.",
                            "Sem usuários", JOptionPane.INFORMATION_MESSAGE);
                    frame.selecionarAba("Usuários"); // idem
                }

                // salva ao encerrar
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try { service.salvar(); } catch (Exception ignored) {}
                }));
            } catch (Throwable t) {
                t.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Falha ao iniciar: " + t.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
