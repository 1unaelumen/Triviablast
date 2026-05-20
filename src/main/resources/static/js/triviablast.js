document.addEventListener('DOMContentLoaded', () => {
    initAuthToggle();
    initBoard();
    initTableToggleButtons();
    initTrivia();

});

/* =========================
   AUTH (Login / Register)
========================= */
function initAuthToggle() {
    const login = document.getElementById("loginForm");
    const register = document.getElementById("registerForm");

    if (!login || !register) return;

    window.toggleForms = function () {
        const isHidden = login.style.display === "none";
        login.style.display = isHidden ? "block" : "none";
        register.style.display = isHidden ? "none" : "block";
    };
}

/* =========================
   SCOREBOARD
========================= */
function initTableToggleButtons() {
    const buttons = document.querySelectorAll('.toggle-btn');
    if (!buttons.length) return;

    buttons.forEach(button => {
        button.addEventListener('click', () => {
            const userId = button.dataset.id;
            const url = window.location.origin + '/admin/toggleView/' + userId;

            fetch(url, {
                method: "POST",
                headers: {
                    [config.csrf.header]: config.csrf.value
                }
            })
            .then(res => {
                if (!res.ok) throw new Error("Error updating visibility");
                return res.json();
            })
            .then(data => {
                const tr = button.closest('tr');
                const isVisible = data.visibilityState; 

                if (!isVisible) {
                    tr.classList.add('opacity-50');
                    button.textContent = 'Display';
                    button.classList.replace('btn-outline-danger', 'btn-success');
                } else {
                    tr.classList.remove('opacity-50');
                    button.textContent = 'Hide';
                    button.classList.replace('btn-success', 'btn-outline-danger');
                }
            })
            .catch(err => console.error(err));
        });
    });
}

/* =========================
   SINGLEPLAYER TRIVIA SCRIPT
========================= */

document.addEventListener("DOMContentLoaded", () => {

    let currentIndex = 0;
    let score = 0;

    const statusEl = document.getElementById("gameStatus");
    const nextBtn = document.getElementById("nextBtn");

    function updateStatus() {
        const total = window.questions.length;
        statusEl.textContent = `${currentIndex + 1}/${total} ${score} points`;
    }

    function updateState(isFinished = false) {
    fetch("/game/end_single_game", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            [config.csrf.header]: config.csrf.value
        },
        body: JSON.stringify({
            currentIndex: currentIndex,
            score: score,
            finished: isFinished
        })
    }).catch(err => console.error("state update failed:", err));
}

    function showQuestion(index) {
        const q = window.questions[index];

        document.getElementById("question").innerHTML = q.question;
        const answersEl = document.getElementById("answers");
        const feedbackEl = document.getElementById("feedback");

        answersEl.innerHTML = "";
        feedbackEl.innerHTML = "";
        nextBtn.disabled = true;

        q.answers.forEach(answer => {
            const btn = document.createElement("button");
            btn.className = "btn btn-outline-primary";
            btn.innerHTML = answer;

            btn.onclick = () => sendAnswer(answer, q.id, btn);

            answersEl.appendChild(btn);
        });

        updateStatus();
    }

    function sendAnswer(answer, questionId, clickedBtn) {
        fetch("/game/answer", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                [config.csrf.header]: config.csrf.value
            },
            body: JSON.stringify({ questionId, answer })
        })
            .then(res => res.json()
                .then(data => {
                    console.log("Clicked:", answer);
                    console.log("Server says correct:", data.correctAnswer);

                    const answersEl = document.getElementById("answers");
                    const feedbackEl = document.getElementById("feedback");

                    Array.from(answersEl.children).forEach(btn => btn.disabled = true);
                    const isCorrect = (data.correct === true || data.correct === 'true');

                    if (isCorrect) {
                        clickedBtn.classList.replace("btn-outline-primary", "btn-success");
                        feedbackEl.textContent = "Correct!";
                        score += 10;
                    } else {
                        clickedBtn.classList.replace("btn-outline-primary", "btn-danger");
                        feedbackEl.textContent = "Incorrect!";

                        const correctAnswer = decodeHtml(data.correctAnswer).trim().toLowerCase();

                        Array.from(answersEl.children).forEach(btn => {
                            if (btn.textContent.trim().toLowerCase() === correctAnswer) {
                                btn.classList.replace("btn-outline-primary", "btn-success");
                            }
                        });
                    }

                    nextBtn.disabled = false;
                    updateStatus();
                }));
    }

    nextBtn.onclick = () => {
        currentIndex++;
        if (currentIndex < window.questions.length) {
            updateState(false);
            showQuestion(currentIndex);
        } else {
            updateState(true);
            document.getElementById("gameCard").innerHTML =
                `<div class="text-center fs-4 fw-bold">
                    Game Over!<br>Your Score: ${score} points
                </div>`;
            nextBtn.hidden = true;
        }
    };

    showQuestion(currentIndex);
});

/* =========================
   HELPERS
========================= */
function decodeHtml(html) {
    const txt = document.createElement("textarea");
    txt.innerHTML = html;
    return txt.value;
}