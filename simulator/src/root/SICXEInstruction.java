package root;

/**
 * Created by loki on 15. 6. 4..
 */
public abstract class SICXEInstruction {
    protected byte[] bytes;
    protected boolean isExtended;

    public SICXEInstruction(byte[] bytes, boolean e) {
        this.bytes = bytes;
        isExtended = e;
    }

    public void setExtended(boolean e) {
        this.isExtended = e;
    }

    public abstract void Execute(VirtualMachine virtualMachine);
}
