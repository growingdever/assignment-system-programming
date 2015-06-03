package interfaces;

/**
 * Created by loki on 15. 6. 3..
 */
public interface ResourceManager {
    //메모리 영역을 초기화 하는 메소드
    void initializeMemory();

    //각 레지스터 값을 초기화 하는 메소드
    void initializeRegister();

    //디바이스 접근에 대한 메소드
    //디바이스는 각 이름과 매칭되는 파일로 가정한다
    //(F1 이라는 디바이스를 읽으면 F1 이라는 파일에서 값을 읽는다.)
    //해당 디바이스(파일)를 사용 가능한 상태로 만드는 메소드
    void initialDevice(String devName);

    //선택한 디바이스(파일)에 값을 쓰는 메소드. 파라메터는 변경 가능하다. void writeDevice(String devName, byte[] data, int size);
    //선택한 디바이스(파일)에서 값을 읽는 메소드. 파라메터는 변경 가능하다. byte[] readDevice(String devName, int size);
    //메모리 영역에 값을 쓰는 메소드
    void setMemory(int locate, byte[] data, int size);

    //레지스터에 값을 세팅하는 메소드. regNum은 레지스터 종류를 나타낸다.
    void setRegister(int regNum, int value);

    //메모리 영역에서 값을 읽어오는 메소드
    byte[] getMemory(int locate, int size);

    //레지스터에서 값을 가져오는 메소드
    int getRegister(int regNum);

    //바뀐 값들을 GUI에 적용시키는 메소드
    void affectVisualSimulator();
}
