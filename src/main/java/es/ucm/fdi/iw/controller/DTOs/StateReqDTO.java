package es.ucm.fdi.iw.controller.DTOs;
import lombok.Data;
@Data
public class StateReqDTO {
    private boolean isFinished;
    private int currentIndex;
    private int score;
}
