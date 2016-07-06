package com.dexter.triangles;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Triangles extends ApplicationAdapter {

    private static final int MAX_DEPTH = 4;
    private static final float MOVE_SPEED = 0.3f;//per second
    private static final float SCALE_SPEED = 0.3f;

    private Camera camera;
    private ShapeRenderer renderer;

    private Triangle outerTriangle;
    private Triangle outerTriangleLeftChild;
    private Triangle outerTriangleRightChild;
    private Triangle outerTriangleTopChild;
    private Triangle baseTriangle;

    private float scaleFactor = 1.0f;
//    private int renderAreaWidth = 600;
//    private int renderAreaHeight = 600;

	@Override
	public void create () {
        camera = new OrthographicCamera(800, 600);
        renderer = new ShapeRenderer();
        renderer.setProjectionMatrix(camera.combined);

        outerTriangle = new Triangle(0f, 0f, 1f, 0f, 0.5f, 0.866f);
        calcBaseFromOuter();
//        baseTriangle = new Triangle(0.25f, 0.866f*0.5f, 0.75f, 0.866f*0.5f, 0.5f, 0f);

        calcTriangles(baseTriangle, MAX_DEPTH);
        calcOuterTriangleChildren();


	}

    private void update(float delta){

        //translate
        float dx = 0f;
        float dy = 0f;
        if(Gdx.input.isKeyPressed(Input.Keys.W)){
            dy = -MOVE_SPEED*delta;
        } else if(Gdx.input.isKeyPressed(Input.Keys.S)){
            dy = MOVE_SPEED*delta;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            dx = MOVE_SPEED*delta;
        } else if(Gdx.input.isKeyPressed(Input.Keys.D)){
            dx = -MOVE_SPEED*delta;
        }

        if(dx != 0f || dy != 0f){
            outerTriangle.move(dx, dy);
            baseTriangle.move(dx, dy);
            calcTriangles(baseTriangle, MAX_DEPTH);
            calcOuterTriangleChildren();
        }

        //scale
        float scale = 1f;
        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            scale += SCALE_SPEED*delta;
        } else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            scale -= SCALE_SPEED*delta;
        }

        if(scale != 1f){
            outerTriangle.scale(scale);
            baseTriangle.scale(scale);
            calcTriangles(baseTriangle, MAX_DEPTH);
            calcOuterTriangleChildren();
        }

//        System.out.println("outerTooSmall: " + isOuterTooSmall() + ", tooBig: " + isOuterTooBig());
        if(isOuterTooBig()){
            System.out.println("OuterTriangle is too big!");
            switchToSmallerOuter();
        }
        if(isOuterTooSmall()){
            System.out.println("OuterTriangle is too small");
            switchToBiggerOuter();
        }
