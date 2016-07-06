package com.dexter.triangles;

public class TriangleModel {

    private float minLength = 0.01f;
    private float moveSpeed = 0.3f;//per second
    private float scaleSpeed = 0.3f;

    private Triangle outerTriangle;
    private Triangle outerTriangleLeftChild;
    private Triangle outerTriangleRightChild;
    private Triangle outerTriangleTopChild;
    private Triangle baseTriangle;

    private float scaleFactor = 1.0f;
    private float areaWidth = 1.0f;
    private float areaHeight = 1.0f;

    private int triangleCounter = 0;
    private float updateTime = 0f;
    private float renderTime = 0f;

    public TriangleModel(){
        outerTriangle = new Triangle(0f, 0f, 1f, 0f, 0.5f, 0.866f);
    }

    public void setMinLength(float minLength) {
        this.minLength = minLength;
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public void setScaleSpeed(float scaleSpeed) {
        this.scaleSpeed = scaleSpeed;
    }

    public void setOuterTriangle(Triangle outerTriangle) {
        this.outerTriangle = outerTriangle;
    }

    public void setOuterTriangleLeftChild(Triangle outerTriangleLeftChild) {
        this.outerTriangleLeftChild = outerTriangleLeftChild;
    }

    public void setOuterTriangleRightChild(Triangle outerTriangleRightChild) {
        this.outerTriangleRightChild = outerTriangleRightChild;
    }

    public void setOuterTriangleTopChild(Triangle outerTriangleTopChild) {
        this.outerTriangleTopChild = outerTriangleTopChild;
    }

    public void setBaseTriangle(Triangle baseTriangle) {
        this.baseTriangle = baseTriangle;
    }

    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public void setAreaWidth(float areaWidth) {
        this.areaWidth = areaWidth;
    }

    public void setAreaHeight(float areaHeight) {
        this.areaHeight = areaHeight;
    }

    public void incrementTriangleCounter(){
        this.triangleCounter++;
    }

    public void resetTriangleCounter() {
        this.triangleCounter = 0;
    }

    public void setUpdateTime(float updateTime) {
        this.updateTime = updateTime;
    }

    public void setRenderTime(float renderTime) {
        this.renderTime = renderTime;
    }

    public float getMinLength() {
        return minLength;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public float getScaleSpeed() {
        return scaleSpeed;
    }

    public Triangle getOuterTriangle() {
        return outerTriangle;
    }

    public Triangle getOuterTriangleLeftChild() {
        return outerTriangleLeftChild;
    }

    public Triangle getOuterTriangleRightChild() {
        return outerTriangleRightChild;
    }

    public Triangle getOuterTriangleTopChild() {
        return outerTriangleTopChild;
    }

    public Triangle getBaseTriangle() {
        return baseTriangle;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public float getAreaWidth() {
        return areaWidth;
    }

    public float getAreaHeight() {
        return areaHeight;
    }

    public int getTriangleCounter() {
        return triangleCounter;
    }

    public float getUpdateTime() {
        return updateTime;
    }

    public float getRenderTime() {
        return renderTime;
    }
}
