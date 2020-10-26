package controller.processing;

import gui.GTMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.locationtech.jts.io.ParseException;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class PreviewAreaSelectionController implements Initializable {
    @FXML
    private Button generatePreview;
    @FXML
    private AnchorPane map;
    private GTMap gtMap;
    private double[] end;
    private double[] init;
    private Point2D initPoint = new Point2D(0,0);
    private Point2D endPoint = new Point2D(200,200);
    private Point2D clickPoint;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gtMap = new GTMap(822, 616, false);
        map.getChildren().add(gtMap);
        AnchorPane.setRightAnchor(gtMap.getParent(),0.0);
        AnchorPane.setLeftAnchor(gtMap.getParent(),0.0);
        AnchorPane.setTopAnchor(gtMap.getParent(),0.0);
        AnchorPane.setBottomAnchor(gtMap.getParent(),0.0);
        gtMap.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClickedOnMap);
        gtMap.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onMouseDraggedOnMap);
        gtMap.addEventHandler(MouseEvent.MOUSE_PRESSED, this::onMousePressedOnMap);
        gtMap.addEventHandler(MouseEvent.MOUSE_RELEASED, this::onMouseReleasedOnMap);
        generatePreview.setOnAction(e->{
            Stage window = (Stage)gtMap.getScene().getWindow();
            window.hide();
        });
    }

    public void setAreaOfWork(String area) throws ParseException {
        gtMap.createFeatureFromWKT(area,"areaOfWork","areaOfWork");
        gtMap.createAndDrawLayer("areaOfWork", Color.BLACK,null);
        gtMap.focusOnLayer("areaOfWork");

        createGrid();
    }

    public void onMouseClickedOnMap(MouseEvent e) {
        Rectangle2D rectangle2D = new Rectangle2D(initPoint.getX(), initPoint.getY(), endPoint.getX(), endPoint.getY());
        if (rectangle2D.contains(new Point2D(e.getSceneX(),e.getSceneY())) && clickPoint == null)
            clickPoint = new Point2D(e.getSceneX(),e.getSceneY());

    }

    public void onMouseReleasedOnMap(MouseEvent e) {
        System.out.println("R");
        clickPoint = null;
    }

    public void onMousePressedOnMap(MouseEvent e) {
        System.out.println(initPoint + " -- " + endPoint);
        System.out.println(clickPoint + " __ ");
        Rectangle2D rectangle2D = new Rectangle2D(initPoint.getX(), initPoint.getY(), endPoint.getX(), endPoint.getY());
        if (rectangle2D.contains(new Point2D(e.getSceneX(),e.getSceneY())))
            if (clickPoint!=null) {
                calculatePoints(e);
                createLayer();
            }
            clickPoint = new Point2D(e.getSceneX(),e.getSceneY());
    }

    private void calculatePoints(MouseEvent e) {
        double deltaX = e.getSceneX() - clickPoint.getX();
        double deltaY = e.getSceneY() - clickPoint.getY();

        if (initPoint.getX() + deltaX > 822 || initPoint.getY() + deltaY > 616)
            return;
        if (endPoint.getX() + deltaX > 822 || endPoint.getY() + deltaY > 616)
            return;
        if (initPoint.getX() + deltaX < 0.0 || initPoint.getY() + deltaY < 0.0)
            return;
        if (endPoint.getX() + deltaX < 0.0 || endPoint.getY() + deltaY < 0.0)
            return;

        initPoint = initPoint.add(deltaX, deltaY);
        endPoint = endPoint.add(deltaX, deltaY);
    }

    public void onMouseDraggedOnMap(MouseEvent e) {
        Rectangle2D rectangle2D = new Rectangle2D(initPoint.getX(), initPoint.getY(), endPoint.getX(), endPoint.getY());
        if (rectangle2D.contains(new Point2D(e.getSceneX(),e.getSceneY())))
            if (clickPoint!=null) {
                calculatePoints(e);
                createLayer();
            }
            clickPoint = new Point2D(e.getSceneX(),e.getSceneY());
    }

    private void createGrid() {
        initPoint = new Point2D(0,0);
        endPoint = new Point2D(200,200);
        //gtMap.createAndDrawLayer("grid",Color.RED,Color.RED);
        createLayer();
    }

    private void createLayer() {
        init = gtMap.transformSceneToWorldCoordinate(initPoint.getX(), initPoint.getY());
        end = gtMap.transformSceneToWorldCoordinate(endPoint.getX(), endPoint.getY());
        gtMap.createLayerFromCoordinates(new Point2D(init[0],init[1]),new Point2D(end[0],end[1]),"grid");
    }

    public String getArea() throws ParseException {
        init = gtMap.transformSceneToWorldCoordinate(initPoint.getX(), initPoint.getY());
        end = gtMap.transformSceneToWorldCoordinate(endPoint.getX(), endPoint.getY());
        return gtMap.getWKTFromCoordinates(new Point2D(init[0],init[1]),new Point2D(end[0],end[1]));
    }
}
