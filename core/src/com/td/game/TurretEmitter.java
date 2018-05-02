package com.td.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TurretEmitter {
    public class TurretTemplate {
        private int type;
        private int level;
        private int imageIndex;
        private int cost;
        private int damage;
        private int radius;
        private float fireRate;

        public int getType() {
            return type;
        }

        public int getLevel() {
            return level;
        }

        public int getImageIndex() {
            return imageIndex;
        }

        public int getCost() {
            return cost;
        }

        public int getDamage() {
            return damage;
        }

        public int getRadius() {
            return radius;
        }

        public float getFireRate() {
            return fireRate;
        }

        // #image_index cost fire_rate damage radius
        public TurretTemplate(String line) {
            String[] tokens = line.split("\\s");
            type = Integer.parseInt(tokens[0]);
            level = Integer.parseInt(tokens[1]);
            imageIndex = Integer.parseInt(tokens[2]);
            cost = Integer.parseInt(tokens[3]);
            fireRate = Float.parseFloat(tokens[4]);
            damage = Integer.parseInt(tokens[5]);
            radius = Integer.parseInt(tokens[6]);
        }

    }

    private GameScreen gameScreen;
    private TextureAtlas atlas;
    private Map map;
    private Turret[] turrets;
    private TurretTemplate[] templates;

    public TurretEmitter(TextureAtlas atlas, GameScreen gameScreen, Map map) {
        this.loadTurretData();
        this.gameScreen = gameScreen;
        this.map = map;
        this.atlas = atlas;
        this.turrets = new Turret[20];
        TextureRegion[] regions = new TextureRegion(atlas.findRegion("turrets")).split(80, 80)[0];
        for (int i = 0; i < turrets.length; i++) {
            turrets[i] = new Turret(regions, gameScreen, map, 0, 0);
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < turrets.length; i++) {
            if (turrets[i].isActive()) {
                turrets[i].render(batch);
            }
        }
    }

    public void update(float dt) {
        for (int i = 0; i < turrets.length; i++) {
            if (turrets[i].isActive()) {
                turrets[i].update(dt);
            }
        }
    }

    public int getTurretCost(int type, int level) {
        for (int i = 0; i < templates.length; i++) {
            if(templates[i].type == type && templates[i].level == level)
                return templates[i].cost;
        }
        return -1;
    }

    public TurretTemplate getTurretTemplateUp(int cellX, int cellY) {
        TurretTemplate curTemplate = null;
        for (int i = 0; i < turrets.length; i++) {
            if(turrets[i].isActive() && turrets[i].getCellX() == cellX && turrets[i].getCellY() == cellY)
                curTemplate = turrets[i].getTemplate();
        }
        if(curTemplate != null){
            for (int i = 0; i < templates.length; i++) {
                if(templates[i].type == curTemplate.type && templates[i].level == curTemplate.level + 1)
                    return templates[i];
            }
        }
        return null;
    }

    public TurretTemplate getTurretTemplate(int type, int level) {
            for (int i = 0; i < templates.length; i++) {
                if(templates[i].type == type && templates[i].level == level)
                    return templates[i];
            }
        return null;
    }

    public void upTurret(TurretTemplate template, int cellX, int cellY) {

            for (int i = 0; i < turrets.length; i++) {
                if (turrets[i].isActive() && turrets[i].getCellX() == cellX && turrets[i].getCellY() == cellY) {
                    turrets[i].deactivate();
                }
            }
            for (int i = 0; i < turrets.length; i++) {
                if (!turrets[i].isActive()) {
                    turrets[i].activate(template, cellX, cellY);
                    break;
                }
            }

    }

    public void setTurret(int type, int level, int cellX, int cellY) {
        if (map.isCellEmpty(cellX, cellY)) {
            for (int i = 0; i < turrets.length; i++) {
                if (turrets[i].isActive() && turrets[i].getCellX() == cellX && turrets[i].getCellY() == cellY) {
                    return;
                }
            }
            TurretTemplate template = getTurretTemplate(type, level);
            if (template != null) {
                for (int i = 0; i < turrets.length; i++) {
                    if (!turrets[i].isActive()) {
                        turrets[i].activate(template, cellX, cellY);
                        break;
                    }
                }
            }
        }
    }

    public void destroyTurret(int cellX, int cellY) {
        for (int i = 0; i < turrets.length; i++) {
            if (turrets[i].isActive() && turrets[i].getCellX() == cellX && turrets[i].getCellY() == cellY) {
                turrets[i].deactivate();
            }
        }
    }

    public void loadTurretData() {
        BufferedReader br = null;
        List<String> lines = new ArrayList<String>();
        try {
            br = Gdx.files.internal("turrets.dat").reader(8192);
            String str;
            while ((str = br.readLine()) != null) {
                lines.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        templates = new TurretTemplate[lines.size() - 1];
        for (int i = 1; i < lines.size(); i++) {
            templates[i - 1] = new TurretTemplate(lines.get(i));
        }
    }

}

