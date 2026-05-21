# TriviaBlast

**Juego de preguntas y respuestas en línea** Online trivia game designed for individual play.

## Proposal

### Description

TriviaBlast is an interactive game that allows users to practice and earn points in single-player matches, or compete on a classic color-category board in private multiplayer rooms. Questions are obtained from the [Open Trivia Database (OpenTDB)](https://opentdb.com/), offering a wide variety of categories and difficulty levels.

### Roles y permisos

| Role           | Permissions                                                                 |
|---------------|--------------------------------------------------------------------------|
| **Player**   | Manage account, play single-player matches, view leaderboard. |
| **Administrator** | Hide, edit, delete, or restore user visibility, view users' game history to find if there is a cheating. |

#### Single-player mode

- Custom configuration: Seelcting number of questions, category, difficulty.
- Score based on difficulty and response speed.

### Point system and leaderboard

- Points are earned depending on the game mode and are reflected on the leaderboard (visible only to registered users).
- Players are ranked from highest to lowest score.
- Administrators can hide players from the leaderboard.

### Game flow

1. Register or log in.
2. Design your own game.
3. Play the game
4. Calculate points and update leaderboard.

### Database Tables

- **IWUser**: Stores the player's id, avatar, email, password, total points, roles, usernames and visibility states.
- **Game**: Stores each game's number of questions, id, playing user's id, difficulty, state and scores.

### Completed

- **Sign in or up**: Account registration and login are fully implemented. Error cases are handled correctly (for example, when two different passwords are entered while creating an account). After creating an account, the user is automatically logged in.
- **Home page**: Correctly implemented.  Once logged in, users can start a single-player game. 
- **Single-player game**: The first one, related to game creation and settings, is fully implemented. The question types correspond to those provided by the API being used. The actual single-player game is also completely implemented. Questions and possible answers are displayed. If the player answers incorrectly, the correct answer is shown afterward. It is worth mentioning that question validation is handled entirely in the backend, making cheating impossible (to verify that the frontend never receives the correct answers, start a single-player game, open the browser console, and type window.questions). There is also a button to quit the game, and the interface displays the current question number and accumulated points. Each correct answer gives the user 10 points.
- **User profile**: Displays the user’s profile picture, username, and total points and game history. It also allows users to log out or completely delete their account. The edit button allows changing the profile picture, username, email, or password (which must be validated using the previous password). 
- **Scoreboard**: Displays registered users along with their points. If an administrator decides to hide a user, the visibility status is updated in the database, and the user no longer appears in the table for normal users.
- **Visual design**: Interface designed at the design application Figma. Used vibrant colours and implemented attractive design for the players to enjoy. While implementing, Figma's Developer Tool extension used to take the structure. 
- **Admin page**: Admin can see the users and related informations at the user table. Below the table, you will find the gam history where all games are visible. If admin suspects of one user, can click to user's username and see their profile and their own game history to examine.

###Use of AI
- The AI used in this project is ChatGPT. AI used to get help when get errors/problems that the group could not understand/solve.
- For the UI implementation, Figma Developer Tool used to pull the design from the application.
- 
### Test users at DB

USERNAME : infinite.alex5
PASSWORD : 1234

USERNAME : ana
PASSWORD : 234

USERNAME : juan
PASSWORD : 123

USERNAME : maria1
PASSWORD : 12

(admin) USERNAME : a   
	PASSWORD : aa
(admin) USERNAME : b   
	PASSWORD : bb
