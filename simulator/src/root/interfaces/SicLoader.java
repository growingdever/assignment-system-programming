package root.interfaces;

import java.io.File;

/**
 * Created by loki on 15. 6. 3..
 */
public interface SicLoader {
    //파일로부터 목적코드를 읽어와 메모리에 로드한다.
    void load(File objFile);
    //목적코드의 한 줄을 읽고, 각 헤더(H, T, M 등)에 맞는 기능을 수행하여 //각 메모리 및 명령어 리스트를 초기화 한다.
    void readLine(String line);
}
