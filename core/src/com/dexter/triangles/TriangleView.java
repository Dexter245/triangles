package com.dexter.triangles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public class TriangleView implements Disposable{

    private TriangleModel model;

    private Camera camera;
    private ShapeRenderer renderer;

    private Color triangleColor = new Color(0.0f, 0.8f, 0.8f, 1.0f);

    public TriangleView(TriangleModel model){
        this.model = model;

        camera = new OrthographicCamera(800, 600);
        renderer = new ShapeRenderer();
        renderer.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

    }

    public void render(){

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.begin(ShapeRenderer.ShapeType.Filled);

        renderTriangles(model.getBaseTriangle());

        renderer.end();
    }

    private void renderTriangles(Triangle t){

        float length = t.getPointLeft().dst(t.getPointRight());
        float alpha = length*20;
        alpha = MathUtils.clamp(alpha, 0f, 1f);
        triangleColor.a = alpha;
        renderTriangle(t, triangleColor);

        if(t.hasChildren()){
            renderTriangles(t.getTriangleLeft());
            renderTriangles(t.getTriangleRight());
            renderTriangles(t.getTriangleUp());
        }
    }

    private void renderTriangle(Triangle t, Color color){
        Vector2 pl = t.getPointLeft().cpy().scl(model.getScaleFactor());
        Vector2 pr = t.getPointRight().cpy().scl(model.getScaleFactor());
        Vector2 pb = t.getPointBottom().cpy().scl(model.getScaleFactor());

        renderer.setColor(color);
        renderer.triangle(pl.x, pl.y, pr.x, pr.y, pb.x, pb.y);

//        Color colorOutline = Color.WHITE;
//        colorOutline.a = 0.25f;
//        renderTriangleOutline(t, colorOutline);
    }

//    private void renderTriangleOutline(Triangle t, Color c){
//        Vector2 pl = t.getPointLeft().cpy().scl(model.getScaleFactor());
//        Vector2 pr = t.getPointRight().cpy().scl(model.getScaleFactor());
//        Vector2 pb = t.getPointBottom().cpy().scl(model.getScaleFactor());
//
//        renderer.setColor(c);
//        renderer.line(pl, pr);
//        renderer.line(pl, pb);
//        renderer.line(pr, pb);
//    }

    public void resize(int width, int height){

        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.position.set(width/2.0f, height/2.0f, 0);
        camera.update();
        renderer.setProjectionMatrix(camera.combined);

        model.setScaleFactor(Math.min(width, height));
        float aspectRatio = (float) width / height;
        if(aspectRatio >= 1f){
            model.setAreaWidth(aspectRatio);
            model.setAreaHeight(1.0f);
        } else{
            model.setAreaWidth(1.0f);
            model.setAreaHeight(1/aspectRatio);
        }

//        model.setScaleFactor(model.getScaleFactor()*0.25f);
//        camera.update();

    }


    @Override
    public void dispose() {
        renderer.dispose();
    }
}
