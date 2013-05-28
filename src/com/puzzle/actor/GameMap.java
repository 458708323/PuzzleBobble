package com.puzzle.actor;

import java.util.ArrayList;
import java.util.List;

import com.puzzle.MyGameSurfaceView;
import com.puzzle.actor.Bullet;

import android.R;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class GameMap extends GameActor {
	//��Ļ�߽�
	public static int GAME_AREA_TOP;
	public static int GAME_AREA_LEFT;
	public static int GAME_AREA_RIGHT;
	public static int GAME_AREA_BOTTOM;
	
	int s, score;
	
	public List<Position> positions;
	GameActor bing;
	
	
	
	public GameMap(String name) {
		super(name);
		GAME_AREA_TOP = MyGameSurfaceView.screenH / 16 + Bullet.radius;
		GAME_AREA_BOTTOM = MyGameSurfaceView.screenH * 19 / 24 - Bullet.radius;
		GAME_AREA_LEFT = MyGameSurfaceView.screenW * 1 / 16 + Bullet.radius;
		GAME_AREA_RIGHT = MyGameSurfaceView.screenW * 29 / 32 - Bullet.radius;
		
		s = 0;
		positions = new ArrayList<Position>();
		bing = new GameActor("bing");
		for(int i = 0; GAME_AREA_TOP + Bullet.radius * 1.732 * i < GAME_AREA_BOTTOM + Bullet.radius * 2; i++){
			for(int j = 0; ; j++) {
				Position p = new Position((GAME_AREA_LEFT + Bullet.radius * 2 * j), (float) (GAME_AREA_TOP + Bullet.radius * Math.sqrt(3) * i));
				p.x += (i % 2 == 0) ? 0 : Bullet.radius;
				if(p.getX() < GAME_AREA_RIGHT)
					positions.add(p);
				else
					break;
			}
		}
		
		addChild(new Bullet(new Position(positions.get(0).x, positions.get(0).y), 0, "0"));
		addChild(new Bullet(new Position(positions.get(1).x, positions.get(1).y), 0, "0"));
		
		paint = new Paint();
		paint.setTextSize(15);
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.STROKE);
	}
	
	public void logic(long elapsedTime) {
		bing.logic(elapsedTime);
	}
	
	public void myDraw(Canvas canvas) {
		super.myDraw(canvas);
		bing.myDraw(canvas);
		
		//��ʾ����С���
		for(Position p : positions)
			canvas.drawCircle(p.getX(), p.getY(), 1, paint);
		
		canvas.drawText("Score��" + score, 0, MyGameSurfaceView.screenH * 89 / 96, paint);
	}
	
	public void addChild(Bullet bullet) {
		
		Position nearly = positions.get(0);
		//�ҵ���positions����͵�ǰbullet��psoition�����position
		for(Position position : positions) {
			if(bullet.position.distance(position) < bullet.position.distance(nearly)) {
				boolean flag = true;
				for(GameActor actor : children) {
					if(position.equals(((Bullet) actor).position)) {
						flag = false;
						Log.i("GameMap-addChild", "good, i find a can't set position and continue it.");
					}
				}
				if(flag) {
					nearly = position;
				}
			}
		}
		
		
		bullet.setPosition(new Position(nearly.getX(), nearly.getY()));		//�˴�Ҫ�½�һ���Է�ֹ�ѵ�ͼ�����position����
		bullet.name = s++ + "-" + bullet.name + "-" + bullet.type;
		super.addChild(bullet);
		
		//Ϊactor�Լ��������ڵ���������ٽӱ�
		for(GameActor actor : this.children) {
			if(bullet.position.distance(((Bullet) actor).position) < Bullet.radius * 3) {
				if(actor != bullet) {
					bullet.addArc(new ArcNode((Bullet) actor));
					((Bullet) actor).addArc(new ArcNode(bullet));
					Log.i("GameMap.addChile", ((Bullet) actor).showArcLink());
				}
			}
		}
		Log.i("GameMap.addChile", bullet.showArcLink());
		
		bing.children.clear();
		findSameColor(bullet);	//����actor��ͬɫ�����ݷŵ�bing����
		checkBing();		//��gameMap�����bing�ڵ�����ɾ�� ������������ ʹ֮����
	}
	
	void findSameColor(Bullet bullet) {
		bing.addChild(bullet);
		
		for(GameActor actor : this.children) {
			if(bullet.position.distance(((Bullet) actor).position) < Bullet.radius *  3 && ((Bullet) bullet).getType() == ((Bullet) actor).getType()) {
				if(!bing.children.contains(actor))
					findSameColor((Bullet) actor);
			}
		}
	}
	
	void checkBing() {
		int countBing = bing.children.size();
		
		if(countBing > 2) {
			score += countBing;		//���ӻ���
			for(GameActor bingActor : bing.children) {
				ArcNode arcNode = ((Bullet) bingActor).nextArc;
				while(arcNode != null) {
					//��ͨ����ȥ����һ�����ݵĻ���ɾ��
					arcNode.bullet.deleteArc(new ArcNode((Bullet) bingActor));
					arcNode = arcNode.nextArc();
				}
				((Bullet) bingActor).direction = Bullet.DIRECTION_DOWN;
				this.children.remove(bingActor);
			}
		}else
			bing.children.clear();		//���û�дչ�������ֱ�����bing��������
		
		
		//֮����Ҫ�ٴ�ѭ�� ����Ϊ���ڲ���gameMap�а�������������ȥ���ݵĻ�ɾ���� ���ܹ���ɾ���߼������� ������������		GameActor bing2 = new GameActor("bing2");
		for(int i = 0; i < bing.children.size(); i++) {
			GameActor bingActor = bing.children.get(i);
			ArcNode arcNode = ((Bullet) bingActor).nextArc;
			Log.i("Bing a actor", ((Bullet) bingActor).showArcLink());
			while(arcNode != null) {
				Log.i("GameMap.checkBing", arcNode.bullet.showArcLink());
				if(!hasARoot(arcNode.bullet, new ArrayList<Bullet>())) {
					Log.i("GameMap-chechBing", "[" + arcNode.bullet.name + "]" + " no root");
					arcNode.bullet.direction = Bullet.DIRECTION_DOWN;
					if(!bing.children.contains(arcNode.bullet))		//���������ݲ���bing�����ټӽ��� ��Ȼ����Զѭ��
						bing.addChild(arcNode.bullet);
					this.children.remove(arcNode.bullet);
					//���arcNode.bullet�����Ѿ��ǹ����������� Ҫ������Χ�����ݵ�ͨ�����Ļ�ɾ��
					ArcNode arc = arcNode.bullet.nextArc;
					while(arc != null) {
						//��ͨ����ȥ����һ�����ݵĻ���ɾ��
						arc.bullet.deleteArc(new ArcNode(arcNode.bullet));
						arc = arc.nextArc();
					}
				}
				arcNode = arcNode.nextArc();
			}
		}
		
		for(GameActor actor : this.children) 
			Log.i("checkBing final", ((Bullet) actor).showArcLink());
		
	}
	
	boolean hasARoot(Bullet bullet, List<Bullet> visted) {		//�ж����������û�и� ���Ǳ�׼�ݹ�
		if(bullet.position.y == positions.get(0).y)
			return true;
		
		ArcNode arcNode = bullet.nextArc;
		while(arcNode != null) {
			if(!visted.contains(arcNode.bullet)) {
				visted.add(arcNode.bullet);
				if(hasARoot(arcNode.bullet, visted))
					return true;
			}
			arcNode = arcNode.nextArc();
		}
		
		return false;
	}
	
	public void reSet() {
		children.clear();
		s = 0;
		score = 0;
		addChild(new Bullet(new Position(positions.get(0).x, positions.get(0).y), 0, "0"));
		addChild(new Bullet(new Position(positions.get(1).x, positions.get(1).y), 0, "0"));
	}
	
	public List<GameActor> getChildren() {
		return children;
	}
	
}
