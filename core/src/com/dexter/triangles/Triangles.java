package com.dexter.triangles;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;

public class Triangles extends ApplicationAdapter {

    private static final int MAX_DEPTH = 4;
    private static final float MIN_LENGTH = 0.01f;
    private static final float MOVE_SPEED = 0.3f;//per second
    private static final float SCALE_SPEED = 0.3f;

    private static final float TRIANGLE_LENGTH = 1.0f;
    private static final float TRIANGLE_HEIGHT = 0.866f;

    private Camera camera;
    private ShapeRenderer renderer;

    private Triangle outerTriangle;
    private Triangle outerTriangleLeftChild;
    private Triangle outerTriangleRightChild;
    private Triangle outerTriangleTopChild;
    private Triangle baseTriangle;

    private float scaleFactor = 1.0f;
    private float aspectRatio = 1.0f;
    private float screenWidth = 100f;
    private float screenHeight = 100f;
    private float areaWidth = 1.0f;
    private float areaHeight = 1.0f;
//    private int renderAreaWidth = 600;
//    private int renderAreaHeight = 600;

	@Override
	public void create () {
        camera = new OrthographicCamera(800, 600);
        renderer = new ShapeRenderer();
        renderer.setProjectionMatrix(camera.combined);

        outerTriangle = new Triangle(0f, 0f, 1f, 0f, 0.5f, 0.866f);
//        outerTriangle = new Triangle(-5f, -5f, 5f, -5f, 0.0f, 10*0.866f);
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

            float f = 0.50f * areaWidth;
            scale -= 1f;
            scale *= -1f;
            outerTriangle.move(scale*f, scale*f);
            baseTriangle.move(scale*f, scale*f);
            calcTriangles(baseTriangle, MAX_DEPTH);
            calcOuterTriangleChildren();
        }

        if(isOuterTooBig()){
            switchToSmallerOuter();
        }
        if(isOuterTooSmall()){
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

        renderer.rect(50f, 50f, screenWidth-100, screenHeight-100);

        renderer.end();


	}

    private void switchToBiggerOuter(){

        Vector2 left = outerTriangle.getPointLeft();
        Vector2 right = outerTriangle.getPointRight();
        Vector2 top = outerTriangle.getPointBottom();

        Vector2 newLeft;
        Vector2 newRight;
        Vector2 newTop;

        if(!Intersector.isPointInTriangle(new Vector2(0f, areaHeight), top, left, right)){
            //expand to left
            newLeft = left.cpy().sub(right.cpy().sub(left));
            newRight = right.cpy();
            newTop = left.cpy().add(0f, 2*(top.y-left.y));
        } else if(!Intersector.isPointInTriangle(new Vector2(areaWidth, areaHeight), top, left, right)){
            //expand to right
            newLeft = left.cpy();
            newRight = right.cpy().sub(left.cpy().sub(right));
            newTop = right.cpy().add(0f, 2*(top.y-left.y));
        } else if(!Intersector.isPointInTriangle(new Vector2(0f, 0f), top, left, right) ||
                !Intersector.isPointInTriangle(new Vector2(areaWidth, 0f), top, left, right)){
            //expand to bottom
            newLeft = left.cpy().sub(0.5f*(right.x-left.x), top.y-left.y);
            newRight = right.cpy().add(0.5f*(right.x-left.x), left.y-top.y);
            newTop = top.cpy();
        } else{
            System.out.println("ERROR in switchToBiggerOuter: every point of rectangle is inside triangle");
            return;
        }

//        Vector2 left = outerTriangle.getPointLeft();
//        Vector2 right = outerTriangle.getPointRight();
//        Vector2 top = outerTriangle.getPointBottom();
//
//        Vector2 newLeft = left.cpy();
//        Vector2 diff = right.cpy().sub(left).scl(1.5f);
//        newLeft.sub(diff).sub(0f, top.y-left.y);
//
//        Vector2 newRight = right.cpy();
//        diff = left.cpy().sub(right).scl(1.5f);
//        newRight.sub(diff).sub(0f, top.y-left.y);
//
//        Vector2 newTop = top.cpy().add(0f, 2f*(top.y-left.y));

        outerTriangle = new Triangle(newLeft, newRight, newTop);
        calcBaseFromOuter();
        calcOuterTriangleChildren();
        calcTriangles(baseTriangle, MAX_DEPTH);

    }

    private void switchToSmallerOuter(){
        Triangle newOuter;
        if(isRectangleInTriangle(0f, 0f, areaWidth, areaHeight, outerTriangleLeftChild)){
            newOuter = outerTriangleLeftChild;
        }
        else if(isRectangleInTriangle(0f, 0f, areaWidth, areaHeight, outerTriangleRightChild)){
            newOuter = outerTriangleRightChild;
        }
        else if(isRectangleInTriangle(0f, 0f, areaWidth, areaHeight, outerTriangleTopChild)){
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

    }

    private boolean isOuterTooBig(){
        if(isRectangleInTriangle(0f, 0f, areaWidth, areaHeight, outerTriangleLeftChild) ||
                isRectangleInTriangle(0f, 0f, areaWidth, areaHeight, outerTriangleRightChild) ||
                isRectangleInTriangle(0f, 0f, areaWidth, areaHeight, outerTriangleTopChild)){
            return true;
        }
        return false;
    }

    private boolean isOuterTooSmall(){
        return !isRectangleInTriangle(0f, 0f, areaWidth, areaHeight, outerTriangle);
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
//        if(steps == 0)
//            return;
        if(t.getPointLeft().dst(t.getPointRight()) < 2*MIN_LENGTH)
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
//        renderTriangle(t, c);
        renderTriangle(t, Color.RED);

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
        camera.position.set(width/2.0f, height/2.0f, 0);
        camera.update();
        renderer.setProjectionMatrix(camera.combined);

        scaleFactor = Math.min(width, height);
        aspectRatio = (float) width / height;
        if(aspectRatio >= 1f){
            areaWidth = aspectRatio;
            areaHeight = 1.0f;
        } else{
            areaWidth = 1.0f;
            areaHeight = 1/aspectRatio;
        }
        screenWidth = width;
        screenHeight = height;

        //TODO: DEBUG only, remove later!
//        scaleFactor *= 0.25f;
//        camera.update();
    }
}
