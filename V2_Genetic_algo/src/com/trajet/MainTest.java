package com.trajet;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Scanner;
import java.lang.*;
import java.io.*; 

public class MainTest{
	
	//function to get random number
	static int getRandom(int min, int max) {
		int r = (int) (Math.random() * (max - min)) + min;
		return r;
	}
	
	public static void main(String args[]) {
		
		//initialization of the distances between cities
		int nb_ville = 0;
		int ville_input = 0;
		int distance_min = 99999999;
		int distance_local = 0;
		ArrayList<Integer> route = new ArrayList<Integer>();
		ArrayList<Integer> route_local = new ArrayList<Integer>();
		
		System.out.println("[V2.0] Probleme de voyager");
		System.out.println("----------------------------------------");
		
		//Ask for the number of cities
		do {
			System.out.println("Entrez le nombre de villes:(un entier!!!)");
			Scanner sc = new Scanner(System.in);
			nb_ville = sc.nextInt();
		}while(nb_ville <= 0);
		
		//initialization of the distance matrix
		int matrix_distance[][] = new int[nb_ville][nb_ville];
		
		//populate the table
		for (int i=0;i< nb_ville;i++)
		{
			route.add(i+1);
			matrix_distance[i][i] = 9999999;
			for (int j=i+1;j< nb_ville;j++) 
			{
					//System.out.println("Entrez la distance entre ville "+(i+1)+" et la ville "+(1+j));
					//Scanner sc = new Scanner(System.in);
					int random_nb = (int)(Math.random() * 200 + 1);
					matrix_distance[i][j] = random_nb;
					//System.out.println(random_nb);
					matrix_distance[j][i] = matrix_distance[i][j];
			}
		}
		
		//print the distance matrix
		System.out.println("La matrice:");
		System.out.print("\t");
		for(int i= 0;i<nb_ville;i++){
			System.out.print("ville "+(i+1)+"\t");
		}
		System.out.print("\n");
		for (int i=0;i< nb_ville;i++)
		{
			System.out.print("ville "+(i+1)+"\t");
			//System.out.println(route.get(i));
			for (int j=0;j<nb_ville;j++) 
			{
				
				System.out.print(matrix_distance[i][j]+"\t"); 
			}
			System.out.print("\n"); 
		}
		
		//Ask for the city of departure
		do{
			System.out.println("Entrez la ville de depart:\n");
			Scanner sc = new Scanner(System.in);
			ville_input = sc.nextInt();
		}while(ville_input<=0);
		
		//generate new generations!
		GA generation = new GA(nb_ville,ville_input,matrix_distance); 
		generation.initPopulation();

	}
	
}