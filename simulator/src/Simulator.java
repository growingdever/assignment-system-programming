import forms.FirstForm;

import javax.swing.*;

/**
 * Created by loki on 15. 5. 28..
 */
public class Simulator {

    public static void Start() {
        Simulator simulator = new Simulator();
        simulator.run();
    }

    public Simulator() {

    }

    void run() {
        JFrame firstForm = new FirstForm();
    }

}
