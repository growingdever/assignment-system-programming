import interfaces.VisualSimulator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.HashMap;

/**
 * Created by loki on 15. 5. 28..
 */
public class GUISimulator extends JFrame implements VisualSimulator {

    private VirtualMachine virtualMachine;
    private CodeSimulator codeSimulator;
    private ObjectCodeLoader objectCodeLoader;

    private JPanel rootPanel;
    private JList<String> listAssemblies;

    private JLabelRegisterValue labelRegisterA;
    private JLabelRegisterValue labelRegisterX;
    private JLabelRegisterValue labelRegisterL;
    private JLabelRegisterValue labelRegisterPC;
    private JLabelRegisterValue labelRegisterSW;


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
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(GUISimulator.this) == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getName();
                String dir = fileChooser.getCurrentDirectory().toString();
                objectCodeLoader.load(new File(dir + "/" + filename));
            }
        });
        panelControlButtons.add(buttonLoadProgram);

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

        labelRegisterA = new JLabelRegisterValue("A");
        panelRegisterValues.add(labelRegisterA);
        labelRegisterX = new JLabelRegisterValue("X");
        panelRegisterValues.add(labelRegisterX);
        labelRegisterL = new JLabelRegisterValue("L");
        panelRegisterValues.add(labelRegisterL);
        labelRegisterPC = new JLabelRegisterValue("PC");
        panelRegisterValues.add(labelRegisterPC);
        labelRegisterSW = new JLabelRegisterValue("SW");
        panelRegisterValues.add(labelRegisterSW);
    }

    @Override
    public void initialize() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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

    public void setObjectCodeLoader(ObjectCodeLoader objectCodeLoader) {
        this.objectCodeLoader = objectCodeLoader;
    }

    public void updateRegisters(int[] registers) {
        labelRegisterA.setValue(registers[0]);
        labelRegisterX.setValue(registers[1]);
        labelRegisterL.setValue(registers[2]);
        labelRegisterPC.setValue(registers[8]);
        labelRegisterSW.setValue(registers[9]);
    }

}
