package es.ucm.fdi.iw.controller.DTOs;

import java.util.List;

public class StartGameDTO {
    public List<QuestionDataPublicDTO> questions;

    public StartGameDTO(List<QuestionDataPublicDTO> questions) {
        this.questions = questions;
    }
}