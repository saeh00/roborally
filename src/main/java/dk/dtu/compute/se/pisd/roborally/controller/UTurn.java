package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * The purpose of this obstacle is to set the opposite heading of the player
 */

public class UTurn extends FieldAction {

    /**
     * @param gameController - the current instance for game
     * @param space - the space on the board
     *
     *              The code in the method sets the heading to the opposite for the player
     *
     */

    @Override
    public boolean doAction(GameController gameController, Space space) {

        Player player = gameController.board.getSpace(space.x, space.y).getPlayer();
        Heading heading = player.getHeading();

        switch (heading) {

            case NORTH -> player.setHeading(Heading.SOUTH);
            case SOUTH -> player.setHeading(Heading.NORTH);
            case EAST -> player.setHeading(Heading.WEST);
            case WEST -> player.setHeading(Heading.EAST);

            default -> System.err.println("Could not perform a uturn");
        }

        return false;
    }

}
