package com.dexter.triangles;

import com.badlogic.gdx.math.Vector2;

public class Triangle {

    private Vector2 pointLeft;
    private Vector2 pointRight;
    private Vector2 pointBottom;
    private Vector2 pointCenter;

    private Triangle triangleParent;
    private Triangle triangleLeft;
    private Triangle triangleRight;
    private Triangle triangleUp;

    public Triangle(Triangle triangleParent, Vector2 pointLeft, Vector2 pointRight, Vector2 pointBottom){
        this(pointLeft, pointRight, pointBottom);
        this.triangleParent = triangleParent;
    }

    public Triangle(float leftX, float leftY, float rightX, float rightY, float bottomX, float bottomY){
        this(new Vector2(leftX, leftY), new Vector2(rightX, rightY), new Vector2(bottomX, bottomY));
    }

    public Triangle(Vector2 pointLeft, Vector2 pointRight, Vector2 pointBottom){
        this.pointLeft = pointLeft;
        this.pointRight = pointRight;
        this.pointBottom = pointBottom;
        this.pointCenter = new Vector2( (pointLeft.x+pointRight.x+pointBottom.x) / 3f,
                (pointLeft.y+pointRight.y+pointBottom.y) / 3f);
    }

    public void calculateSurroundingTriangles(){

        //left
        Vector2 v = pointLeft.cpy().add(pointBottom).scl(0.5f);
        Vector2 diff = pointRight.cpy().sub(v);
        Vector2 v2 = v.cpy().sub(diff);
        triangleLeft = new Triangle(this, pointLeft.cpy().add(v2).scl(0.5f),
                v, pointBottom.cpy().add(v2).scl(0.5f));

        //right
        v = pointBottom.cpy().add(pointRight).scl(0.5f);
        diff = pointLeft.cpy().sub(v);
        v2 = v.cpy().sub(diff);
        triangleRight = new Triangle(this, v,
                pointRight.cpy().add(v2).scl(0.5f), pointBottom.cpy().add(v2).scl(0.5f));

        //up
        v = pointLeft.cpy().add(pointRight).scl(0.5f);
        diff = pointBottom.cpy().sub(v);
        v2 = v.cpy().sub(diff);
        triangleUp = new Triangle(this, pointLeft.cpy().add(v2).scl(0.5f),
                pointRight.cpy().add(v2).scl(0.5f), v);

    }

    public void move(float x, float y){
        move(new Vector2(x, y));
    }

    public void move(Vector2 v){
        pointLeft.add(v);
        pointRight.add(v);
        pointBottom.add(v);
        pointCenter.add(v);
    }

    public void scale(float factor){
        pointLeft.scl(factor);
        pointRight.scl(factor);
        pointBottom.scl(factor);
        pointCenter.scl(factor);
    }

    public boolean hasParent(){
        if(triangleParent != null)
            return true;
        else
            return false;
    }

    public boolean hasChildren(){
        if(triangleLeft != null && triangleRight != null && triangleUp != null)
            return true;
        else
            return false;
    }

    public Vector2 getPointLeft() {
        return pointLeft;
    }

    public Vector2 getPointRight() {
        return pointRight;
    }

    public Vector2 getPointBottom() {
        return pointBottom;
    }

    public Vector2 getPointCenter() {
        return pointCenter;
    }

    public Triangle getTriangleParent() {
        return triangleParent;
    }

    public Triangle getTriangleLeft() {
        return triangleLeft;
    }

    public Triangle getTriangleRight() {
        return triangleRight;
    }

    public Triangle getTriangleUp() {
        return triangleUp;
    }

    @Override
    public String toString() {
        return "pointLeft: " + pointLeft + ", pointRight: " + pointRight + ", pointBottom: " + pointBottom;
    }

}
