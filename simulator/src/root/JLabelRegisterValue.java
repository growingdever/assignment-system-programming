package root;

import javax.swing.*;
import java.awt.*;

/**
 * Created by loki on 15. 5. 29..
 */
public class JLabelRegisterValue extends JLabel {

    String registerName;
    int value;

    public JLabelRegisterValue(String name) {
        super();

        this.setPreferredSize(new Dimension(120, 30));
        this.setHorizontalAlignment(SwingConstants.LEFT);
        this.setFont(new Font("Courier", Font.TRUETYPE_FONT, 14));

        this.registerName = name;
        this.setValue(0);
    }

    public void setValue(int v) {
        this.value = v;
        this.setText(String.format("%-4s : %08X", registerName, value));
    }

}
