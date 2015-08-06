package tictactoe;

public class ResultMM {
	
	String[] matrix;
	int score;
	int depth;
	
	public ResultMM(String[] matrix, int score, int depth)
	{
		this.matrix = matrix;
		this.score = score;
		this.depth = depth;
	}
	
	public void updateMatrix(String[] matrix)
	{
		this.matrix = matrix;
	}
	
	public int getScore()
	{
		return score;
	}
	public int getIntrus()
	{
		for(int i=0; i<9; i++)
			if(matrix[i].equals("o"))
				return i;
		return -1;
	}
}
