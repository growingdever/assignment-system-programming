package root.interfaces;

/**
 * Created by loki on 15. 6. 3..
 */
public interface VisualSimulator {
    //시뮬레이터를 동작시키기 위한 세팅을 수행한다.
    //SIC 시뮬레이터를 통해 로더를 수행시키고, 로드된 값들을 읽고, //하나의 스텝 혹은 남은 명령어 전체를
    //실행 할 수 있는 상태로 만들어 놓는다.
    void initialize();

    //하나의 명령어만 실행하는 메소드로써 SIC 시뮬레이터에게 작업을 전달
    void oneStep();

    //남은 명령어를 모두 실행하는 메소드로써 SIC 시뮬레이터에 작업을 전달
    void allStep();
}
