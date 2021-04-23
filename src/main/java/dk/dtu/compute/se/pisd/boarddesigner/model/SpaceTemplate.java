package dk.dtu.compute.se.pisd.boarddesigner.model;

import pisd.dk.dtu.compute.se.roborally.model.Board;
import pisd.dk.dtu.compute.se.roborally.model.FieldAction;
import pisd.dk.dtu.compute.se.roborally.model.Heading;
import pisd.dk.dtu.compute.se.roborally.model.Space;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple template of the Space class.
 * The main improvements of this template is that it can be used
 * to save with Gson since it doesn't have the same recursion issues
 * that exists with the normal Space class.
 * Methods have been created to create instances of Space and create
 * an instance of SpaceTemplate from an instance of Space.
 *
 * NB! This class should only be used for the purpose of saving board layouts
 *     and should never exist on it's own while the actual game is running.
 *
 * @author Gustav Utke Kauman, s195396@student.dtu.dk
 */
public class SpaceTemplate {

    public int playerNo;
    public int x;
    public int y;
    public List<Heading> walls = new ArrayList<>();
    public List<FieldAction> actions = new ArrayList<>();

    public SpaceTemplate fromSpace(Space space) {
        this.x = space.x;
        this.y = space.y;
        this.playerNo = space.getStartPlayerNo();

        this.walls = space.getWalls();
        this.actions = space.getActions();

        return this;
    }

    public Space toSpace(Board board) {

        Space space = new Space(board, this.x, this.y);
        space.setStartPlayerNo(this.playerNo);

        for (FieldAction action : actions) {
            space.addAction(action);
        }

        for (Heading wall : walls) {
            space.addWall(wall);
        }

        return space;

    }

}
