var app = angular.module('hangmanApp', []);


app.controller('CurrentGamesController', function ($scope, $http, $interval) {
    $scope.games = [];

    $scope.displayCurrentGames = function () {
        $http.get("/game/peek").then(function (response) {
            $scope.games = response.data;
        })
    };

    $scope.peekInterval = $interval(function () {
        $scope.displayCurrentGames();
    }, 2000);

});

app.controller('GameController', function ($scope, $http, $interval, $window) {
    $scope.game = null;
    $scope.obfuscatedWord = [];
    $scope.attemptsLeft = 10;
    $scope.loser = false;
    $scope.winner = false;
    $scope.gameNotFound = false;
    $scope.pollingInterval = null;
    $scope.gameIdToLoad = null;
    $scope.gameLoaded = false;
    $scope.gameIdToLoad = null;
    $scope.killCalled = false;
    $scope.numberOfPlayers = 1;
    $scope.isInError = false;
    var alphabet = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"];
    $scope.letters = [];

    $scope.createNewGame = function () {
        stopPolling();
        $http.get("/game/create").then(function (response) {
            $scope.game = response.data;
            $scope.obfuscateNewWordAndLoadLetters($scope.game.word)
        });
        startPolling();
    };

    $scope.loadGame = function () {
        stopPolling();
        $http.get("/game/load", {
            params: {
                gameid: $scope.gameIdToLoad
            }
        }).then(function (response) {
            $scope.game = response.data;
            if (!$scope.game.gameId) {
                $scope.gameNotFound = true;
            } else {
                $scope.obfuscatedLoadedWord();
                loadLettersForLoadedGame();
                $scope.gameLoaded = true;
                $scope.attemptsLeft = $scope.game.attemptsLeft;
                determineIfWinner($scope.obfuscatedWord);
                $scope.numberOfPlayers++;
                $scope.gameNotFound = false;
                startPolling();
            }
        })
    };

    $scope.pollGame = function () {
        $http.get("/game/poll", {
            params: {
                gameid: $scope.game.gameId
            }
        }).then(function (response) {
            $scope.game = response.data;
            if(!$scope.game.gameId)
            {
                $scope.isInError = true;
            }
            $scope.obfuscatedLoadedWord();
            loadLettersForLoadedGame();
            $scope.gameLoaded = true;
            $scope.attemptsLeft = $scope.game.attemptsLeft;
            determineIfWinner($scope.obfuscatedWord);
        })
    };

    $scope.obfuscateNewWordAndLoadLetters = function (word) {
        for (var i = 0; i < word.length; i++) {
            $scope.obfuscatedWord.push("-");
        }
        loadLettersForNewGame();
    };

    $scope.obfuscatedLoadedWord = function () {
        $scope.obfuscatedWord = [];
        var splitWord = $scope.game.word.split('');
        for (var i = 0; i < splitWord.length; i++) {
            if (($scope.game.usedCharacters.indexOf(splitWord[i]) >= 0)) {
                $scope.obfuscatedWord.push(splitWord[i]);
            } else {
                $scope.obfuscatedWord.push("-")
            }
        }
    };

    $scope.obfuscatedUpdatedWordAndHandleAttempts = function () {

        var splitWord = $scope.game.word.split('');
        var updatedObfuscatedWord = [];
        var letterFound = false;

        for (var i = 0; i < splitWord.length; i++) {
            if (($scope.game.usedCharacters.indexOf(splitWord[i]) >= 0) && $scope.obfuscatedWord.indexOf(splitWord[i]) < 0) {

                updatedObfuscatedWord.push(splitWord[i]);
                letterFound = true;
            }
            else if ($scope.obfuscatedWord.indexOf(splitWord[i]) >= 0) {
                updatedObfuscatedWord.push(splitWord[i]);
            }
            else {
                updatedObfuscatedWord.push("-");
            }
        }

        if (!letterFound) {
            $scope.attemptsLeft--;
            if ($scope.attemptsLeft <= 0) {
                $scope.loser = true;
                stopPolling();
                disableAllLetters();
            }
        }
        determineIfWinner(updatedObfuscatedWord);
        $scope.obfuscatedWord = updatedObfuscatedWord;
    };

    $scope.handleUpdate = function (usedCharacter) {
        stopPolling();
        $http.post("/game/update", new GameUpdateInfo($scope.game.gameId, usedCharacter)).then(function () {
            $scope.game.usedCharacters.push(usedCharacter);
            $scope.obfuscatedUpdatedWordAndHandleAttempts();
            disableLetter(usedCharacter);
        });
        startPolling();
    };

    $scope.killGame = function () {
        stopPolling();
        $http.post("/game/kill", new GameUpdateInfo($scope.game.gameId, null)).then(function () {
            $window.location.href = '../index.html';
        });
    };

    function loadLettersForNewGame() {
        for (var i = 0; i < alphabet.length; i++) {
            $scope.letters.push(new Letter(alphabet[i]))

        }
        $scope.gameLoaded = true;
    }

    function loadLettersForLoadedGame() {
        $scope.letters = [];
        for (var i = 0; i < alphabet.length; i++) {
            if (($scope.game.usedCharacters.indexOf(alphabet[i]) >= 0)) {
                var letter = new Letter(alphabet[i]);
                letter.isUsed = true;
                $scope.letters.push(letter);
            }
            else {
                $scope.letters.push(new Letter(alphabet[i]))
            }
        }
    }

    function determineIfWinner(updatedObfuscatedWord) {
        if (updatedObfuscatedWord.indexOf("-") < 0) {
            disableAllLetters();
            $scope.winner = true;
        }
    }

    function disableLetter(usedCharacter) {
        for (var i = 0; i < $scope.letters.length; i++) {
            if ($scope.letters[i].character === usedCharacter) {
                $scope.letters[i].isUsed = true;
                break;
            }
        }
    }

    function disableAllLetters() {
        for (var i = 0; i < $scope.letters.length; i++) {
            $scope.letters[i].isUsed = true;
        }
    }

    function startPolling() {
        $scope.pollingInterval = $interval(function () {
            $scope.pollGame()
        }, 2000);
    }

    function stopPolling() {
        $interval.cancel($scope.pollingInterval);
    }

});





