package com.dexter.triangles;

import com.badlogic.gdx.math.Vector3;

public class Point {

    private float x;
    private float y;

    public Point(){

    }

    public Point(Point p){
        this.x = p.x;
        this.y = p.y;
    }

    public Point(float x, float y){
        this.x = x;
        this.y = y;
    }

    public Vector3 getScaledVector3(float scaleFactor){
        Vector3 v = new Vector3(x*scaleFactor, y*scaleFactor, 0f);
        return v;
    }

    public Point getAverageTo(Point other){
        Point newPoint = new Point();
        newPoint.setX( (this.x + other.getX()) / 2f);
        newPoint.setY( (this.y + other.getY()) / 2f);
        return newPoint;
    }

    public float getDistanceTo(Point other){
        float dx = x-other.x;
        float dy = y-other.y;
        float distance = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        return distance;
    }

    public void set(float x, float y){
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
