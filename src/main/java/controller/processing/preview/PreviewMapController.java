package controller.processing.preview;

import gui.GTMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.locationtech.jts.io.ParseException;
import utils.WKTUtil;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class PreviewMapController implements Initializable {
    public static final int GRID_SIZE = 150;
    @FXML
    private AnchorPane container;

    private GTMap gtMap;
    private double[] end;
    private double[] init;
    private Point2D initPoint = new Point2D(0,0);
    private Point2D endPoint = new Point2D(200,200);
    private Point2D clickPoint = null;
    private String wkt = null;
    private String productFootprint;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gtMap = new GTMap(558, 548, false);
        container.getChildren().add(gtMap);
        AnchorPane.setRightAnchor(gtMap,0.0);
        AnchorPane.setLeftAnchor(gtMap,0.0);
        AnchorPane.setTopAnchor(gtMap,0.0);
        AnchorPane.setBottomAnchor(gtMap,0.0);
        gtMap.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClickedOnMap);
        gtMap.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onMouseDraggedOnMap);
        gtMap.addEventHandler(MouseEvent.MOUSE_PRESSED, this::onMousePressedOnMap);
        gtMap.addEventHandler(MouseEvent.MOUSE_RELEASED, this::onMouseReleasedOnMap);
    }

    public void setAreaOfWork(String area) throws ParseException {
        this.productFootprint = area;
        gtMap.removeLayer("areaOfWork");
        gtMap.createFeatureFromWKT(area,"areaOfWorkID","areaOfWork");
        gtMap.createAndDrawLayer("areaOfWork", Color.BLACK,null);
        gtMap.refresh();
        gtMap.focusOnLayer("areaOfWork");
        createGrid();
    }

    public void onMouseClickedOnMap(MouseEvent e) {
        Rectangle2D rectangle2D = new Rectangle2D(initPoint.getX(), initPoint.getY(), endPoint.getX(), endPoint.getY());
        if (rectangle2D.contains(new Point2D(e.getX(),e.getY())) && clickPoint == null)
            clickPoint = new Point2D(e.getSceneX(),e.getSceneY());

    }

    public void onMouseReleasedOnMap(MouseEvent e) {
        clickPoint = null;
    }

    public void onMousePressedOnMap(MouseEvent e) {
        moveGrid(e);
    }

    private void calculatePoints(MouseEvent e) {
        double deltaX = e.getX() - clickPoint.getX();
        double deltaY = e.getY() - clickPoint.getY();

        if (initPoint.getX() + deltaX > gtMap.getWidth() || initPoint.getY() + deltaY > gtMap.getHeight())
            return;
        if (endPoint.getX() + deltaX > gtMap.getWidth() || initPoint.getY() + deltaY + 150 > gtMap.getHeight())
            return;
        if (initPoint.getX() + deltaX < 0.0 || initPoint.getY() + deltaY < 0.0)
            return;
        if (endPoint.getX() + deltaX < 0.0 || endPoint.getY() + deltaY < 0.0)
            return;

        initPoint = initPoint.add(deltaX, deltaY);
        endPoint = endPoint.add(deltaX, deltaY);
    }

    public void onMouseDraggedOnMap(MouseEvent e) {
        moveGrid(e);
    }

    private void moveGrid(MouseEvent e) {
        Rectangle2D rectangle2D = new Rectangle2D(initPoint.getX(), initPoint.getY(), GRID_SIZE, GRID_SIZE);
        if (rectangle2D.contains(new Point2D(e.getX(), e.getY())))
            if (clickPoint!=null) {
                calculatePoints(e);
                createLayer();
            }
        clickPoint = new Point2D(e.getX(), e.getY());
    }

    private void createGrid() {
        System.out.println(container.getWidth()/2.0 + " - " + container.getHeight()/2.0);
        double xCenter = container.getPrefWidth()/2.0;
        double yCenter = container.getPrefHeight()/2.0;
        initPoint = new Point2D(xCenter-GRID_SIZE,yCenter-GRID_SIZE);
        endPoint = new Point2D(xCenter,yCenter);
        //gtMap.createAndDrawLayer("grid",Color.RED,Color.RED);
        createLayer();
    }

    private void createLayer() {
        init = gtMap.transformSceneToWorldCoordinate(initPoint.getX(), initPoint.getY());
        end = gtMap.transformSceneToWorldCoordinate(endPoint.getX(), endPoint.getY());
        gtMap.createLayerFromCoordinates(new Point2D(init[0],init[1]),new Point2D(end[0],end[1]),"grid");
    }

    public String getArea() {
        init = gtMap.transformSceneToWorldCoordinate(initPoint.getX(), initPoint.getY());
        end = gtMap.transformSceneToWorldCoordinate(endPoint.getX(), endPoint.getY());
        try {
            wkt = gtMap.getWKTFromCoordinates(new Point2D(init[0],init[1]),new Point2D(end[0],end[1]));
        } catch (ParseException parseException) {
            parseException.printStackTrace();
            wkt = null;
        }
        return wkt;
    }
}
