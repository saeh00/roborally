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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.CheckPoint;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.UTurn;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 75; // 60; // 75;
    final public static int SPACE_WIDTH = 75;  // 60; // 75;

    public final Space space;


    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }


        addToBoard();
        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void addToBoard(){
        Canvas spaceBackground = new Canvas(SPACE_WIDTH, SPACE_HEIGHT);
        GraphicsContext gc = spaceBackground.getGraphicsContext2D();
        gc.setStroke(Color.RED);
        gc.setLineWidth(5);
        gc.setLineCap(StrokeLineCap.ROUND);

        for (Heading wall:
                space.getWalls()
        )
        {
            switch (wall)
            {
                case NORTH:
                    gc.strokeLine(2, 2, SPACE_WIDTH - 2, 2);
                    break;
                case SOUTH:
                    gc.strokeLine(2, SPACE_HEIGHT - 2, SPACE_WIDTH - 2, SPACE_HEIGHT - 2);
                    break;
                case WEST:
                    gc.strokeLine(2, 2, 2, SPACE_HEIGHT - 2);
                    break;
                case EAST:
                    gc.strokeLine(SPACE_WIDTH - 2, 2, SPACE_WIDTH - 2, SPACE_HEIGHT - 2);
                    break;
            }
        }


        gc.setLineWidth(2);
        for(FieldAction action : space.getFieldActions()) {
            if(action instanceof ConveyorBelt)
            {
                gc.setStroke(Color.BLUE);
                switch (((ConveyorBelt) action).getHeading())
                {
                    case NORTH:
                        gc.strokeLine(2, SPACE_HEIGHT/2, SPACE_WIDTH/2, 2);
                        gc.strokeLine(SPACE_WIDTH - 2, SPACE_HEIGHT/2, SPACE_WIDTH/2, 2);
                        break;
                    case EAST:
                        gc.strokeLine(SPACE_WIDTH/2, 2, SPACE_WIDTH - 2, SPACE_HEIGHT/2);
                        gc.strokeLine(SPACE_WIDTH/2, SPACE_HEIGHT - 2, SPACE_WIDTH - 2, SPACE_HEIGHT/2);
                        break;
                    case WEST:
                        gc.strokeLine(SPACE_WIDTH/2, 2, 2, SPACE_HEIGHT/2);
                        gc.strokeLine(SPACE_WIDTH/2, SPACE_HEIGHT - 2, 2, SPACE_HEIGHT/2);
                        break;
                    case SOUTH:
                        gc.strokeLine(2, SPACE_HEIGHT/2, SPACE_WIDTH/2, SPACE_HEIGHT - 2);
                        gc.strokeLine(SPACE_WIDTH - 2, SPACE_HEIGHT/2, SPACE_WIDTH/2, SPACE_HEIGHT - 2);
                        break;
                    default:
                        this.setStyle("-fx-background-color: pink;");
                }
            }

            if (action instanceof UTurn) {

                gc.setStroke(Color.RED);
                gc.strokeLine(SPACE_WIDTH/2, 2, SPACE_WIDTH - 2, SPACE_HEIGHT/2);
                gc.strokeLine(SPACE_WIDTH/2, SPACE_HEIGHT - 2, SPACE_WIDTH - 2, SPACE_HEIGHT/2);
                gc.strokeLine(SPACE_WIDTH/2, 2, 2, SPACE_HEIGHT/2);
                gc.strokeLine(SPACE_WIDTH/2, SPACE_HEIGHT - 2, 2, SPACE_HEIGHT/2);

            }

            if(action instanceof CheckPoint) {
                gc.setStroke(Color.GREEN);
                if(((CheckPoint) action).getCheckpointNumber() == 0)
                {
                    //gc.strokeOval(SPACE_WIDTH/2 - 15, SPACE_HEIGHT/2 - 20, 30, 40);
                    gc.strokeLine(SPACE_WIDTH/2 - 10,SPACE_HEIGHT/2 - 20, SPACE_WIDTH/2 - 10, SPACE_HEIGHT/2 + 20 );
                    gc.strokeLine(SPACE_WIDTH/2 + 10,SPACE_HEIGHT/2 - 20, SPACE_WIDTH/2 + 10, SPACE_HEIGHT/2 + 20 );
                    gc.strokeLine(SPACE_WIDTH/2 - 10,SPACE_HEIGHT/2 + 20, SPACE_WIDTH/2 + 10, SPACE_HEIGHT/2 + 20 );
                    gc.strokeLine(SPACE_WIDTH/2 - 10,SPACE_HEIGHT/2 - 20, SPACE_WIDTH/2 + 10, SPACE_HEIGHT/2 - 20 );
                }
                else
                {
                    for(int i = 0; i < ((CheckPoint) action).getCheckpointNumber(); i++)
                    {
                        gc.strokeLine(SPACE_WIDTH/2 - 25 + 5*i,SPACE_HEIGHT/2 - 20, SPACE_WIDTH/2 - 25 + 5*i, SPACE_HEIGHT/2 + 20 );
                    }
                }
            }
        }
        this.getChildren().add(spaceBackground);
    }





    private void updatePlayer() {
        if(this.getChildren().size() > 1)
        {
            this.getChildren().remove(1,this.getChildren().size());
        }

        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0 );
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90*player.getHeading().ordinal())%360);
            this.getChildren().add(arrow);
        }
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            updatePlayer();
        }
    }

}
