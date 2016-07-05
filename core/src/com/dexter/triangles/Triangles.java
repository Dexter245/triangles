package com.dexter.triangles;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Triangles extends ApplicationAdapter {

    private static final int MAX_DEPTH = 7;

    private Camera camera;
    private ShapeRenderer renderer;

    private Triangle outerTriangle;
    private Triangle baseTriangle;

    private float scaleFactor = 1.0f;

	@Override
	public void create () {
        camera = new OrthographicCamera(800, 600);
        renderer = new ShapeRenderer();
        renderer.setProjectionMatrix(camera.combined);

        outerTriangle = new Triangle(0f, 0f, 1f, 0f, 0.5f, 0.866f);
        baseTriangle = new Triangle(0.25f, 0.866f*0.5f, 0.75f, 0.866f*0.5f, 0.5f, 0f);

//        baseTriangle.calculateSurroundingTriangles();
        calcTriangles(baseTriangle, MAX_DEPTH);
//        calcTrianglesIterative(baseTriangle, MAX_DEPTH);


//        Triangle inner1 = calcInnerTriangle(baseTriangle);
//        System.out.println("base: " + baseTriangle);
//        System.out.println("inner1: " + inner1);



	}

	@Override
	public void render () {
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

        renderTriangleOutline(outerTriangle, Color.WHITE);

        renderTriangles(baseTriangle);


        renderer.end();


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

        float red = 1f - outerTriangle.getPointLeft().dst(t.getPointCenter());
        float green = 1f - outerTriangle.getPointRight().dst(t.getPointCenter());
        float blue = 1f - outerTriangle.getPointBottom().dst(t.getPointCenter());
        Color c = new Color(red, green, blue, 1.0f);
        renderTriangle(t, c);

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
    }
}
