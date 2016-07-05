package com.dexter.triangles;

import java.util.logging.XMLFormatter;

public class Triangle {

    private Point pointLeft;
    private Point pointRight;
    private Point pointOther;
    private Point pointCenter;

    private TrianglePosition trianglePosition;
    private Orientation orientation;
    private Triangle triangleParent;
    private Triangle triangleLeft;
    private Triangle triangleRight;
    private Triangle triangleMiddle;
    private Triangle triangleOther;

    public Triangle(TrianglePosition trianglePosition, Triangle triangleParent, Point pointLeft, Point pointRight, Point pointOther){
        this(pointLeft, pointRight, pointOther);
        this.triangleParent = triangleParent;
        this.trianglePosition = trianglePosition;
    }

    public Triangle(Point pointLeft, Point pointRight, Point pointOther){
        this.pointLeft = pointLeft;
        this.pointRight = pointRight;
        this.pointOther = pointOther;
        pointCenter = new Point( (pointLeft.getX()+pointRight.getX()+pointOther.getX()) / 3f,
                (pointLeft.getY()+pointRight.getY()+pointOther.getY()) / 3f);
    }

    public Triangle(float leftX, float leftY, float rightX, float rightY, float otherX, float otherY){
        pointLeft = new Point(leftX, leftY);
        pointRight = new Point(rightX, rightY);
        pointOther = new Point(otherX, otherY);
        pointCenter = new Point( (pointLeft.getX()+pointRight.getX()+pointOther.getX()) / 3f,
                (pointLeft.getY()+pointRight.getY()+pointOther.getY()) / 3f);
    }

    public void calculateInnerTriangles(){

        Point p1 = new Point(pointLeft);
        Point p2 = new Point(pointLeft.getAverageTo(pointRight));
        Point p3 = new Point(pointRight);
        Point p4 = new Point(pointRight.getAverageTo(pointOther));
        Point p5 = new Point(pointOther);
        Point p6 = new Point(pointOther.getAverageTo(pointLeft));

        triangleLeft = new Triangle(TrianglePosition.LEFT, this, p1, p2, p6);
        triangleRight = new Triangle(TrianglePosition.RIGHT, this, p2, p3, p4);
        triangleOther = new Triangle(TrianglePosition.OTHER, this, p6, p4, p5);
        triangleMiddle = new Triangle(TrianglePosition.MIDDLE, this, p6, p4, p2);

        if(orientation == Orientation.UP){
            triangleLeft.setOrientation(Orientation.UP);
            triangleRight.setOrientation(Orientation.UP);
            triangleOther.setOrientation(Orientation.UP);
            triangleMiddle.setOrientation(Orientation.DOWN);
        } else{
            triangleLeft.setOrientation(Orientation.DOWN);
            triangleRight.setOrientation(Orientation.DOWN);
            triangleOther.setOrientation(Orientation.DOWN);
            triangleMiddle.setOrientation(Orientation.UP);
        }

    }

    public boolean hasParent(){
        if(triangleParent != null)
            return true;
        else
            return false;
    }

    public boolean hasChildren(){
        if(triangleLeft != null && triangleRight != null && triangleOther != null && triangleMiddle != null)
            return true;
        else
            return false;
    }

    public Point getPointCenter() {
        return pointCenter;
    }

    public TrianglePosition getTrianglePosition() {
        return trianglePosition;
    }

    public void setTrianglePosition(TrianglePosition trianglePosition) {
        this.trianglePosition = trianglePosition;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public Triangle getTriangleMiddle() {
        return triangleMiddle;
    }

    public void setTriangleMiddle(Triangle triangleMiddle) {
        this.triangleMiddle = triangleMiddle;
    }

    public Triangle getTriangleParent() {
        return triangleParent;
    }

    public void setTriangleParent(Triangle triangleParent) {
        this.triangleParent = triangleParent;
    }

    public Triangle getTriangleLeft() {
        return triangleLeft;
    }

    public void setTriangleLeft(Triangle triangleLeft) {
        this.triangleLeft = triangleLeft;
    }

    public Triangle getTriangleRight() {
        return triangleRight;
    }

    public void setTriangleRight(Triangle triangleRight) {
        this.triangleRight = triangleRight;
    }

    public Triangle getTriangleOther() {
        return triangleOther;
    }

    public void setTriangleOther(Triangle triangleOther) {
        this.triangleOther = triangleOther;
    }

    public Point getPointLeft() {
        return pointLeft;
    }

    public void setPointLeft(Point pointLeft) {
        this.pointLeft = pointLeft;
    }

    public Point getPointRight() {
        return pointRight;
    }

    public void setPointRight(Point pointRight) {
        this.pointRight = pointRight;
    }

    public Point getPointOther() {
        return pointOther;
    }

    public void setPointOther(Point pointOther) {
        this.pointOther = pointOther;
    }

    @Override
    public String toString() {
        return "pointLeft: " + pointLeft + ", pointRight: " + pointRight + ", pointOther: " + pointOther;
    }

}
