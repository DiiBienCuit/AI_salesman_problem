package com.trajet;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;
import java.lang.*;
import java.io.*; 

//class of each path
public class Individu {
	
		private int[] gene;
		private double score;
		private double distance;
		
		private int villeDepart;
		private int nb_ville;
		private int matrix_distance[][];
		
		//constructor
		public Individu(int size, int villeDepart, int matrix_distance[][]) {
			
			if (size <= 0 || villeDepart <= 0) {
				return;
			}
			size ++;
			gene = new int[size];
			this.villeDepart = villeDepart;
			this.nb_ville = size;
			this.matrix_distance = matrix_distance;
		}
		
		//function to get random number
		public int getRandom(int min, int max) {
			int r = (int) (Math.random() * (max - min)) + min;
			return r;
		}
		
		//function to set the gene of this individu
		public int[] setGene(){
			this.score = 0;
			ArrayList<Integer> route = new ArrayList<Integer>();
			
			for (int i=0;i< this.nb_ville;i++)
			{
				route.add(i+1);
			}
			
			this.gene[0] = this.villeDepart;
			this.gene[nb_ville-1] = this.villeDepart;
			route.remove(this.villeDepart-1);
			
			//Get randomly the next city to pass by 
			for(int i = 1;i < this.nb_ville-1;i++) {
				int random_nb = getRandom(0, route.size()-1);
				this.gene[i] = route.get(random_nb);
				route.remove(route.get(random_nb));
				//System.out.println(route_local.size());
			}
			this.calculateDistance();
			return this.gene;
		}
		
		public void setScore(){
			this.score = 1/this.distance;
		}
		
		public void setGeneDirect(int[] gene) {
			this.gene = gene;
			//this.calculateDistance();
		}
		
		public void setScoreDirect(double score) {
			this.score = score;
		}
		
		public void setDistanceDirect(double distance) {
			this.distance = distance;
		}
		
		public void printGene(){
			System.out.print("Chemin: ");
			for(int i = 0; i< this.nb_ville;i++){
				System.out.print(this.gene[i]+" ");
			}
		}

		public double calculateDistance(){
			this.distance = 0;
			
			for(int i=0; i< this.nb_ville-1; i++) {
				this.distance += this.matrix_distance[this.gene[i]-1][this.gene[i+1]-1];
		}
			//System.out.println(". Sa distance est: "+this.distance);
			this.setScore();
			return this.distance;
		}
		
		public double getScore() {
			return this.score;
		}
		public double getDistance() {
			return this.distance;
		}
		public int[] getGene() {
			return this.gene;
		}
		
}
