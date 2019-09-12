package com.robercival.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.Random;


public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] birds;
	int flapState = 0;
	int pause = 0;



	//for move an fly
	float birdY = 0;
	float velocity = 0;
	int gameState = 0;
	float gravity = 1;


	//for pipes
	Texture topTube;
	Texture bottomTube;
	float gap = 400;



	//moveTube up down
	float maxTubeOffset;
	Random randomGenerator;




	//moveTube to the left
	float tubeVelocity = 4;
	int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;



	//bird mark
	Circle birdCircle;
	ShapeRenderer shapeRenderer ;


	//Tube mark
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;



	//for score
	int score = 0;
	int scoreTube = 0;
	BitmapFont font;


	//for gameover
	Texture gameover;



	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		//shapeRenderer = new ShapeRenderer();
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");




		//for bird mark
		birdCircle = new Circle();
		shapeRenderer = new ShapeRenderer();






		//Tube
		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");



		//moveTube up down
		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
		randomGenerator = new Random();


		//move Tube to the left
		distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4;

		//for Tube mark
		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];





		//for score
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		//for game over
		gameover = new Texture("flappybird_fulmer.png");


		startGame();




	}




	public void startGame(){
		//bird back to  center
		birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;
		for (int i = 0; i < numberOfTubes; i++) {

			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;

			//for Tube mark
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();

		}
	}






	@Override
	public void render () {

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1) {  //game is active



//for score
			if(tubeX[scoreTube] < Gdx.graphics.getWidth() / 2){
				score ++;
				if(scoreTube <numberOfTubes - 1){
					scoreTube ++;
					Gdx.app.log("Score",String.valueOf(score));
				}else{
					scoreTube = 0;
				}
			}





			if (Gdx.input.justTouched()) {

				velocity = -20;

			}
//for tube and movement

			for (int i = 0; i < numberOfTubes; i++) {

				if (tubeX[i] < - topTube.getWidth()) {

					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

				} else {

					tubeX[i] = tubeX[i] - tubeVelocity;


				}




//for Tube
				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);
//for mark Tube
				topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
			}


//bird
			if (birdY > 0 /*|| velocity < 0*/) {

				velocity = velocity + gravity;
				birdY -= velocity;

			}else{
				gameState = 2;
			}

		} else if (gameState == 0) {

			if (Gdx.input.justTouched()) {

				gameState = 1;

			}

		}else if (gameState == 2){
			batch.draw(gameover,Gdx.graphics.getWidth() /2 - gameover.getWidth()/2,Gdx.graphics.getHeight() /2 - gameover.getHeight()/2);

				if (Gdx.input.justTouched()) {
					gameState = 1;
					startGame();
					score = 0;
					scoreTube = 0;
					velocity = 0;

				}
		}

		if (pause < 8) {
			pause++;
		} else {
			pause = 0;
			if (flapState == 0) {
				flapState = 1;
			} else {
				flapState = 0;
			}

		}



		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);
//display the score
		font.draw(batch, String.valueOf(score),100,200);
//bird mark circle
		birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);









		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		//shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

		for (int i = 0; i < numberOfTubes; i++) {

			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());


			if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {

				gameState = 2;

			}

		}


		batch.end();
		//shapeRenderer.end();



	}
}
