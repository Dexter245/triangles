package com.dexter.triangles;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;

public class Triangles extends ApplicationAdapter {

    private static final int MAX_DEPTH = 7;
//    private static final Color COLOR_TRIANGLE_LEFT = Color.RED;
//    private static final Color COLOR_TRIANGLE_RIGHT = Color.GREEN;
//    private static final Color COLOR_TRIANGLE_OTHER = Color.BLUE;
//    private static final Color COLOR_TRIANGLE_MIDDLE = Color.BLACK;

    private static final float TRIANGLE_COLOR_ALPHA = 1.0f;
    private static final Color COLOR_TRIANGLE_LEFT = new Color(1f, 0f, 0f, TRIANGLE_COLOR_ALPHA);
    private static final Color COLOR_TRIANGLE_RIGHT = new Color(0f, 1f, 0f, TRIANGLE_COLOR_ALPHA);
    private static final Color COLOR_TRIANGLE_OTHER = new Color(0f, 0f, 1f, TRIANGLE_COLOR_ALPHA);
    private static final Color COLOR_TRIANGLE_MIDDLE = new Color(1f, 0f, 0f, TRIANGLE_COLOR_ALPHA);

//    private static final Color COLOR_TRIANGLE_LEFT = new Color(1f, 1f, 1f, TRIANGLE_COLOR_ALPHA);
//    private static final Color COLOR_TRIANGLE_RIGHT = new Color(1f, 1f, 1f, TRIANGLE_COLOR_ALPHA);
//    private static final Color COLOR_TRIANGLE_OTHER = new Color(1f, 1f, 1f, TRIANGLE_COLOR_ALPHA);
//    private static final Color COLOR_TRIANGLE_MIDDLE = new Color(1f, 1f, 1f, TRIANGLE_COLOR_ALPHA);

    private Camera camera;
    private ShapeRenderer renderer;

    private Triangle baseTriangle;
    private List<List<Triangle>> triangles = new ArrayList<>();

    private float scaleFactor = 1.0f;

	@Override
	public void create () {
        camera = new OrthographicCamera(800, 600);
        renderer = new ShapeRenderer();
        renderer.setProjectionMatrix(camera.combined);

        baseTriangle = new Triangle(0f, 0f, 1f, 0f, 0.5f, 0.866f);
        baseTriangle.setOrientation(Orientation.UP);
        baseTriangle.setTrianglePosition(TrianglePosition.MIDDLE);
//        baseTriangle = new Triangle(0f, 0f, 1f, 0f, 1.0f, 1f);

//        baseTriangle.calculateInnerTriangles();
//        calcTriangles(baseTriangle, MAX_DEPTH);
        calcTrianglesIterative(baseTriangle, MAX_DEPTH);


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
        renderer.setColor(Color.BLACK);

        COLOR_TRIANGLE_LEFT.a = TRIANGLE_COLOR_ALPHA;
        COLOR_TRIANGLE_RIGHT.a = TRIANGLE_COLOR_ALPHA;
        COLOR_TRIANGLE_OTHER.a = TRIANGLE_COLOR_ALPHA;
        COLOR_TRIANGLE_MIDDLE.a = TRIANGLE_COLOR_ALPHA;

//        renderTriangles(baseTriangle, MAX_DEPTH, Color.BLACK);
//        renderTriangles(baseTriangle, MAX_DEPTH, Color.WHITE);
        renderTriangle(baseTriangle, Color.BLACK);
        renderTriangles(baseTriangle.getTriangleMiddle(), MAX_DEPTH, Color.WHITE);
//        renderTrianglesIterative(Color.WHITE);
//        renderTriangleEdges(baseTriangle, MAX_DEPTH, Color.WHITE);

//        renderTriangle(baseTriangle);
//
//        renderTriangle(baseTriangle.getTriangleMiddle());
//
//        renderer.setColor(Color.RED);
//        renderTriangle(baseTriangle.getTriangleLeft());
//
//        renderer.setColor(Color.GREEN);
//        renderTriangle(baseTriangle.getTriangleRight());
//
//        renderer.setColor(Color.BLUE);
//        renderTriangle(baseTriangle.getTriangleOther());


        renderer.end();


	}

