import javax.swing.*;

/**
 * Created by loki on 15. 5. 29..
 */
public class DisabledItemSelectionModel extends DefaultListSelectionModel {

    @Override
    public void setSelectionInterval(int index0, int index1) {
        super.setSelectionInterval(-1, -1);
    }

}
