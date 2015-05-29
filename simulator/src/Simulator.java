import com.sun.javaws.exceptions.JNLParseException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

/**
 * Created by loki on 15. 5. 28..
 */
public class Simulator extends JFrame {

    private JPanel rootPanel;
    private JList listAssemblies;
    private JLabel labelRegisterA;
    private JPanel panelRegisterA;
    private JLabel labelRegisterAValue;
    private JPanel panelRegisterX;

    final String[] registerNames = { "A", "X", "L", "PC", "SW", "B", "S", "T", "F" };
    HashMap<String, Integer> registerValueMap;
    private boolean isEnd;


    public static void Start() {
        Simulator simulator = new Simulator();
        simulator.run();
    }

    public Simulator() {
        super("FirstForm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        isEnd = false;

        registerValueMap = new HashMap<>();
        for(String registerName : registerNames) {
            registerValueMap.put(registerName, 0);
        }

        setUpUI();
        pack();

        setVisible(true);
    }

    private void setUpUI() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout());
        setContentPane(rootPanel);

        String[] assemblies = new String[]{
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

        addControlButtons();

        listAssemblies = new JList(model);
        listAssemblies.setPreferredSize(new Dimension(200, 400));
        rootPanel.add(listAssemblies, BorderLayout.LINE_START);

        addRegisterValueLabels();
    }

    private void addControlButtons() {
        JPanel panelControlButtons = new JPanel();
        panelControlButtons.setLayout(new BoxLayout(panelControlButtons, BoxLayout.LINE_AXIS));
        rootPanel.add(panelControlButtons, BorderLayout.PAGE_START);

        JButton buttonStepOnce = new JButton("Step");
        buttonStepOnce.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO : Perform next instruction
            }
        });
        panelControlButtons.add(buttonStepOnce);

        JButton buttonStepAll = new JButton("Step All");
        buttonStepAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                while(!isEnd) {
                    buttonStepOnce.doClick();
                }
            }
        });
        panelControlButtons.add(buttonStepAll);
    }

    private void addRegisterValueLabels() {
        JPanel panelRegisterValues = new JPanel();
        panelRegisterValues.setLayout(new BoxLayout(panelRegisterValues, BoxLayout.PAGE_AXIS));
        panelRegisterValues.setBorder(new EmptyBorder(10, 10, 10, 10));
        rootPanel.add(panelRegisterValues, BorderLayout.CENTER);

        for(String registerName : registerNames) {
            panelRegisterValues.add(new JLabelRegisterValue(registerName));
        }
    }

    void run() {

    }

}