    private void calcTriangles(Triangle t, int steps){
        if(steps == 0)
            return;
        t.calculateInnerTriangles();

        Triangle left = t.getTriangleLeft();
        Triangle right = t.getTriangleRight();
        Triangle other = t.getTriangleOther();
        Triangle middle = t.getTriangleMiddle();

        calcTriangles(left, steps-1);
        calcTriangles(right, steps-1);
        calcTriangles(other, steps-1);
        calcTriangles(middle, steps-1);
    }

    private void calcTrianglesIterative(Triangle baseTriangle, int steps){

        ArrayList<Triangle> currentList;
        ArrayList<Triangle> lastList = null;

        for(int i = 0; i < MAX_DEPTH; i++){

            currentList = new ArrayList<>();

            if(i == 0){
                currentList.add(baseTriangle);
            }else{
                for(Triangle t : lastList){
                    t.calculateInnerTriangles();
                    currentList.add(t.getTriangleLeft());
                    currentList.add(t.getTriangleRight());
                    currentList.add(t.getTriangleOther());
                    currentList.add(t.getTriangleMiddle());
                }
            }

            triangles.add(currentList);
            lastList = currentList;

        }

    }

    private void renderTrianglesIterative(Color color){

//        float alphaStep = (1f/MAX_DEPTH) * TRIANGLE_COLOR_ALPHA;
        float alphaStep = 0f;
        for(List<Triangle> list : triangles){
            for(Triangle t : list){
                switch(t.getTrianglePosition()){
                    case LEFT:
//                        renderTriangle(t, COLOR_TRIANGLE_LEFT);
                        break;
                    case RIGHT:
//                        renderTriangle(t, COLOR_TRIANGLE_RIGHT);
                        break;
                    case OTHER:
//                        renderTriangle(t, COLOR_TRIANGLE_OTHER);
                        break;
                    case MIDDLE:
                        renderTriangle(t, COLOR_TRIANGLE_MIDDLE);
                        break;
                }
            }
            COLOR_TRIANGLE_LEFT.a -= alphaStep;
            COLOR_TRIANGLE_RIGHT.a -= alphaStep;
            COLOR_TRIANGLE_OTHER.a -= alphaStep;
            COLOR_TRIANGLE_MIDDLE.a -= alphaStep;
        }

    }

    private void renderTriangles(Triangle t, int steps, Color color){
        if(steps == 0)
            return;

//        renderTriangle(t, color);
        float red = 1f - baseTriangle.getPointLeft().getDistanceTo(t.getPointCenter());
        float green = 1f - baseTriangle.getPointRight().getDistanceTo(t.getPointCenter());
        float blue = 1f - baseTriangle.getPointOther().getDistanceTo(t.getPointCenter());
        Color c = new Color(red, green, blue, 1.0f);
        renderTriangle(t, c);
//        color.a = 1f - (MAX_DEPTH-steps)*(1f/MAX_DEPTH);
//        color.a = ((float)steps/MAX_DEPTH);
//        System.out.println("steps: " + steps + ", a: " + color.a);

//        if(color.equals(Color.BLACK))
//            color = Color.WHITE;
//        else
//            color = Color.BLACK;

        if(t.hasChildren()){
//            renderTriangles(t.getTriangleLeft(), steps-1, COLOR_TRIANGLE_LEFT);
//            renderTriangles(t.getTriangleRight(), steps-1, COLOR_TRIANGLE_RIGHT);
//            renderTriangles(t.getTriangleOther(), steps-1, COLOR_TRIANGLE_OTHER);
//            renderTriangles(t.getTriangleMiddle(), steps-1, COLOR_TRIANGLE_MIDDLE);

            renderTriangles(t.getTriangleParent().getTriangleLeft().getTriangleMiddle(), steps-1, COLOR_TRIANGLE_LEFT);
            renderTriangles(t.getTriangleParent().getTriangleRight().getTriangleMiddle(), steps-1, COLOR_TRIANGLE_RIGHT);
            renderTriangles(t.getTriangleParent().getTriangleOther().getTriangleMiddle(), steps-1, COLOR_TRIANGLE_OTHER);

        }
    }

//    private void renderTriangleEdges(Triangle t, int steps, Color color){
//        if(steps == 0)
//            return;
//
//        renderTriangleEdge(t, color);
//
//        if(t.hasChildren()){
//            renderTriangleEdges(t.getTriangleLeft(), steps-1, color);
//            renderTriangleEdges(t.getTriangleRight(), steps-1, color);
//            renderTriangleEdges(t.getTriangleOther(), steps-1, color);
//            renderTriangleEdges(t.getTriangleMiddle(), steps-1, color);
//        }
//    }

//    private void renderTriangleEdge(Triangle t, Color color){
//        Point pl = t.getPointLeft();
//        Point pr = t.getPointRight();
//        Point po = t.getPointOther();
//
//        renderer.setColor(color);
//        renderer.line(pl.getScaledVector3(scaleFactor), pr.getScaledVector3(scaleFactor));
//        renderer.line(pl.getScaledVector3(scaleFactor), po.getScaledVector3(scaleFactor));
//        renderer.line(pr.getScaledVector3(scaleFactor), po.getScaledVector3(scaleFactor));
//    }

