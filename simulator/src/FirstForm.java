import javax.swing.*;

/**
 * Created by loki on 15. 5. 28..
 */
public class FirstForm extends JFrame {
    private JPanel rootPanel;
    private JList listAssemblies;
    private JLabel labelRegisterA;
    private JPanel panelRegisterA;
    private JLabel labelRegisterAValue;
    private JPanel panelRegisterX;

    String[] assemblies;


    public FirstForm() {
        super("FirstForm");

        setContentPane(rootPanel);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setVisible(true);
    }


    private void createUIComponents() {
        assemblies = new String[]{
                "abc",
                "abc",
                "abc",
                "abc",
                "abc",
        };
        DefaultListModel<String> model = new DefaultListModel<>();
        for(String str : assemblies) {
            model.addElement(str);
        }
        listAssemblies = new JList(model);
    }
}
