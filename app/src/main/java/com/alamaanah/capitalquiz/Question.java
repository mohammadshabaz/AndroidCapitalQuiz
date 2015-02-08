package com.alamaanah.capitalquiz;

public class Question {
	int qId;
	String qText, op1, op2, op3, op4;
	int answerIndex;
	String type;
	int level;
	
	public Question(int qId, String qText, String op1, String op2, String op3, String op4, int answerIndex, String type, int level){
		this.qId = qId;
		this.qText = qText;
		this.op1 = op1;
		this.op2 = op2;
		this.op3 = op3;
		this.op4 = op4;
		this.answerIndex = answerIndex;
		this.type = type;
		this.level = level;
	}
}