    private void renderTriangle(Triangle t, Color color){
        Point pl = t.getPointLeft();
        Point pr = t.getPointRight();
        Point po = t.getPointOther();

        renderer.setColor(color);
        renderer.triangle(pl.getX()*scaleFactor, pl.getY()*scaleFactor, pr.getX()*scaleFactor, pr.getY()*scaleFactor, po.getX()*scaleFactor, po.getY()*scaleFactor);

        renderer.setColor(Color.WHITE);
        renderer.line(pl.getScaledVector3(scaleFactor), pr.getScaledVector3(scaleFactor));
        renderer.line(pl.getScaledVector3(scaleFactor), po.getScaledVector3(scaleFactor));
        renderer.line(pr.getScaledVector3(scaleFactor), po.getScaledVector3(scaleFactor));
    }

    private Triangle calcInnerTriangle(Triangle outer){

        Point op1 = outer.getPointLeft();
        Point op2 = outer.getPointRight();
        Point op3 = outer.getPointOther();
//        Vector3 op1 = outer.getPointLeft();
//        Vector3 op2 = outer.getPointRight();
//        Vector3 op3 = outer.getPointOther();

//        float x1 = 0.5f * Math.abs(op1.getX() - op2.getX());
//        float x2 = 0.5f * Math.abs(op1.getX() - op3.getX());
//        float x3 = 0.5f * Math.abs(op2.getX() - op3.getX());
        float x1 = (op1.getX() + op2.getX()) / 2.0f;
        float x2 = (op1.getX() + op3.getX()) / 2.0f;
        float x3 = (op2.getX() + op3.getX()) / 2.0f;

//        float y1 = 0.5f * Math.abs(op1.getY() - op2.getY());
//        float y2 = 0.5f * Math.abs(op1.getY() - op3.getY());
//        float y3 = 0.5f * Math.abs(op2.getY() - op3.getY());
        float y1 = (op1.getY() + op2.getY()) / 2.0f;
        float y2 = (op1.getY() + op3.getY()) / 2.0f;
        float y3 = (op2.getY() + op3.getY()) / 2.0f;

//        float x1 = Math.abs(op1.x - op2.x);
//        float x2 = Math.abs(op1.x - op3.x);
//        float x3 = Math.abs(op2.x - op3.x);
//
//        float y1 = Math.abs(op1.y - op2.y);
//        float y2 = Math.abs(op1.y - op3.y);
//        float y3 = Math.abs(op2.y - op3.y);

        Triangle inner = new Triangle(x1, y1, x2, y2, x3, y3);
        return inner;
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
