package com.dexter.triangles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;

public class TriangleController {

    private TriangleModel model;

    public TriangleController(TriangleModel model){
        this.model = model;
        updateTriangles();
    }

    public void update(float delta){

        boolean updateNeeded = false;

        //translate
        float dx = 0f;
        float dy = 0f;
        if(Gdx.input.isKeyPressed(Input.Keys.W)){
            dy = -model.getMoveSpeed()*delta;
        } else if(Gdx.input.isKeyPressed(Input.Keys.S)){
            dy = model.getMoveSpeed()*delta;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            dx = model.getMoveSpeed()*delta;
        } else if(Gdx.input.isKeyPressed(Input.Keys.D)){
            dx = -model.getMoveSpeed()*delta;
        }

        if(dx != 0f || dy != 0f){
            model.getOuterTriangle().move(dx, dy);
            model.getBaseTriangle().move(dx, dy);
            updateNeeded = true;
        }

        //scale
        float scale = 1f;
        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            scale += model.getScaleSpeed()*delta;
        } else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            scale -= model.getScaleSpeed()*delta;
        }

        if(scale != 1f){
            model.getOuterTriangle().scale(scale);
            model.getBaseTriangle().scale(scale);

            float f = 0.50f * model.getAreaWidth();
            scale -= 1f;
            scale *= -1f;
            model.getOuterTriangle().move(scale*f, scale*f);
            model.getBaseTriangle().move(scale*f, scale*f);
            updateNeeded = true;
        }

        if(updateNeeded){
            updateTriangles();
        }

        if(isOuterTooBig()){
            switchToSmallerOuter();
        }
        if(isOuterTooSmall()){
            switchToBiggerOuter();
        }

    }

    private void updateTriangles(){
        model.resetTriangleCounter();
        calcBaseFromOuter();
        calcOuterTriangleChildren();
        calcTriangles(model.getBaseTriangle());
    }

    private void switchToBiggerOuter(){

        Vector2 left = model.getOuterTriangle().getPointLeft();
        Vector2 right = model.getOuterTriangle().getPointRight();
        Vector2 top = model.getOuterTriangle().getPointBottom();

        Vector2 newLeft;
        Vector2 newRight;
        Vector2 newTop;

        if(!Intersector.isPointInTriangle(new Vector2(0f, model.getAreaHeight()), top, left, right)){
            //expand to left
            newLeft = left.cpy().sub(right.cpy().sub(left));
            newRight = right.cpy();
            newTop = left.cpy().add(0f, 2*(top.y-left.y));
        } else if(!Intersector.isPointInTriangle(new Vector2(model.getAreaWidth(), model.getAreaHeight()), top, left, right)){
            //expand to right
            newLeft = left.cpy();
            newRight = right.cpy().sub(left.cpy().sub(right));
            newTop = right.cpy().add(0f, 2*(top.y-left.y));
        } else if(!Intersector.isPointInTriangle(new Vector2(0f, 0f), top, left, right) ||
                !Intersector.isPointInTriangle(new Vector2(model.getAreaWidth(), 0f), top, left, right)){
            //expand to bottom
            newLeft = left.cpy().sub(0.5f*(right.x-left.x), top.y-left.y);
            newRight = right.cpy().add(0.5f*(right.x-left.x), left.y-top.y);
            newTop = top.cpy();
        } else{
            System.out.println("ERROR in switchToBiggerOuter: every point of rectangle is inside triangle");
            return;
        }

        model.setOuterTriangle(new Triangle(newLeft, newRight, newTop));
        updateTriangles();

    }

    private void switchToSmallerOuter(){
        Triangle newOuter;
        if(isRectangleInTriangle(0f, 0f, model.getAreaWidth(), model.getAreaHeight(), model.getOuterTriangleLeftChild())){
            newOuter = model.getOuterTriangleLeftChild();
        }
        else if(isRectangleInTriangle(0f, 0f, model.getAreaWidth(), model.getAreaHeight(), model.getOuterTriangleRightChild())){
            newOuter = model.getOuterTriangleRightChild();
        }
        else if(isRectangleInTriangle(0f, 0f, model.getAreaWidth(), model.getAreaHeight(), model.getOuterTriangleTopChild())){
            newOuter = model.getOuterTriangleTopChild();
        }
        else{
            System.out.println("ERROR in switchToSmallerOuter: No child of OuterTriangle fits");
            return;
        }

        model.setOuterTriangle(newOuter);
        updateTriangles();

    }

    private boolean isOuterTooBig(){
        return isRectangleInTriangle(0f, 0f, model.getAreaWidth(), model.getAreaHeight(), model.getOuterTriangleLeftChild()) ||
                isRectangleInTriangle(0f, 0f, model.getAreaWidth(), model.getAreaHeight(), model.getOuterTriangleRightChild()) ||
                isRectangleInTriangle(0f, 0f, model.getAreaWidth(), model.getAreaHeight(), model.getOuterTriangleTopChild());
    }

    private boolean isOuterTooSmall(){
        return !isRectangleInTriangle(0f, 0f, model.getAreaWidth(), model.getAreaHeight(), model.getOuterTriangle());
    }

    private boolean isRectangleInTriangle(float rectX, float rectY, float rectWidth, float rectHeight, Triangle triangle){

        Vector2 left = triangle.getPointLeft();
        Vector2 right = triangle.getPointRight();
        Vector2 top = triangle.getPointBottom();

        return Intersector.isPointInTriangle(new Vector2(rectX, rectY), top, left, right) &&
                Intersector.isPointInTriangle(new Vector2(rectX + rectWidth, rectY), top, left, right) &&
                Intersector.isPointInTriangle(new Vector2(rectX, rectY + rectHeight), top, left, right) &&
                Intersector.isPointInTriangle(new Vector2(rectX + rectWidth, rectY + rectHeight), top, left, right);

    }

    private void calcOuterTriangleChildren(){
        model.setOuterTriangleLeftChild(new Triangle(model.getOuterTriangle().getPointLeft(), model.getBaseTriangle().getPointBottom(),
                model.getBaseTriangle().getPointLeft()));
        model.setOuterTriangleRightChild(new Triangle(model.getBaseTriangle().getPointBottom(), model.getOuterTriangle().getPointRight(),
                model.getBaseTriangle().getPointRight()));
        model.setOuterTriangleTopChild(new Triangle(model.getBaseTriangle().getPointLeft(), model.getBaseTriangle().getPointRight(),
                model.getOuterTriangle().getPointBottom()));
    }

    private void calcBaseFromOuter(){
        Vector2 left = model.getOuterTriangle().getPointLeft();
        Vector2 right = model.getOuterTriangle().getPointRight();
        Vector2 top = model.getOuterTriangle().getPointBottom();
        model.setBaseTriangle(new Triangle(model.getOuterTriangle(), left.cpy().add(top).scl(0.5f),
                right.cpy().add(top).scl(0.5f), left.cpy().add(right).scl(0.5f)));
    }

    private void calcTriangles(Triangle t){
        model.incrementTriangleCounter();
        if(t.getPointLeft().dst(t.getPointRight()) < 2*model.getMinLength())
            return;
        t.calculateSurroundingTriangles();

        Triangle left = t.getTriangleLeft();
        Triangle right = t.getTriangleRight();
        Triangle other = t.getTriangleUp();

        calcTriangles(left);
        calcTriangles(right);
        calcTriangles(other);
    }




}
