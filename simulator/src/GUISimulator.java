import interfaces.VisualSimulator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;

/**
 * Created by loki on 15. 5. 28..
 */
public class GUISimulator extends JFrame implements VisualSimulator {

    private VirtualMachine virtualMachine;
    private CodeSimulator codeSimulator;

    private JPanel rootPanel;
    private JList<String> listAssemblies;

    HashMap<String, Integer> registerValueMap;


    public GUISimulator() {
        super("SIC/XE Simulator");
        initialize();
    }

    private void setUpUI() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout());
        setContentPane(rootPanel);

        addAssemblyList();
        addControlButtons();
        addRegisterValueLabels();
    }

    private void addAssemblyList() {
        String[] assemblies = new String[]{
                "abc",
                "abc",
                "abc",
                "abc",
                "abc",
        };
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String str : assemblies) {
            model.addElement(str);
        }

        listAssemblies = new JList<>(model);
        listAssemblies.setPreferredSize(new Dimension(200, 400));
        rootPanel.add(listAssemblies, BorderLayout.LINE_START);
    }

    private void addControlButtons() {
        JPanel panelControlButtons = new JPanel();
        panelControlButtons.setLayout(new BoxLayout(panelControlButtons, BoxLayout.LINE_AXIS));
        rootPanel.add(panelControlButtons, BorderLayout.PAGE_START);

        JButton buttonLoadProgram = new JButton("LoadProgram");
        buttonLoadProgram.addActionListener(e -> {
        });

        JButton buttonStepOnce = new JButton("Step");
        buttonStepOnce.addActionListener(e -> {
            oneStep();
        });
        panelControlButtons.add(buttonStepOnce);

        JButton buttonStepAll = new JButton("Step All");
        buttonStepAll.addActionListener(e -> {
            allStep();
        });
        panelControlButtons.add(buttonStepAll);
    }

    private void addRegisterValueLabels() {
        JPanel panelRegisterValues = new JPanel();
        panelRegisterValues.setLayout(new BoxLayout(panelRegisterValues, BoxLayout.PAGE_AXIS));
        panelRegisterValues.setBorder(new EmptyBorder(10, 10, 10, 10));
        rootPanel.add(panelRegisterValues, BorderLayout.CENTER);

        for (String registerName : new String[]{"abc", "def", "abc"}) {
            panelRegisterValues.add(new JLabelRegisterValue(registerName));
        }
    }

    @Override
    public void initialize() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        registerValueMap = new HashMap<>();

        setUpUI();
        pack();

        setVisible(true);
    }

    @Override
    public void oneStep() {

    }

    @Override
    public void allStep() {
    }

    public void setVirtualMachine(VirtualMachine virtualMachine) {
        this.virtualMachine = virtualMachine;
    }

    public void setCodeSimulator(CodeSimulator codeSimulator) {
        this.codeSimulator = codeSimulator;
    }
}