//        isOuterTooBig();


    }

	@Override
	public void render () {

        update(Gdx.graphics.getDeltaTime());

//		Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_SRC_ALPHA);

//        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.WHITE);
//        renderer.rect(0f, 0f, scaleFactor, scaleFactor);

        renderTriangleOutline(outerTriangle, Color.BLACK);

        renderTriangles(baseTriangle);



        renderer.end();



        //debug only
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.WHITE);

        renderer.rect(0f, 0f, scaleFactor, scaleFactor);

        renderer.end();


	}

    private void switchToBiggerOuter(){

        Vector2 left = outerTriangle.getPointLeft();
        Vector2 right = outerTriangle.getPointRight();
        Vector2 top = outerTriangle.getPointBottom();

        Vector2 newLeft = left.cpy();
        Vector2 diff = right.cpy().sub(left).scl(1.5f);
        newLeft.sub(diff).sub(0f, top.y-left.y);

        Vector2 newRight = right.cpy();
        diff = left.cpy().sub(right).scl(1.5f);
        newRight.sub(diff).sub(0f, top.y-left.y);

        Vector2 newTop = top.cpy().add(0f, 2f*(top.y-left.y));

        outerTriangle = new Triangle(newLeft, newRight, newTop);
        calcBaseFromOuter();
        calcOuterTriangleChildren();
        calcTriangles(baseTriangle, MAX_DEPTH);

        System.out.println("bigger new outerTriangle: " + outerTriangle);
    }

    private void switchToSmallerOuter(){
        Triangle newOuter;
        if(isRectangleInTriangle(0f, 0f, 1f, 1f, outerTriangleLeftChild)){
            newOuter = outerTriangleLeftChild;
        }
        else if(isRectangleInTriangle(0f, 0f, 1f, 1f, outerTriangleRightChild)){
            newOuter = outerTriangleRightChild;
        }
        else if(isRectangleInTriangle(0f, 0f, 1f, 1f, outerTriangleTopChild)){
            newOuter = outerTriangleTopChild;
        }
        else{
            System.out.println("ERROR in switchToSmallerOuter: No child of OuterTriangle fits");
            return;
        }

        outerTriangle = newOuter;
        calcBaseFromOuter();
        calcOuterTriangleChildren();
        calcTriangles(baseTriangle, MAX_DEPTH);

        System.out.println("smaller new outerTriangle: " + outerTriangle);
    }

    private boolean isOuterTooBig(){
        if(isRectangleInTriangle(0f, 0f, 1f, 1f, outerTriangleLeftChild) ||
                isRectangleInTriangle(0f, 0f, 1f, 1f, outerTriangleRightChild) ||
                isRectangleInTriangle(0f, 0f, 1f, 1f, outerTriangleTopChild)){
            return true;
        }
        return false;
    }

    private boolean isOuterTooSmall(){
        return !isRectangleInTriangle(0f, 0f, 1f, 1f, outerTriangle);
    }

    private boolean isRectangleInTriangle(float rectX, float rectY, float rectWidth, float rectHeight, Triangle triangle){

        Vector2 left = triangle.getPointLeft();
        Vector2 right = triangle.getPointRight();
        Vector2 top = triangle.getPointBottom();

        if(Intersector.isPointInTriangle(new Vector2(rectX, rectY), top, left, right) &&
                Intersector.isPointInTriangle(new Vector2(rectX+rectWidth, rectY), top, left, right) &&
                Intersector.isPointInTriangle(new Vector2(rectX, rectY+rectHeight), top, left, right) &&
                Intersector.isPointInTriangle(new Vector2(rectX+rectWidth, rectY+rectHeight), top, left, right)){
            return true;
        }

        return false;
    }

    private void calcOuterTriangleChildren(){
        outerTriangleLeftChild = new Triangle(outerTriangle.getPointLeft(), baseTriangle.getPointBottom(),
                baseTriangle.getPointLeft());
        outerTriangleRightChild = new Triangle(baseTriangle.getPointBottom(), outerTriangle.getPointRight(),
                baseTriangle.getPointRight());
        outerTriangleTopChild = new Triangle(baseTriangle.getPointLeft(), baseTriangle.getPointRight(),
                outerTriangle.getPointBottom());
    }

    private void calcBaseFromOuter(){
        Vector2 left = outerTriangle.getPointLeft();
        Vector2 right = outerTriangle.getPointRight();
        Vector2 top = outerTriangle.getPointBottom();
        baseTriangle = new Triangle(outerTriangle, left.cpy().add(top).scl(0.5f),
                right.cpy().add(top).scl(0.5f), left.cpy().add(right).scl(0.5f));
    }

    private void calcTriangles(Triangle t, int steps){
        if(steps == 0)
            return;
        t.calculateSurroundingTriangles();

        Triangle left = t.getTriangleLeft();
        Triangle right = t.getTriangleRight();
        Triangle other = t.getTriangleUp();

        calcTriangles(left, steps-1);
        calcTriangles(right, steps-1);
        calcTriangles(other, steps-1);
    }

    private void renderTriangleOutline(Triangle t, Color c){
        Vector2 pl = t.getPointLeft().cpy().scl(scaleFactor);
        Vector2 pr = t.getPointRight().cpy().scl(scaleFactor);
        Vector2 pb = t.getPointBottom().cpy().scl(scaleFactor);

        renderer.setColor(c);
        renderer.line(pl, pr);
        renderer.line(pl, pb);
        renderer.line(pr, pb);
    }

    private void renderTriangles(Triangle t){

        float maxDistance = outerTriangle.getPointLeft().dst(outerTriangle.getPointRight());

        float red = 1f - outerTriangle.getPointLeft().dst(t.getPointCenter()) / maxDistance;
        float green = 1f - outerTriangle.getPointRight().dst(t.getPointCenter()) / maxDistance;
        float blue = 1f - outerTriangle.getPointBottom().dst(t.getPointCenter()) / maxDistance;
        Color c = new Color(red, green, blue, 1.0f);
        renderTriangle(t, c);
//        renderTriangle(t, Color.RED);

        if(t.hasChildren()){
//            renderTriangles(t.getTriangleLeft(), steps-1, COLOR_TRIANGLE_LEFT);
//            renderTriangles(t.getTriangleRight(), steps-1, COLOR_TRIANGLE_RIGHT);
//            renderTriangles(t.getTriangleUp(), steps-1, COLOR_TRIANGLE_OTHER);
//            renderTriangles(t.getTriangleMiddle(), steps-1, COLOR_TRIANGLE_MIDDLE);

            renderTriangles(t.getTriangleLeft());
            renderTriangles(t.getTriangleRight());
            renderTriangles(t.getTriangleUp());

        }
    }

    private void renderTriangle(Triangle t, Color color){
        Vector2 pl = t.getPointLeft().cpy().scl(scaleFactor);
        Vector2 pr = t.getPointRight().cpy().scl(scaleFactor);
        Vector2 pb = t.getPointBottom().cpy().scl(scaleFactor);

        renderer.setColor(color);
        renderer.triangle(pl.x, pl.y, pr.x, pr.y, pb.x, pb.y);

//        Color colorOutline = color.cpy();
//        colorOutline.a = 1.0f;
//        renderTriangleOutline(t, colorOutline);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        camera.viewportWidth = width;
        camera.viewportHeight = height;
//        camera.viewportWidth = 1.0f;
//        camera.viewportHeight = 1.0f;
//        camera.position.set(width/2.0f, height/2.0f, 0);
        camera.update();
        renderer.setProjectionMatrix(camera.combined);

        scaleFactor = Math.min(width, height);

        //TODO: DEBUG only, remove later!
        scaleFactor *= 0.25f;
//        camera.position.set(0f, 0f, 0f);
        camera.update();
    }
}
