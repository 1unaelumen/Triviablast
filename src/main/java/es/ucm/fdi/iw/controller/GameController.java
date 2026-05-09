package es.ucm.fdi.iw.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import es.ucm.fdi.iw.controller.DTOs.AnswerReqDTO;
import es.ucm.fdi.iw.controller.DTOs.AnswerResDTO;
import es.ucm.fdi.iw.controller.DTOs.GameSetupDTO;
import es.ucm.fdi.iw.controller.DTOs.QuestionDataPrivateDTO;
import es.ucm.fdi.iw.controller.DTOs.QuestionDataPublicDTO;
import es.ucm.fdi.iw.model.MultiplayerGameSession;
import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;


@Controller
@RequestMapping("/game")
public class GameController {


    @Autowired
    private EntityManager entityManager;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static Map<String, MultiplayerGameSession> games = new HashMap<>();

    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {
        for (String name : new String[] { "u", "url", "ws", "topics" }) {
            model.addAttribute(name, session.getAttribute(name));
        }
    }

    @PostMapping("/create_game")
    public String createGame(@ModelAttribute GameSetupDTO setup, HttpSession session) {

        String code = UUID.randomUUID().toString().substring(0, 6);

        MultiplayerGameSession game = new MultiplayerGameSession();
        game.setCode(code);
        game.setSetup(setup);

        User user = (User) session.getAttribute("u");
        game.getPlayers().add(user);

        games.put(code, game);

        session.setAttribute("gameCode", code);

        return "redirect:/game/lobby?code=" + code;
    }

    @GetMapping("/lobby")
    public String lobby(@RequestParam String code, HttpSession session, Model model) {

        MultiplayerGameSession game = games.get(code);

        if (game == null) return "redirect:/";

        model.addAttribute("code", code);
        model.addAttribute("players", game.getPlayers());

        return "lobby";
    }
    @PostMapping("/join")
    public String joinGame(@RequestParam String code, HttpSession session, Model model) {

        MultiplayerGameSession game = games.get(code);

        if (game == null) {
            model.addAttribute("error", "Game not found");
            return "join_game";
        }

        User user = (User) session.getAttribute("u");
        game.getPlayers().add(user);
        System.out.println("User " + user.getUsername() + " joined game " + code);
        System.out.println("User " + user.getId() + " joined game " + code);

        session.setAttribute("gameCode", code);
        System.out.println("JOIN CALLED");
        return "gamepage";
    }

    @PostMapping("/start_game")
    public String startGame(Model model,HttpSession session) {
        String code = (String) session.getAttribute("gameCode");
        MultiplayerGameSession game = games.get(code);

        if (game == null) {
            return "redirect:/";
        }

        GameSetupDTO setup = game.getSetup();

        String url = "https://opentdb.com/api.php?amount=" + setup.getQuestionCount();
        if (setup.getCategory() != null && !setup.getCategory().isEmpty()) {
            url += "&category=" + setup.getCategory();
        }
        if (setup.getDifficulty() != null && !setup.getDifficulty().isEmpty()) {
            url += "&difficulty=" + setup.getDifficulty().toLowerCase();
        }

        RestTemplate rest = new RestTemplate();
        Map<String, Object> response = rest.getForObject(url, Map.class);

        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

        List<QuestionDataPrivateDTO> fullQuestions = new ArrayList<>(); // full data stored in session
        List<QuestionDataPublicDTO> publicQuestions = new ArrayList<>(); // safe version for frontend

        int i = 0;
        for (Map<String, Object> q : results) {
            List<String> answers = new ArrayList<>();
            String correct = (String) q.get("correct_answer");

            answers.add(correct);
            answers.addAll((List<String>) q.get("incorrect_answers"));
            Collections.shuffle(answers);

            fullQuestions.add(new QuestionDataPrivateDTO(i, (String) q.get("question"), answers, correct));
            publicQuestions.add(new QuestionDataPublicDTO(i, (String) q.get("question"), answers));

            i++;
        }
        game.setQuestions(fullQuestions);


        return "gamepage";
    }

    @PostMapping("/answer")
    @ResponseBody
    @Transactional
    public AnswerResDTO checkAnswer(@RequestBody AnswerReqDTO req, HttpSession session) {
        String code = (String) session.getAttribute("gameCode");
        MultiplayerGameSession game = games.get(code);

        if (game == null || game.getQuestions() == null) {
            return new AnswerResDTO(false, null);
        }

        List<QuestionDataPrivateDTO> questions = game.getQuestions();

        if (req.getQuestionId() >= questions.size()) {
            return new AnswerResDTO(false, null);
        }

        QuestionDataPrivateDTO q = questions.get(req.getQuestionId());
        boolean isCorrect = q.getCorrectAnswer().equals(req.getAnswer());

        if (isCorrect) {
            User user = (User) session.getAttribute("u");
            if (user != null) {
                User managedUser = entityManager.find(User.class, user.getId());

                if (managedUser != null) {
                    managedUser.setTotalPoints(
                            (managedUser.getTotalPoints() == null ? 0 : managedUser.getTotalPoints()) + 10);

                    session.setAttribute("u", managedUser);
                }
            }
        }

        return new AnswerResDTO(isCorrect, q.getCorrectAnswer());
        
    }

} 
