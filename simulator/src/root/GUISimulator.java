package root;

import root.interfaces.VisualSimulator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.io.File;

/**
 * Created by loki on 15. 5. 28..
 */
public class GUISimulator extends JFrame implements VisualSimulator {

    private VirtualMachine virtualMachine;
    private CodeSimulator codeSimulator;
    private ObjectCodeLoader objectCodeLoader;

    private JPanel rootPanel;

    private JLabel labelProgramName;
    private JLabel labelProgramLength;
    private JLabel labelCurrInstruction;

    private JTextField textFieldDevice1;
    private JTextField textFieldDevice2;

    private JLabelRegisterValue labelRegisterA;
    private JLabelRegisterValue labelRegisterX;
    private JLabelRegisterValue labelRegisterL;
    private JLabelRegisterValue labelRegisterPC;
    private JLabelRegisterValue labelRegisterSW;
    private JLabelRegisterValue labelRegisterB;
    private JLabelRegisterValue labelRegisterS;
    private JLabelRegisterValue labelRegisterT;
    private JLabelRegisterValue labelRegisterF;

    private JTextArea textAreaMemoryDump;

    private JTextArea textAreaLogs;


    public GUISimulator() {
        super("SIC/XE Simulator");
        initialize();
    }

    private void setUpUI() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout());
        setContentPane(rootPanel);

        addControlButtons();
        addProgramInformation();
        addRegisterValueLabels();
        addMemoryDumps();
        addLogs();
    }

    private void addControlButtons() {
        JPanel panelControlButtons = new JPanel();
        panelControlButtons.setLayout(new BoxLayout(panelControlButtons, BoxLayout.LINE_AXIS));
        rootPanel.add(panelControlButtons, BorderLayout.PAGE_START);

        JButton buttonLoadProgram = new JButton("LoadProgram");
        buttonLoadProgram.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(GUISimulator.this) == JFileChooser.APPROVE_OPTION) {
                virtualMachine.initializeMemory();
                virtualMachine.initializeRegister();
                virtualMachine.initialDevices();

                textAreaLogs.setText(null);

                codeSimulator.initialize();

                String filename = fileChooser.getSelectedFile().getName();
                String dir = fileChooser.getCurrentDirectory().toString();
                objectCodeLoader.load(new File(dir + "/" + filename));
            }
        });
        panelControlButtons.add(buttonLoadProgram);

        JButton buttonStepOnce = new JButton("Step Once");
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

    private void addProgramInformation() {
        JPanel panel = new JPanel();
        rootPanel.add(panel, BorderLayout.WEST);

        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setPreferredSize(new Dimension(200, 100));

        labelProgramName = new JLabel("PROGRAM NAME");
        labelProgramName.setPreferredSize(new Dimension(150, 20));
        panel.add(labelProgramName);

        labelProgramLength = new JLabel("PROGRAM LENGTH");
        labelProgramLength.setPreferredSize(new Dimension(150, 20));
        panel.add(labelProgramLength);

        labelCurrInstruction = new JLabel("CURRENT INSTRUCTION");
        labelCurrInstruction.setPreferredSize(new Dimension(150, 20));
        panel.add(labelCurrInstruction);


        JPanel panelDevices = new JPanel();
        panel.add(panelDevices);

        panelDevices.setLayout(new BoxLayout(panelDevices, BoxLayout.PAGE_AXIS));
        panelDevices.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDevices.setPreferredSize(new Dimension(200, 100));

        JPanel panelDevice1 = new JPanel();
        panelDevice1.setPreferredSize(new Dimension(200, 40));
        panelDevice1.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDevices.add(panelDevice1);

        panelDevice1.add(new JLabel("Device1"));
        textFieldDevice1 = new JTextField("F1", 5);
        panelDevice1.add(textFieldDevice1);

        JPanel panelDevice2 = new JPanel();
        panelDevice2.setPreferredSize(new Dimension(200, 40));
        panelDevice2.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDevices.add(panelDevice2);

        panelDevice2.add(new JLabel("Device2"));
        textFieldDevice2 = new JTextField("05", 5);
        panelDevice2.add(textFieldDevice2);

        JButton buttonInitDevices = new JButton("Initialize devices");
        panelDevices.add(buttonInitDevices);
        buttonInitDevices.addActionListener(e -> {
            virtualMachine.initialDevice(textFieldDevice1.getText());
            virtualMachine.initialDevice(textFieldDevice2.getText());
        });

        panelDevices.add(Box.createRigidArea(new Dimension(5, 200)));
    }

    private void addRegisterValueLabels() {
        JPanel panelRegisterValues = new JPanel();
        panelRegisterValues.setLayout(new BoxLayout(panelRegisterValues, BoxLayout.PAGE_AXIS));
        panelRegisterValues.setBorder(new EmptyBorder(20, 20, 20, 20));
        rootPanel.add(panelRegisterValues, BorderLayout.EAST);

        // SIC
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

        // SIC/XE
        labelRegisterB = new JLabelRegisterValue("B");
        panelRegisterValues.add(labelRegisterB);
        labelRegisterS = new JLabelRegisterValue("S");
        panelRegisterValues.add(labelRegisterS);
        labelRegisterT = new JLabelRegisterValue("T");
        panelRegisterValues.add(labelRegisterT);
        labelRegisterF = new JLabelRegisterValue("F");
        panelRegisterValues.add(labelRegisterF);
    }

    private void addMemoryDumps() {
        textAreaMemoryDump = new JTextArea();
        textAreaMemoryDump.setAutoscrolls(true);
        textAreaMemoryDump.setFont(new Font("Courier", Font.TRUETYPE_FONT, 14));
        textAreaMemoryDump.setEditable(false);
        DefaultCaret caret = (DefaultCaret) textAreaMemoryDump.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scrollPane = new JScrollPane(textAreaMemoryDump);
        scrollPane.setPreferredSize(new Dimension(300, 300));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        rootPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void addLogs() {
        textAreaLogs = new JTextArea();
        textAreaLogs.setAutoscrolls(true);
        textAreaLogs.setFont(new Font("Courier", Font.TRUETYPE_FONT, 14));
        textAreaLogs.setEditable(false);
        DefaultCaret caret = (DefaultCaret) textAreaLogs.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scrollPane = new JScrollPane(textAreaLogs);
        scrollPane.setPreferredSize(new Dimension(300, 100));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        rootPanel.add(scrollPane, BorderLayout.SOUTH);
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
        codeSimulator.oneStep();
    }

    @Override
    public void allStep() {
        codeSimulator.allStep();
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
        labelRegisterA.setValue(registers[Constants.REGISTER_A]);
        labelRegisterX.setValue(registers[Constants.REGISTER_X]);
        labelRegisterL.setValue(registers[Constants.REGISTER_L]);
        labelRegisterPC.setValue(registers[Constants.REGISTER_PC]);
        labelRegisterSW.setValue(registers[Constants.REGISTER_SW]);
        labelRegisterB.setValue(registers[Constants.REGISTER_B]);
        labelRegisterS.setValue(registers[Constants.REGISTER_S]);
        labelRegisterT.setValue(registers[Constants.REGISTER_T]);
        labelRegisterF.setValue(registers[Constants.REGISTER_F]);
    }

    public void updateProgramInformation(String programName, int programLength) {
        labelProgramName.setText(String.format("Name : %s", programName));
        labelProgramLength.setText(String.format("Length : %s bytes", programLength));
        if( codeSimulator.getCurrInstructionData() != null ) {
            labelCurrInstruction.setText(String.format("Inst : %s", codeSimulator.getCurrInstructionData().GetMnemonic()));
        }
    }

    public void updateMemoryDump() {
        StringBuilder stringBuilder = new StringBuilder();

        byte[] bytes = virtualMachine.getMemory(0, 8192);
        for (int i = 0; i < bytes.length; i++) {
            if (i % 16 == 0 && i > 0) {
                stringBuilder.append("\n");
            } else if (i % 4 == 0 && i > 0) {
                stringBuilder.append(" ");
            }

            char c1 = (char) ((bytes[i] & 0x000000F0) >> 4);
            char c2 = (char) (bytes[i] & 0x0000000F);

            stringBuilder.append(Util.digitToHex(c1));
            stringBuilder.append(Util.digitToHex(c2));
        }

        textAreaMemoryDump.setText(stringBuilder.toString());

        int start = virtualMachine.getRegisterPC() * 2;
        int end = (virtualMachine.getRegisterPC() + codeSimulator.calculateInstructionSize(virtualMachine.getRegisterPC())) * 2;
        start += start / 8;
        end += end / 8;

        textAreaMemoryDump.setCaretPosition(start);

        Highlighter highlighter = textAreaMemoryDump.getHighlighter();
        highlighter.removeAllHighlights();
        try {
            highlighter.addHighlight(start,
                    end,
                    DefaultHighlighter.DefaultPainter);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void updateLogs() {
        String newLog = textAreaLogs.getText() + "\n" + codeSimulator.getLastLog();
        if( textAreaLogs.getText().equals("") ) {
            newLog = codeSimulator.getLastLog();
        }

        textAreaLogs.setText(newLog);
        textAreaLogs.setCaretPosition(newLog.length());
    }

}
