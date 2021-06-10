/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.dal.GameInDB;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceDialog;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    public GameController(Board board) {
        this.board = board;
    }

    // TODO lot of stuff missing here

    public void moveCurrentPlayerToSpace(@NotNull Space space) {
        System.out.println(space.x + " " + space.y);
        if (board.getSpace(space.x, space.y).getPlayer() == board.getCurrentPlayer()) {
            System.out.println("You are already standing on this square.");
            return;
        }

        if (board.getSpace(space.x, space.y).getPlayer() != null) {
            System.out.println(board.getSpace(space.x, space.y).getPlayer().getName() + " is already on that square.");
            return;
        }

        board.getCurrentPlayer().setSpace(space);
        System.out.println(board.getCurrentPlayer().getSpace().x + " " + board.getCurrentPlayer().getSpace().y);
        board.setCount(board.getCounter() + 1);

        System.out.println(board.getStatusMessage());

        board.changePlayer();

        System.out.println("It is now " + board.getCurrentPlayer().getName() + "'s turn");

        for (FieldAction action : space.getFieldActions()) {

            if (action instanceof CheckPoint) {

                if (checkpointWinner()) {
                    System.out.println("The game has ended, " + board.getCurrentPlayer().getName() + " has won.");
                    ChoiceDialog<GameInDB> dialog = new ChoiceDialog<>();
                    dialog.setTitle("het");
                    dialog.setHeaderText("Select save to load");
                    Optional<GameInDB> result = dialog.showAndWait();
                }

                action.doAction(this, space);
            }

            if (action instanceof UTurn) {
                action.doAction(this, space);
            }

            if (action instanceof ConveyorBelt) {
                action.doAction(this, space);
            }

        }

    }

    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    if (command.isInteractive()) {
                        board.setPhase(Phase.PLAYER_INTERACTION);
                        return;
                    }
                    executeCommand(currentPlayer, command);
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    step++;

                    if (checkpointWinner()) {
                        System.out.println("The game has ended, " + board.getCurrentPlayer().getName() + " has won.");
                        ChoiceDialog<GameInDB> dialog = new ChoiceDialog<>();
                        dialog.setTitle("het");
                        dialog.setHeaderText("Select save to load");
                        Optional<GameInDB> result = dialog.showAndWait();
                    }

                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    private boolean checkpointWinner() {

        for (int i = 0; i < board.getPlayersNumber(); i++) {

            Player p = board.getPlayer(i);
            if (p.getLastCheckpoint() >= 3) {
                //Platform.exit();
                return true;
            }

        }
        return false;
    }


    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                case OPTION_LEFT_RIGHT:
                    switch (command){
                        case LEFT:
                            this.turnLeft(player);
                            break;
                        case RIGHT:
                            this.turnRight(player);
                            break;
                        default:
                    }
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    private void move(@NotNull Player player, int step) {
        Space space = null;
        switch (player.getHeading()) {
            case SOUTH:
                space = board.getSpace(board.getCurrentPlayer().getSpace().x, board.getCurrentPlayer().getSpace().y + step);
                break;
            case NORTH:
                space = board.getSpace(board.getCurrentPlayer().getSpace().x, board.getCurrentPlayer().getSpace().y - step);
                break;
            case WEST:
                space = board.getSpace(board.getCurrentPlayer().getSpace().x - step, board.getCurrentPlayer().getSpace().y);
                break;
            case EAST:
                space = board.getSpace(board.getCurrentPlayer().getSpace().x + step, board.getCurrentPlayer().getSpace().y);
                break;
        }
        if (space == null) {
            System.out.println("You cannot move to this space.");
            return;
        }
        if (space.getPlayer() != null) {
            System.out.println(space.getPlayer().getName() + " is already on that space.");
            return;
        }
        player.setSpace(space);
        for (FieldAction action : space.getFieldActions()) {

            if (action instanceof CheckPoint) {
                action.doAction(this, space);

                if (checkpointWinner()) {
                    System.out.println("The game has ended, " + board.getCurrentPlayer().getName() + " has won.");
                    ChoiceDialog<GameInDB> dialog = new ChoiceDialog<>();
                    dialog.setTitle("het");
                    dialog.setHeaderText("Select save to load");
                    Optional<GameInDB> result = dialog.showAndWait();
                }

            }

            if (action instanceof ConveyorBelt) {
                action.doAction(this, space);
            }

        }
    }

    public void moveForward(@NotNull Player player) {
        if (player.board == board) {
            Space space = player.getSpace();
            Heading heading = player.getHeading();

            Space target = board.getNeighbour(space, heading);

            if (target != null) {
                try {
                    moveToSpace(player, target, heading);
                } catch (ImpossibleMoveException e) {
                    // we don't do anything here  for now; we just catch the
                    // exception so that we do no pass it on to the caller
                    // (which would be very bad style).
                }
            }
        }
    }

    void moveToSpace(@NotNull Player player, @NotNull Space space, @NotNull Heading heading) throws ImpossibleMoveException {
        assert board.getNeighbour(player.getSpace(), heading) == space; // make sure the move to here is possible in principle
        Player other = space.getPlayer();
        if (other != null){
            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                // XXX Note that there might be additional problems with
                //     infinite recursion here (in some special cases)!
                //     We will come back to that!
                moveToSpace(other, target, heading);

                // Note that we do NOT embed the above statement in a try catch block, since
                // the thrown exception is supposed to be passed on to the caller

                assert space.getPlayer() == null : "Space to move to is not free: " + space; // make sure space is free now

            } else {
                throw new ImpossibleMoveException(player, space, heading);
            }
        }
        player.setSpace(space);

        for (FieldAction action : space.getFieldActions()) {

            if (action instanceof CheckPoint) {
                action.doAction(this, space);

                if (checkpointWinner()) {
                    System.out.println("The game has ended, " + board.getCurrentPlayer().getName() + " has won.");
                    ChoiceDialog<GameInDB> dialog = new ChoiceDialog<>();
                    dialog.setTitle("het");
                    dialog.setHeaderText("Select save to load");
                    Optional<GameInDB> result = dialog.showAndWait();
                }

            }

            if (action instanceof ConveyorBelt) {
                action.doAction(this, space);
            }

        }


    }

    public void fastForward(@NotNull Player player) {
        move(player, 2);
    }

    public void turnRight(@NotNull Player player) {
        switch (player.getHeading()) {
            case EAST:
                player.setHeading(Heading.SOUTH);
                break;
            case WEST:
                player.setHeading(Heading.NORTH);
                break;
            case NORTH:
                player.setHeading(Heading.EAST);
                break;
            case SOUTH:
                player.setHeading(Heading.WEST);
                break;
        }
    }

    public void turnLeft(@NotNull Player player) {
        switch (player.getHeading()) {
            case EAST:
                player.setHeading(Heading.NORTH);
                break;
            case WEST:
                player.setHeading(Heading.SOUTH);
                break;
            case NORTH:
                player.setHeading(Heading.WEST);
                break;
            case SOUTH:
                player.setHeading(Heading.EAST);
                break;
        }

    }

    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    public void executeCommandOptionAndContinue(Command command) {
        Player currentPlayer = board.getCurrentPlayer();
        if (currentPlayer != null &&
                board.getPhase() == Phase.PLAYER_INTERACTION &&
                command != null) {
            board.setPhase(Phase.ACTIVATION);
            executeCommand(currentPlayer, command);

            int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
            if (nextPlayerNumber < board.getPlayersNumber()) {
                board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
            } else {
                int step = board.getStep() + 1;
                if (step < Player.NO_REGISTERS) {
                    makeProgramFieldsVisible(step);
                    board.setStep(step);
                    board.setCurrentPlayer(board.getPlayer(0));
                } else {
                    startProgrammingPhase();
                }
            }
        }
    }

    class ImpossibleMoveException extends Exception {

        private Player player;
        private Space space;
        private Heading heading;

        public ImpossibleMoveException(Player player, Space space, Heading heading) {
            super("Move impossible");
            this.player = player;
            this.space = space;
            this.heading = heading;
        }
    }

}
