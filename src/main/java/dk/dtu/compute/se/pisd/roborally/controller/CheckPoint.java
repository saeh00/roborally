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
import dk.dtu.compute.se.pisd.roborally.dal.RepositoryAccess;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.application.Platform;
import javafx.scene.control.*;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * ...
 *
 * @author Markus Visvaldis Ingemann Thieden, s164920
 * @author Tobias Sønderskov Hansen, s164270
 */

public class CheckPoint extends FieldAction {

    private final int checkpointNumber;

    public CheckPoint(int checkpointNumber) {
        this.checkpointNumber = checkpointNumber;
    }

    public int getCheckpointNumber()
    {
        return checkpointNumber;
    }

    /**
     * If the checkpointNumber matches the player's nextCheckpoint, the player's nextCheckpoint is updated
     * @param gameController the gameController of the respective game
     * @param space the space this action should be executed for
     * @return
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        Player player = space.getPlayer();

        if(player.getLastCheckpoint() == checkpointNumber)
        {
            player.setLastCheckpoint(checkpointNumber + 1);

            /*ChoiceDialog<GameInDB>dialog = new ChoiceDialog<>();
            dialog.setTitle("Load game");
            dialog.setHeaderText("Select save to load");
            Optional<GameInDB> result = dialog.showAndWait();*/


        }

        if (gameController.checkpointWinner()) {

            ButtonType view_board = new ButtonType("View board", ButtonBar.ButtonData.OK_DONE);
            ButtonType exit = new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Congratulations " + player.getName() + ". What should happen next?", view_board, exit);

            alert.setTitle(player.getName() + " has won!");
            Optional<ButtonType> option = alert.showAndWait();

            if (option.orElse(view_board) == exit) {
                // exit
                Platform.exit();
            } else {
                gameController.view_board = true;
                System.out.println("Player has returned to board");
            }

        }

        return false;
    }

}
