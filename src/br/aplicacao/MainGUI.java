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
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignore) {}

        SwingUtilities.invokeLater(() -> {
            try {
                var bib = new Biblioteca("Biblioteca", new Endereco("", "", ""));
                var service = new BibliotecaService(bib);

                // tenta carregar dados persistidos
                try { service.carregar(); } catch (Exception ignored) {}

                // cria janela principal com as abas (Acervo / Usuários / Empréstimos)
                var frame = new MainFrame(service);
                frame.setTitle("Sistema de Biblioteca");
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setSize(960, 600);
                frame.setLocationRelativeTo(null);

                // mensagens iniciais úteis
                if (service.getAcervo().isEmpty()) {
                    JOptionPane.showMessageDialog(frame,
                            "O acervo está vazio.\nVá até a aba \"Acervo\" para cadastrar publicações.",
                            "Acervo vazio",
                            JOptionPane.INFORMATION_MESSAGE);
                    frame.selecionarAba("Acervo");
                }

                if (service.getUsuarios().isEmpty()) {
                    JOptionPane.showMessageDialog(frame,
                            "Nenhum usuário cadastrado.\nVá até a aba \"Usuários\" para cadastrar.",
                            "Sem usuários",
                            JOptionPane.INFORMATION_MESSAGE);
                    frame.selecionarAba("Usuários");
                }

                // exibe a janela por último (depois de montar tudo)
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                frame.setVisible(true);

                // salva automaticamente ao encerrar a aplicação
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try { service.salvar(); } catch (Exception ignored) {}
                }));

            } catch (Throwable t) {
                t.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Falha ao iniciar: " + t.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

