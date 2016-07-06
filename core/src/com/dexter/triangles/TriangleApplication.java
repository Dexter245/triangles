package com.dexter.triangles;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

public class TriangleApplication extends ApplicationAdapter {

    TriangleModel model;
    TriangleController controller;
    TriangleView view;

    @Override
    public void create() {

        model = new TriangleModel();
        controller = new TriangleController(model);
        view = new TriangleView(model);

    }

    @Override
    public void render() {

        long timeStart = System.currentTimeMillis();
        controller.update(Gdx.graphics.getDeltaTime());
        model.setUpdateTime(System.currentTimeMillis() - timeStart);

        timeStart = System.currentTimeMillis();
        view.render();
        model.setRenderTime(System.currentTimeMillis() - timeStart);

    }

    @Override
    public void resize(int width, int height) {
        view.resize(width, height);
    }

    @Override
    public void dispose() {
        view.dispose();
    }
}
